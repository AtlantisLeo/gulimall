package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author HZM
 * @email xxxxx@qq.com
 * @date 2022-10-01 22:22:45
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
