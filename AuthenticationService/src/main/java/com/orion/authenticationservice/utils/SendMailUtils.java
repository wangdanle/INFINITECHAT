package com.orion.authenticationservice.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * @Author: Orion
 * @CreateTime: 2025/5/21
 * @Description:
 */
@Configuration
public class SendMailUtils {
    @Autowired
    private Environment env;

    /**
     *
     * @param targetEmail
     * @param authCode
     */
    public void sendEmailCode(String targetEmail, String authCode) {
        try {
            // 设置TLS协议
            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
            // 创建邮箱对象
            SimpleEmail mail = new SimpleEmail();
            // 设置发送邮件的服务器
            mail.setHostName(env.getProperty("app.infinitechat.email.host"));
            // "你的邮箱号"+ "上文开启SMTP获得的授权码"
            mail.setAuthentication(env.getProperty("app.infinitechat.email.username"), env.getProperty("app.infinitechat.email.password"));
            // 发送邮件 "你的邮箱号"+"发送时用的昵称"
            mail.setFrom(env.getProperty("app.infinitechat.email.username"), env.getProperty("app.infinitechat.email.nikename"));
            // 使用安全链接
            mail.setSSLOnConnect(true);
            // 接收用户的邮箱
            mail.addTo(targetEmail);
            // 邮件的主题(标题)
            mail.setSubject("注册验证码");
            // 邮件的内容
            mail.setMsg("您的验证码为:" + authCode + "(1分钟内有效)");
            // 发送
            mail.send();
        } catch (EmailException e){
            e.printStackTrace();
        }
    }
}