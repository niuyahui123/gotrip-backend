package com.ytzl.gotrip.rpc.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.ytzl.gotrip.rpc.api.RpcSendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;



@Component
@Service(interfaceClass=RpcSendMailService.class)
public class RpcSendMailServiceImpl implements RpcSendMailService {
    //发送邮件
    @Autowired
    private JavaMailSender mailSender;

    //邮箱发件人
    private String from="niu_ya_hui@163.com";


    @Override
    public void sendMail(String to, String subject, String verifyCode) {
        //创建邮箱正文

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(verifyCode);
        try {
            mailSender.send(message);
            //logger.info("简单邮件已经发送。");
        } catch (Exception e) {
           //logger.error("发送简单邮件时发生异常！", e);
        }
    }
}
