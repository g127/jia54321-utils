package com.jia54321.utils;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.sun.mail.util.MailSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.PasswordAuthentication;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class MailTest {
    static final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    public static void sendMail() throws Exception {

        Properties props = new Properties();

        // 开启debug调试
        props.setProperty("mail.debug", "true");
        // 发送服务器需要身份验证
        props.setProperty("mail.smtp.auth", "true");
        // 端口号
        props.setProperty("mail.smtp.port", "465");
        // 设置邮件服务器主机名
        props.setProperty("mail.host", "smtp.qq.com");
        // 发送邮件协议名称
        props.setProperty("mail.transport.protocol", "smtp");

        props.setProperty("mail.smtp.from", "525260657@qq.com");

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getInstance(props);

        Message msg = new MimeMessage(session);
        msg.setSubject("主题");
        StringBuilder builder = new StringBuilder();
        builder.append("胡子&小猿的博客:");
        builder.append("url = " + "http://www.cnblogs.com/hzxy-blog/");
        msg.setText(builder.toString());
        //msg.setFrom(new InternetAddress("525260657@qq.com"));

        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "525260657@qq.com", "rgnsemlxvnigbjdb");

        transport.sendMessage(msg, new Address[]{new InternetAddress("guogang@topflames.com")});
        transport.close();
    }
}
