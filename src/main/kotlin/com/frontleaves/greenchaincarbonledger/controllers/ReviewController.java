package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewOrganizeVO;
import com.frontleaves.greenchaincarbonledger.services.ReviewService;
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
import org.springframework.web.bind.annotation.*;

/**
 * ReviewController
 * <hr/>
 * 用作组织账户与监管账户的实名认证审核，组织账户的实名认证审核需要组织账户权限，监管账户的实名认证审核需要监管账户权限
 *
 * @author xiao_lfeng AND FLASHLACK AND DC_DC
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 组织账户实名认证审核
     * <hr/>
     * 用于组织账户的实名认证审核，需要组织账户权限, 通过组织账户实名认证审核VO进行审核
     *
     * @param reviewOrganizeVO 组织账户实名认证审核VO
     * @param bindingResult     数据绑定结果
     * @param request           请求
     * @return ResponseEntity<BaseResponse>
     */
    @PostMapping("/add/organize")
    @CheckAccountPermission({"review:addOrganize"})
    public ResponseEntity<BaseResponse> addReviewFromOrganize(
            @RequestBody @Validated ReviewOrganizeVO reviewOrganizeVO,
            @NotNull BindingResult bindingResult,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 执行 addReviewFromOrganize 方法");
        long timestamp = System.currentTimeMillis();
        // 数据检查
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务逻辑
        return reviewService.addReviewFromOrganize(reviewOrganizeVO, request);
    }

    /**
     * 监管账户实名认证审核
     * <hr/>
     * 用于监管账户的实名认证审核，需要监管账户权限, 通过监管账户实名认证审核VO进行审核
     *
     * @param reviewOrganizeVO 组织账户实名认证审核VO
     * @param bindingResult     数据绑定结果
     * @param request           请求
     * @return ResponseEntity<BaseResponse>
     */
    @PostMapping("/add/admin")
    @CheckAccountPermission({"review:addAdmin"})
    public ResponseEntity<BaseResponse> addReviewFromAdmin(
            @RequestBody @Validated ReviewOrganizeVO reviewOrganizeVO,
            @NotNull BindingResult bindingResult,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Controller] 执行 addReviewFromAdmin 方法");
        long timestamp = System.currentTimeMillis();
        // 数据检查
        if (bindingResult.hasErrors()) {
            return ResultUtil.error(timestamp, ErrorCode.REQUEST_BODY_ERROR, ProcessingUtil.getValidatedErrorList(bindingResult));
        }
        // 业务逻辑
        return reviewService.addReviewFromAdmin(reviewOrganizeVO, request);
    }

    /**
     * 组织账户实名认证审核
     * <hr/>
     * 用于组织账户的实名认证审核，需要组织账户权限, 通过组织账户实名认证审核VO进行审核
     *
     * @param checkId 审核ID
     * @return ResponseEntity<BaseResponse>
     */
    @PatchMapping("/check/organize/{checkId}")
    @CheckAccountPermission({"review:checkOrganize"})
    public ResponseEntity<BaseResponse> checkReviewFromOrganize(

            @PathVariable String checkId
    ) {
        return null;
    }

    /**
     * 监管账户实名认证审核
     * <hr/>
     * 用于监管账户的实名认证审核，需要监管账户权限, 通过监管账户实名认证审核VO进行审核
     *
     * @param checkId 审核ID
     * @return ResponseEntity<BaseResponse>
     */
    @PatchMapping("/check/admin/{checkId}")
    @CheckAccountPermission({"review:checkAdmin"})
    public ResponseEntity<BaseResponse> checkReviewFromAdmin(

            @PathVariable String checkId
    ) {
        return null;
    }

    /**
     * 组织账户实名认证审核
     * <hr/>
     * 用于组织账户的实名认证审核，需要组织账户权限, 通过组织账户实名认证审核VO进行审核
     *
     * @param checkId 审核ID
     * @return ResponseEntity<BaseResponse>
     */
    @PutMapping("/re-send/organize/{checkId}")
    @CheckAccountPermission({"review:reSendOrganize"})
    public ResponseEntity<BaseResponse> reSendReviewFromOrganize(

            @PathVariable String checkId
    ) {
        return null;
    }

    /**
     * 监管账户实名认证审核
     * <hr/>
     * 用于监管账户的实名认证审核，需要监管账户权限, 通过监管账户实名认证审核VO进行审核
     *
     * @param checkId 审核ID
     * @return ResponseEntity<BaseResponse>
     */
    @PutMapping("/re-send/admin/{checkId}")
    @CheckAccountPermission({"review:reSendAdmin"})
    public ResponseEntity<BaseResponse> reSendReviewFromAdmin(

            @PathVariable String checkId
    ) {
        return null;
    }

    /**
     * 获取审核列表
     * <hr/>
     * 用于获取审核列表，需要组织账户权限
     *
     * @return ResponseEntity<BaseResponse>
     */
    @GetMapping("/list")
    @CheckAccountPermission({"review:getList"})
    public ResponseEntity<BaseResponse> getReviewList(

    ) {
        return null;
    }
}
