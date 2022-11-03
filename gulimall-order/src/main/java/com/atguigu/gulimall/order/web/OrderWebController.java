package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();

        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes attributes){
       SubmitOrderResponseVo responseVo =  orderService.submitOrder(vo);
       if (responseVo.getCode()==0){
           model.addAttribute("submitOrderResp",responseVo);
            return "pay";
       }else {
           String msg= "下单失败：";
           switch (responseVo.getCode()){
               case 1: msg += "订单信息过期,请重试";break;
               case 2: msg += "订单商品价格发生变化,请重试";break;
               case 3: msg+="库存锁定失败，商品库存不足"; break;
           }
           attributes.addFlashAttribute("msg",msg);
           return "redirect:http://order.gulimall.com/toTrade";
       }
    }
}
