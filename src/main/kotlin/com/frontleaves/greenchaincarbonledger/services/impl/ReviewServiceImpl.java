package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.ReviewDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveManageDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewAdminVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewCheckVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewOrganizeVO;
import com.frontleaves.greenchaincarbonledger.services.ReviewService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ReviewServiceImpl
 * <hr/>
 * 用于实现审核服务, 用于组织账户与监管账户的实名认证审核
 *
 * @author xiao_lfeng
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDAO reviewDAO;
    private final UserDAO userDAO;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addReviewFromOrganize(long timestamp, @NotNull ReviewOrganizeVO reviewOrganizeVO, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 addReviewFromOrganize 方法");
        // 企业账户资料添加
        ApproveOrganizeDO approveOrganizeDO = reviewDAO.checkOrganizeHasApprove(reviewOrganizeVO.getOrganizeName(), reviewOrganizeVO.getCreditCode());
        if (approveOrganizeDO == null) {
            UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
            if (getUserDO != null) {
                Base64 base64 = new Base64();
                try {
                    // 对图片数据进行 Base64 解码重命名保存在指定位置
                    byte[] license = base64.decode(reviewOrganizeVO.getLicense());
                    OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_organize.jpg");
                    licenseOutput.write(license);
                    licenseOutput.flush();
                    licenseOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardFront = base64.decode(reviewOrganizeVO.getLegalIdCardFront());
                    OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card" + getUserDO.getUuid() + "_front.jpg");
                    idCardFrontOutput.write(idCardFront);
                    idCardFrontOutput.flush();
                    idCardFrontOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardBack = base64.decode(reviewOrganizeVO.getLegalIdCardBack());
                    OutputStream idCardBackOutput = new FileOutputStream("output/legal_id_card" + getUserDO.getUuid() + "_back.jpg");
                    idCardBackOutput.write(idCardBack);
                    idCardBackOutput.flush();
                    idCardBackOutput.close();
                } catch (IOException e) {
                    log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                    // 删除文件（处理无关数据）
                    if (!new File("upload/license/" + getUserDO.getUuid() + "_organize.jpg").delete()) {
                        log.debug("[Service] License 资料不存在");
                    }
                    if (!new File("upload/legal_id_card" + getUserDO.getUuid() + "_front.jpg").delete()) {
                        log.debug("[Service] IdCardFront 资料不存在");
                    }
                    if (!new File("output/legal_id_card" + getUserDO.getUuid() + "_back.jpg").delete()) {
                        log.debug("[Service] IdCardBack 资料不存在");
                    }
                    ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
                }
                // 对数据进行集中化处理
                ApproveOrganizeDO newApproveOrganizeDO = new ApproveOrganizeDO();
                newApproveOrganizeDO
                        .setAccountUuid(getUserDO.getUuid())
                        .setType((short) 0)
                        .setOrganizeName(reviewOrganizeVO.getOrganizeName())
                        .setOrganizeLicenseUrl(getUserDO.getUuid() + "_organize.jpg")
                        .setOrganizeCreditCode(reviewOrganizeVO.getCreditCode())
                        .setOrganizeRegisteredCapital(reviewOrganizeVO.getRegisteredCapital())
                        .setOrganizeEstablishmentDate(reviewOrganizeVO.getEstablishmentDate())
                        .setLegalRepresentativeName(reviewOrganizeVO.getLegalRepresentativeName())
                        .setLegalRepresentativeId(reviewOrganizeVO.getLegalRepresentativeId())
                        .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front.jpg")
                        .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back.jpg")
                        .setRemarks(reviewOrganizeVO.getRemark());
                try {
                    reviewDAO.setReviewOrganizeApprove(newApproveOrganizeDO);
                    return ResultUtil.success(timestamp, "申请已提交");
                } catch (Exception e) {
                    log.error("[DAO] 数据库写入错误: {}", e.getMessage(), e);
                    return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
            }
        } else {
            return ResultUtil.error(timestamp, "申请已通过或正在审核", ErrorCode.REVIEW_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addReviewFromAdmin(long timestamp, @NotNull ReviewAdminVO reviewAdminVO, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 addReviewFromAdmin 方法");
        // 监管账户资料添加
        ApproveManageDO approveManageDO = reviewDAO.checkAdminHasApprove(reviewAdminVO.getOrganizeName());
        if (approveManageDO == null) {
            UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
            if (getUserDO != null) {
                Base64 base64 = new Base64();
                try {
                    // 对图片数据进行 Base64 解码重命名保存在指定位置
                    byte[] license = base64.decode(reviewAdminVO.getOrganizeAuthorize());
                    OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_authorize.jpg");
                    licenseOutput.write(license);
                    licenseOutput.flush();
                    licenseOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardFront = base64.decode(reviewAdminVO.getLegalIdCardFront());
                    OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card" + getUserDO.getUuid() + "_front.jpg");
                    idCardFrontOutput.write(idCardFront);
                    idCardFrontOutput.flush();
                    idCardFrontOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardBack = base64.decode(reviewAdminVO.getLegalIdCardBack());
                    OutputStream idCardBackOutput = new FileOutputStream("output/legal_id_card" + getUserDO.getUuid() + "_back.jpg");
                    idCardBackOutput.write(idCardBack);
                    idCardBackOutput.flush();
                    idCardBackOutput.close();
                } catch (IOException e) {
                    log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                    // 删除文件（处理无关数据）
                    if (!new File("upload/license/" + getUserDO.getUuid() + "_authorize.jpg").delete()) {
                        log.debug("[Service] License 资料不存在");
                    }
                    if (!new File("upload/legal_id_card" + getUserDO.getUuid() + "_front.jpg").delete()) {
                        log.debug("[Service] IdCardFront 资料不存在");
                    }
                    if (!new File("output/legal_id_card" + getUserDO.getUuid() + "_back.jpg").delete()) {
                        log.debug("[Service] IdCardBack 资料不存在");
                    }
                    ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
                }

            } else {
                return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
            }
            // 对数据进行集中化处理
            ApproveManageDO newApproveManageDO = new ApproveManageDO();
            newApproveManageDO
                    .setAccountUuid(getUserDO.getUuid())
                    .setAccountType(reviewAdminVO.getType())
                    .setOrganizeName(reviewAdminVO.getOrganizeName())
                    .setOrganizeAuthorizeUrl(getUserDO.getUuid() + "_authorize.jpg")
                    .setLegalRepresentativeName(reviewAdminVO.getLegalRepresentativeName())
                    .setLegalRepresentativeId(reviewAdminVO.getLegalRepresentativeId())
                    .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front.jpg")
                    .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back.jpg")
                    .setRemarks(reviewAdminVO.getRemarks());
            try {
                reviewDAO.setReviewAdminApprove(newApproveManageDO);
                return ResultUtil.success(timestamp, "申请已提交");
            } catch (Exception e) {
                log.error("[DAO] 数据库写入错误: {}", e.getMessage(), e);
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, "申请已通过或正在审核", ErrorCode.REVIEW_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> checkReviewFormOrganize(
            long timestamp,
            @NotNull String checkId,
            @NotNull ReviewCheckVO reviewCheckVO,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Service] 执行 checkReviewFormOrganize 方法");
        // 根据id获取信息
        ApproveOrganizeDO getApproveOrganizeDO = reviewDAO.getApproveOrganizeById(checkId);
        if (getApproveOrganizeDO != null) {
            // 检查是否审核通过
            if (reviewCheckVO.getAllow()) {
                reviewDAO.setReviewOrganizeAllow(getApproveOrganizeDO.getId(), true, null);
            } else {
                reviewDAO.setReviewOrganizeAllow(getApproveOrganizeDO.getId(), false, reviewCheckVO.getRemark());
            }
            return ResultUtil.success(timestamp, "已进行操作");
        } else {
            return ResultUtil.error(timestamp, "审核内容不存在", ErrorCode.REVIEW_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> checkReviewFormAdmin(
            long timestamp,
            @NotNull String checkId,
            @NotNull ReviewCheckVO reviewCheckVO,
            @NotNull HttpServletRequest request
    ) {
        log.info("[Service] 执行 checkReviewFormAdmin 方法");
        // 根据id获取信息
        ApproveManageDO getApproveAdminDO = reviewDAO.getApproveAdminById(checkId);
        if (getApproveAdminDO != null) {
            // 检查是否审核通过
            if (reviewCheckVO.getAllow()) {
                reviewDAO.setReviewAdminAllow(getApproveAdminDO.getId(), true, null);
            } else {
                reviewDAO.setReviewAdminAllow(getApproveAdminDO.getId(), false, reviewCheckVO.getRemark());
            }
            return ResultUtil.success(timestamp, "已进行操作");
        } else {
            return ResultUtil.error(timestamp, "审核内容不存在", ErrorCode.REVIEW_ERROR);
        }
    }
}
