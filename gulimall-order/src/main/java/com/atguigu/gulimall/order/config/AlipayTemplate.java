package com.atguigu.gulimall.order.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.atguigu.gulimall.order.vo.PayVo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "alipay")
@Component
@Data
public class AlipayTemplate {

    //在支付宝创建的应用的id
    private   String app_id = "2021000120616338";

    // 商户私钥，您的PKCS8格式RSA2私钥
    private  String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDFX0DIYashKIh89RmL5zhgswnpYbMXWVYiNxGM/0uPi82I+89y4xziW1L8yQcpz94OVvNw5SycdhOeWu58gBqOR9ACoUreN0vlFzBCQdmO1+6T+ESKbJbCYpawlaDwlAO6CG2qaWxAp8tsxOL/lKO7hhcSF5BlYIBLg7DRN93srERfuBIAsEuWz1CoD7FoARF1VICMf2+5LF0VWCIGI22A3wRfv2sIazZg8+CgXbSarxEafCgA4bnTCop59WQRbD/J8fbDyMHm3UaPWrVZbNM4YOEk91fLWPr1EV3EuwFzXTH6u6DZgkg5JyFYLvXVoSNXobufaIPI1k9NdWiqkYmNAgMBAAECggEAELbmV5Hd2isClAJ1rWFhZt8Cod8COaH5J2CefeBI19kxCL66bLJLCehN+NZVYvcViQVYDGF8C6v8MVbmB2kZ1p7CQNbD7lmkdbwm/BVzhSNIMwO9WjOj+MY8mKuzO2xtfF2mW94dqsQK+CUcTCScUQ8j+QZB1jP76Wk3miAvDRHLfeZXFrsLFYtDmU9iDU3IKDQ4we6Q008XcQvCKzTlPGFpYlSfI1g9dreOT0WRtOL/uevMSz18d+TChCJsTaUtp+GX1zCasrur+q074DyD1f+jpFQuAmCLznh7PhPqvSxTczo1Lw0CYMC1Jd9YLnSwmyJ9NoE8TQtwDsWnTIidAQKBgQDuHoXqe5WvaEIxKR7fyA4RH9t03gA0CYZV59o+Vcx5KJrNubct1Hp6u8AxIwbpDyayWBsNu5CdFhzp6pP7Mkfm2CtwO8h7E5QLjWEsLZc6dS81+uxLz7TgGMUIkiOBhazNp0nGUDWdBu2vH2+5luHg+y4DMEHKflIkX/GvppOyIQKBgQDUMW20J70TKQAEQUVZ8SONBOtNRBGx3faHliSIf8QRh6d83UqALfj/l1Eoq79dMLlT7GBDLtFDmtSboSRRh1d8yByzsuC7RzrmBv4feX4jZNgbtqTQ0tcFCsev8rMjQcYienu0p6Q+Ep/CLXgy2Db0I6qYMTq0ksMIam/6OrOB7QKBgCMoBoWUMYlwTzWK6LAJjWoh1fjvPgBw15CfZq2FZWcOA60UGmN914d/aOTYUxDd18zFDVl9s+A9f49Zvvd7ePowOFmduf/R6C28dfhb0tQisRG7NmALDp13ir+1M+DPp6/FXzMUbJX+i+ErNwejqz3YV/bIjS4kcGRGdhyE75GBAoGAZHsLkfYHaN1PbrRh3Nd9mDeuHSaQooOTTF1i62hlNk/glhZMQyqB8LZ87h92PPDnscALTYUL/1GAQDoscpb+bM5uGQIpvUVbtLW2/LyRaL1GQMlafrB4taCHMuBgx99LreQGVGPWOLalBFR3yMvuKeoBzJPzUrL0vDV61Sma+3kCgYEA6iO6wrVxnCtb8dUFPBAeqJyuV8Ab4uEV5bI2IKvEzBn8hH3FrT2Xq33Oz3vCt7ipfg9etjtMF0qFXaGOnjt6P0UyMVwMu45mHFL0r8lsoeDQgRJc1QCBY1a3nuqhuQnLKFd16r3mXcFGtacVNRHvrH5/kgICnSpHQupDarRAZrM=";;
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private  String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyJdle/JEr6psylBhqcF0vwjt3l+mLzfTZprUrFlPW7I1Fx3mDb24Zo5CUKRLju28Aux9hTM+XxU+ogd//c4w7ifh4LzVrzmSKfAgPLEdaLhqTiFbleeJMlIRTmdQGyrlBhUP0XlPZNPTHGZkpNKq0A4RxdSn09FqnO5d9+8HW+7QTl9N9rTHr/SipUGEK2Ncro9IOfMWOVuOudBMI/2py1kW1rklLIe5W+YXd6X427HzeQNexYyPtHWvE6pbO6kclxDMbyZfYLgkjUJcXV5/OH1RPPJmF1QsTak166FuMsN7xt64HZj+egZXGLxKqASf23GIDgkZEp7Ek1KVP6W7hwIDAQAB";
    // 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
    private  String notify_url = "https://6x2260h738.goho.co/payed/notify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    //同步通知，支付成功，一般跳转到成功页
    private  String return_url = "http://member.gulimall.com/memberOrder.html";

    // 签名方式
    private  String sign_type = "RSA2";

    // 字符编码格式
    private  String charset = "utf-8";

    private String timeout = "5m";

    // 支付宝网关； https://openapi.alipaydev.com/gateway.do
    private  String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    public  String pay(PayVo vo) throws AlipayApiException {

        //AlipayClient alipayClient = new DefaultAlipayClient(AlipayTemplate.gatewayUrl, AlipayTemplate.app_id, AlipayTemplate.merchant_private_key, "json", AlipayTemplate.charset, AlipayTemplate.alipay_public_key, AlipayTemplate.sign_type);
        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id, merchant_private_key, "json",
                charset, alipay_public_key, sign_type);

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(return_url);
        alipayRequest.setNotifyUrl(notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = vo.getOut_trade_no();
        //付款金额，必填
        String total_amount = vo.getTotal_amount();
        //订单名称，必填
        String subject = vo.getSubject();
        //商品描述，可空
        String body = vo.getBody();

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"timeout_express\":\""+timeout+"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        System.out.println("支付宝的响应："+result);

        return result;

    }
}
