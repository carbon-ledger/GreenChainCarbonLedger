package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.MailSendCodeVO;
import com.frontleaves.greenchaincarbonledger.services.MailService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MailController
 * <hr/>
 * 用于邮件服务的控制器类，用于发送邮件，接收邮件等，提供邮件服务
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/send/code")
    public ResponseEntity<BaseResponse> sendMailByCode(
            @RequestBody @Validated MailSendCodeVO mailSendCodeVO,
            HttpServletRequest request,
            @NotNull BindingResult bindingResult
            ) {
        log.info("[Controller] 执行 sendMailByCode 方法");
        long timestamp = System.currentTimeMillis();
        if (bindingResult.hasErrors()) {
            log.warn("\t> 参数校验失败");
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务代码
        return mailService.sendMailByCode(timestamp, request, mailSendCodeVO.getEmail(), mailSendCodeVO.getTemplate());
    }
}
