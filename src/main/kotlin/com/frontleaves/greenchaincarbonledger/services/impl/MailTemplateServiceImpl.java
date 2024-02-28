package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.services.MailTemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;

/**
 * MailTemplateServiceImpl
 * <hr/>
 * 用于邮件模板服务的实现类，用于发送邮件，接收邮件等，提供邮件服务
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.MailTemplateService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailTemplateServiceImpl implements MailTemplateService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendMail(@NotNull String email, @NotNull HashMap<String, Object> prepareData, @NotNull String template) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            Context context = new Context();
            context.setVariables(prepareData);
            String emailContent = templateEngine.process(template, context);
            this.mineMessageSender(message, email, prepareData, emailContent);
        } catch (MessagingException e) {
            log.error("\t> 发送邮件发送失败", e);
        }
    }

    @Override
    public void mailSendCode(@NotNull String email, @NotNull String code, @NotNull String template) {

    }

    @Override
    public void mailSend(@NotNull String email, @NotNull HashMap<String, Object> data, @NotNull String template) {

    }

    /**
     * mineMessageSender
     * <hr/>
     * 发送邮件的方法, 用于发送邮件, 通过MimeMessageHelper发送邮件
     *
     * @param message      邮件消息
     * @param email        邮件地址
     * @param prepareData  准备数据
     * @param emailContent 邮件内容
     */
    private void mineMessageSender(MimeMessage message, String email, @NotNull HashMap<String, Object> prepareData, String emailContent) throws MessagingException {
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(prepareData.get("title").toString());
        mimeMessageHelper.setText(emailContent, true);
        javaMailSender.send(message);
        log.info("\t> 发送邮件 {} 标题 {} 成功", email, prepareData.get("title").toString());
    }
}
