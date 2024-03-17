package com.frontleaves.greenchaincarbonledger.controllers;

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReviewController
 * <hr/>
 * 用作组织账户与监管账户的实名认证审核，组织账户的实名认证审核需要组织账户权限，监管账户的实名认证审核需要监管账户权限
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng AND FLASHLACK AND DC_DC
 */
@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    @PostMapping("/add/organize")
    @CheckAccountPermission({"review:addOrganize"})
    public ResponseEntity<BaseResponse> addReviewFromOrganize(

    ) {
        return null;
    }

    @PostMapping("/add/admin")
    @CheckAccountPermission({"review:addAdmin"})
    public ResponseEntity<BaseResponse> addReviewFromAdmin(

    ) {
        return null;
    }

    @PatchMapping("/check/organize/{checkId}")
    @CheckAccountPermission({"review:checkOrganize"})
    public ResponseEntity<BaseResponse> checkReviewFromOrganize(

            @PathVariable String checkId
    ) {
        return null;
    }

    @PatchMapping("/check/admin/{checkId}")
    @CheckAccountPermission({"review:checkAdmin"})
    public ResponseEntity<BaseResponse> checkReviewFromAdmin(

            @PathVariable String checkId
    ) {
        return null;
    }

    @PutMapping("/re-send/organize/{checkId}")
    @CheckAccountPermission({"review:reSendOrganize"})
    public ResponseEntity<BaseResponse> reSendReviewFromOrganize(

            @PathVariable String checkId
    ) {
        return null;
    }

    @PutMapping("/re-send/admin/{checkId}")
    @CheckAccountPermission({"review:reSendAdmin"})
    public ResponseEntity<BaseResponse> reSendReviewFromAdmin(

            @PathVariable String checkId
    ) {
        return null;
    }

    @GetMapping("/list")
    @CheckAccountPermission({"review:getList"})
    public ResponseEntity<BaseResponse> getReviewList(

    ) {
        return null;
    }
}
