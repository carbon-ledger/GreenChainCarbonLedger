package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewOrganizeVO;
import com.frontleaves.greenchaincarbonledger.services.ReviewService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * ReviewServiceImpl
 * <hr/>
 * 用于实现审核服务, 用于组织账户与监管账户的实名认证审核
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addReviewFromOrganize(@NotNull ReviewOrganizeVO reviewOrganizeVO, @NotNull HttpServletRequest request) {
        return null;
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addReviewFromAdmin(@NotNull ReviewOrganizeVO reviewOrganizeVO, @NotNull HttpServletRequest request) {
        return null;
    }
}
