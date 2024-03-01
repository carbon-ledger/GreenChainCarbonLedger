package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.VerifyCodeDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.VerifyCodeDO;
import com.frontleaves.greenchaincarbonledger.services.MailService;
import com.frontleaves.greenchaincarbonledger.services.MailTemplateService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * MailServiceImpl
 * <hr/>
 * 用于邮件服务的实现类，用于发送邮件，接收邮件等，提供邮件服务
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @see com.frontleaves.greenchaincarbonledger.services.MailService
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final VerifyCodeDAO verifyCodeDAO;
    private final MailTemplateService mailTemplateService;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> sendMailByCode(long timestamp, @NotNull HttpServletRequest request, @NotNull String email, @NotNull String template) {
        log.info("[Service] 执行 sendMailByCode 方法");
        // 检查UserAgent与UserIp是否存在
        log.debug("\t> 检查UserAgent与UserIp是否存在");
        log.debug("\t\t> User-Agent: {} | User-IP: {}", request.getHeader("User-Agent"), request.getRemoteAddr());
        if (!request.getHeader("User-Agent").isBlank() && !request.getRemoteAddr().isBlank()) {
            log.debug("\t\t> 验证通过");
        } else {
            log.warn("\t\t> 用户访问信息不合法");
            return ResultUtil.error(timestamp, ErrorCode.USER_ACCESS_ILLEGAL);
        }
        VerifyCodeDO getVerifyCodeDO = verifyCodeDAO.getVerifyCodeByContact(email);
        if (getVerifyCodeDO != null) {
            if (getVerifyCodeDO.getCreatedAt().getTime() + 120000 > System.currentTimeMillis()) {
                log.debug("\t> 两分钟内不能重复发送验证码");
                long timeLeave = (getVerifyCodeDO.getCreatedAt().getTime() + 120000 - System.currentTimeMillis()) / 1000;
                return ResultUtil.error(timestamp, "可重新发送时间 " + timeLeave + " 秒", ErrorCode.VERIFY_CODE_VALID);
            }
            if (getVerifyCodeDO.getExpiredAt().getTime() < System.currentTimeMillis() || getVerifyCodeDO.getCreatedAt().getTime() + 120000 < System.currentTimeMillis()) {
                log.debug("\t> 邮箱校验码已存在(重新生成验证码/验证码已过期)");
                verifyCodeDAO.deleteVerifyCode(email);
            }
        }
        String verifyCode = ProcessingUtil.createRandomNumbers(6);
        log.debug("\t> 生成的校验码为: {}", verifyCode);
        // 处理数据
        VerifyCodeDO newVerifyCodeDO = new VerifyCodeDO();
        newVerifyCodeDO
                .setType(true)
                .setCode(verifyCode)
                .setContent(email)
                .setCreatedAt(new Timestamp(System.currentTimeMillis()))
                .setExpiredAt(new Timestamp(System.currentTimeMillis() + 900000L))
                .setUserAgent(request.getHeader("User-Agent"))
                .setUserIp(request.getRemoteAddr());
        if (verifyCodeDAO.insertVerifyCodeByEmail(newVerifyCodeDO)) {
            log.debug("\t> 保存验证码成功");
            mailTemplateService.mailSendCode(email, verifyCode, template);
            return ResultUtil.success(timestamp, "邮件发送成功");
        } else {
            log.error("\t> 邮箱校验码存入数据库失败");
            return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> sendMail(long timestamp, @NotNull String email, @NotNull String template) {
        log.info("[Service] 执行 sendMail 方法");
        mailTemplateService.mailSendWithTemplate(email, template);
        return ResultUtil.success(timestamp, "邮件发送成功");
    }
}
