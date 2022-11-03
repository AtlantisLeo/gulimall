package com.atguigu.gulimall.thirdpart.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdpart.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {


    @Autowired
    SmsComponent smsComponent;


    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code")String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }
}
