package com.atguigu.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.mq.OrderTo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.R;

import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.feign.OrderFignService;
import com.atguigu.gulimall.ware.feign.ProductFeignService;

import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.OrderVo;
import com.atguigu.gulimall.ware.vo.SkuHasStockVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.rabbitmq.client.Channel;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private  WareSkuDao wareSkuDao;
    @Autowired
    private ProductFeignService productFeignService;
    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;
    @Autowired
    WareOrderTaskService orderTaskService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    OrderFignService orderFignService;

    @Transactional
    public void unLockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
        wareSkuDao.unLockStock(skuId,wareId,num);
        WareOrderTaskDetailEntity entity = new WareOrderTaskDetailEntity();
        entity.setId(taskDetailId);
        entity.setLockStatus(2);
        orderTaskDetailService.updateById(entity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        List<WareSkuEntity> skuEntities = wareSkuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (skuEntities.size() == 0 || skuEntities == null){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            try{
                R info = productFeignService.info(skuId);
                Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0){
                    String skuName = (String) skuInfo.get("skuName");
                    wareSkuEntity.setSkuName(skuName);
                }
            }catch (Exception e){
            }

            wareSkuDao.insert(wareSkuEntity);
        }else {
            wareSkuDao.addStock(skuId,wareId,skuNum);
        }

    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
           Long count = baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count==null?false:count>0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    @Transactional
    public Boolean orderLockStock(WareSkuLockVo vo) {
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);
        List<OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            Integer count = item.getCount();
            stock.setNum(count);
            stock.setSkuId(skuId);
            List<Long> wareIds= baseMapper.listWareIdHasStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock stock : collect) {
            Boolean skuStocked = false;
            Long skuId = stock.getSkuId();
            List<Long> wareIds = stock.getWareId();
            if (wareIds==null||wareIds.size()==0){
                throw new NoStockException(skuId);
            }
            for (Long wareId : wareIds) {
                Long count = wareSkuDao.locoSkuStock(skuId,wareId,stock.getNum());
                if (count == 1){
                    skuStocked=true;
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null,skuId,"",stock.getNum(),taskEntity.getId(),wareId,1);
                    orderTaskDetailService.save(detailEntity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity,detailTo);
                    lockedTo.setDetail(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;
                }else {
                }
            }
            if (skuStocked==false){
                throw  new NoStockException(skuId);
            }
        }
        return true;
    }

    @Override
    public void unlockStock(StockLockedTo to) {


        StockDetailTo detail = to.getDetail();
        Long detailId = detail.getId();
        //??????
        //1????????????????????????????????????????????????????????????
        //  ?????????????????????????????????
        //    ????????????????????????
        //          1????????????????????????????????????
        //          2??????????????????????????????????????????
        //                ??????????????? ????????????????????????
        //                          ????????????????????????
        //  ???????????????????????????????????????????????????????????????????????????
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if (byId != null) {
            //??????
            Long id = to.getId();
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();//????????????????????????????????????
            R r = orderFignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {
                //????????????????????????
                OrderVo data = r.getData(new TypeReference<OrderVo>() {
                });
                if (data == null || data.getStatus() == 4) {
                    //???????????????
                    //?????????????????????????????????????????????
                    //detailId
                    if (byId.getLockStatus() == 1) {
                        //????????????????????????????????????1 ???????????????????????????????????????
                        unLockStock(detail.getSkuId(), detail.getWareId(), detail.getSkuNum(), detailId);
                    }
                }
            } else {
                //???????????????????????????????????????????????????????????????????????????
                throw new RuntimeException("??????????????????");
            }

        } else {
            //????????????
        }

    }

    @Override
    public void unlockStock(OrderTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity task =  orderTaskService.getOrderTaskByOrderById(orderSn);
        Long id = task.getId();
        //??????????????????????????? ????????????????????????????????????
        List<WareOrderTaskDetailEntity> entities = orderTaskDetailService.list(
                new QueryWrapper<WareOrderTaskDetailEntity>()
                        .eq("task_id", id)
                        .eq("lock_status", 1));

        //Long skuId, Long wareId, Integer num, Long taskDetailId
        for (WareOrderTaskDetailEntity entity : entities) {
            unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }

    }

    @Data
    class SkuWareHasStock{
        private Long SkuId;
        private Integer num;
        private List<Long> wareId;
    }

}