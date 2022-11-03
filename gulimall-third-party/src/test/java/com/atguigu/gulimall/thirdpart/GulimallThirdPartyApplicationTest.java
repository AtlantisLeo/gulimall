package com.atguigu.gulimall.thirdpart;

import com.aliyun.oss.OSS;

import com.atguigu.gulimall.thirdpart.component.SmsComponent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.FileNotFoundException;
import java.util.UUID;


@SpringBootTest
public class GulimallThirdPartyApplicationTest {
    @Autowired
    OSS ossClient;
    @Autowired
    SmsComponent smsComponent;

    @Test
    public void testUpload(){
        String code = UUID.randomUUID().toString().substring(0, 5);
        smsComponent.sendSmsCode("13889912370",code);
    }
 
}