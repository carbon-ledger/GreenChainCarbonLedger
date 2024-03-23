package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.ReviewDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveManageDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewAdminVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewCheckVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.ReviewOrganizeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackReviewAdminVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackReviewListVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackReviewOrganizeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackUserVO;
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
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

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

    /**
     * 获取图片类型
     * <hr/>
     * 用于获取图片类型, 用于判断图片类型
     *
     * @param base64 图片数据
     * @return 图片类型
     */
    @NotNull
    private static String getImageType(@NotNull String base64) {
        if (base64.startsWith("data:image/png;base64,")) {
            return "png";
        } else if (base64.startsWith("data:image/jpg;base64,")) {
            return "jpg";
        } else if (base64.startsWith("data:image/jpeg;base64,")) {
            return "jpeg";
        } else {
            return "png";
        }
    }

    /**
     * 单独获取图片 base64
     * <hr/>
     * 对图片进行内容拆分，拆分单独的base64出来进行解析操作
     *
     * @param base64 总体base64
     * @return 返回单独的 base64
     */
    @Nullable
    private static String extractImageData(@NotNull String base64) {
        switch (getImageType(base64)) {
            case "png" -> {
                return base64.replace("data:image/png;base64,", "");
            }
            case "jpg" -> {
                return base64.replace("data:image/jpg;base64,", "");
            }
            case "jpeg" -> {
                return base64.replace("data:image/jpeg;base64,", "");
            }
            default -> {
                return null;
            }
        }
    }

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
                    OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense()));
                    byte[] license = base64.decode(extractImageData(reviewOrganizeVO.getLicense()));
                    licenseOutput.write(license);
                    licenseOutput.flush();
                    licenseOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront()));
                    byte[] idCardFront = base64.decode(extractImageData(reviewOrganizeVO.getLegalIdCardFront()));
                    idCardFrontOutput.write(idCardFront);
                    idCardFrontOutput.flush();
                    idCardFrontOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    OutputStream idCardBackOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewOrganizeVO.getLegalIdCardBack()));
                    byte[] idCardBack = base64.decode(extractImageData(reviewOrganizeVO.getLegalIdCardBack()));
                    idCardBackOutput.write(idCardBack);
                    idCardBackOutput.flush();
                    idCardBackOutput.close();
                } catch (Exception e) {
                    log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                    // 删除文件（处理无关数据）
                    if (!new File("upload/license/" + getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense())).delete()) {
                        log.debug("[Service] License 资料不存在");
                    }
                    if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront())).delete()) {
                        log.debug("[Service] IdCardFront 资料不存在");
                    }
                    if (!new File("output/legal_id_card/" + getUserDO.getUuid() + "_back.jpg" + getImageType(reviewOrganizeVO.getLegalIdCardBack())).delete()) {
                        log.debug("[Service] IdCardBack 资料不存在");
                    }
                    return ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
                }
                // 对数据进行集中化处理
                ApproveOrganizeDO newApproveOrganizeDO = new ApproveOrganizeDO();
                newApproveOrganizeDO
                        .setAccountUuid(getUserDO.getUuid())
                        .setType((short) 0)
                        .setOrganizeName(reviewOrganizeVO.getOrganizeName())
                        .setOrganizeLicenseUrl(getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense()))
                        .setOrganizeCreditCode(reviewOrganizeVO.getCreditCode())
                        .setOrganizeRegisteredCapital(reviewOrganizeVO.getRegisteredCapital())
                        .setOrganizeEstablishmentDate(reviewOrganizeVO.getEstablishmentDate())
                        .setLegalRepresentativeName(reviewOrganizeVO.getLegalRepresentativeName())
                        .setLegalRepresentativeId(reviewOrganizeVO.getLegalRepresentativeId())
                        .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront()))
                        .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back." + getImageType(reviewOrganizeVO.getLegalIdCardBack()))
                        .setApplyTime(new Date(System.currentTimeMillis()))
                        .setRemarks(reviewOrganizeVO.getRemark());
                reviewDAO.setReviewOrganizeApprove(newApproveOrganizeDO);
                return ResultUtil.success(timestamp, "申请已提交");
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
                    byte[] license = base64.decode(extractImageData(reviewAdminVO.getOrganizeAuthorize()));
                    OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize()));
                    licenseOutput.write(license);
                    licenseOutput.flush();
                    licenseOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardFront = base64.decode(extractImageData(reviewAdminVO.getLegalIdCardFront()));
                    OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront()));
                    idCardFrontOutput.write(idCardFront);
                    idCardFrontOutput.flush();
                    idCardFrontOutput.close();
                    // 对身份证数据进行 Base64 解码重命名保存在指定位置
                    byte[] idCardBack = base64.decode(extractImageData(reviewAdminVO.getLegalIdCardBack()));
                    OutputStream idCardBackOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewAdminVO.getLegalIdCardBack()));
                    idCardBackOutput.write(idCardBack);
                    idCardBackOutput.flush();
                    idCardBackOutput.close();
                } catch (Exception e) {
                    log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                    // 删除文件（处理无关数据）
                    if (!new File("upload/license/" + getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize())).delete()) {
                        log.debug("[Service] License 资料不存在");
                    }
                    if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront())).delete()) {
                        log.debug("[Service] IdCardFront 资料不存在");
                    }
                    if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_back.jpg" + getImageType(reviewAdminVO.getLegalIdCardBack())).delete()) {
                        log.debug("[Service] IdCardBack 资料不存在");
                    }
                    return ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
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
                    .setOrganizeAuthorizeUrl(getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize()))
                    .setLegalRepresentativeName(reviewAdminVO.getLegalRepresentativeName())
                    .setLegalRepresentativeId(reviewAdminVO.getLegalRepresentativeId())
                    .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront()))
                    .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back." + getImageType(reviewAdminVO.getLegalIdCardBack()))
                    .setApplyTime(new Date(System.currentTimeMillis()))
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

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> reSendReviewFormOrganize(long timestamp, @NotNull String checkId, @NotNull ReviewOrganizeVO reviewOrganizeVO, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 reSendReviewFormOrganize 方法");
        // 获取用户资料
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUserDO != null) {
            // 获取管理账户资料
            ApproveOrganizeDO getApproveOrganizeDO = reviewDAO.getApproveOrganizeById(checkId);
            if (getApproveOrganizeDO != null) {
                if (getApproveOrganizeDO.getApproveUuid().equals(getUserDO.getUuid())) {
                    if (getApproveOrganizeDO.getCertificationStatus() != 0) {
                        // 对资料进行编辑
                        Base64 base64 = new Base64();
                        try {
                            // 对图片数据进行 Base64 解码重命名保存在指定位置
                            byte[] license = base64.decode(extractImageData(reviewOrganizeVO.getLicense()));
                            OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense()));
                            licenseOutput.write(license);
                            licenseOutput.flush();
                            licenseOutput.close();
                            // 对身份证数据进行 Base64 解码重命名保存在指定位置
                            byte[] idCardFront = base64.decode(extractImageData(reviewOrganizeVO.getLegalIdCardFront()));
                            OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront()));
                            idCardFrontOutput.write(idCardFront);
                            idCardFrontOutput.flush();
                            idCardFrontOutput.close();
                            // 对身份证数据进行 Base64 解码重命名保存在指定位置
                            byte[] idCardBack = base64.decode(extractImageData(reviewOrganizeVO.getLegalIdCardBack()));
                            OutputStream idCardBackOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewOrganizeVO.getLegalIdCardBack()));
                            idCardBackOutput.write(idCardBack);
                            idCardBackOutput.flush();
                            idCardBackOutput.close();
                        } catch (Exception e) {
                            log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                            // 删除文件（处理无关数据）
                            if (!new File("upload/license/" + getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense())).delete()) {
                                log.debug("[Service] License 资料不存在");
                            }
                            if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront())).delete()) {
                                log.debug("[Service] IdCardFront 资料不存在");
                            }
                            if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewOrganizeVO.getLegalIdCardBack())).delete()) {
                                log.debug("[Service] IdCardBack 资料不存在");
                            }
                            return ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
                        }
                        // 资料进行更新
                        getApproveOrganizeDO
                                .setAccountUuid(getUserDO.getUuid())
                                .setType((short) 0)
                                .setOrganizeName(reviewOrganizeVO.getOrganizeName())
                                .setOrganizeLicenseUrl(getUserDO.getUuid() + "_organize." + getImageType(reviewOrganizeVO.getLicense()))
                                .setOrganizeCreditCode(reviewOrganizeVO.getCreditCode())
                                .setOrganizeRegisteredCapital(reviewOrganizeVO.getRegisteredCapital())
                                .setOrganizeEstablishmentDate(reviewOrganizeVO.getEstablishmentDate())
                                .setLegalRepresentativeName(reviewOrganizeVO.getLegalRepresentativeName())
                                .setLegalRepresentativeId(reviewOrganizeVO.getLegalRepresentativeId())
                                .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front." + getImageType(reviewOrganizeVO.getLegalIdCardFront()))
                                .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back." + getImageType(reviewOrganizeVO.getLegalIdCardBack()))
                                .setRemarks(reviewOrganizeVO.getRemark())
                                .setApplyTime(new Date(System.currentTimeMillis()))
                                .setUpdatedAt(new Timestamp(System.currentTimeMillis()))
                                .setApproveUuid(null)
                                .setApproveRemarks(null)
                                .setApplyTime(null)
                                .setCertificationStatus((short) 0);
                        reviewDAO.updateReviewOrganizeApprove(getApproveOrganizeDO);
                        return ResultUtil.success(timestamp, "申请已提交");
                    } else {
                        return ResultUtil.error(timestamp, "您的资料正在等待审核", ErrorCode.REVIEW_ERROR);
                    }
                } else {
                    return ResultUtil.error(timestamp, "您没有权限重新编辑此内容", ErrorCode.REVIEW_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "资料不存在", ErrorCode.REVIEW_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> reSendReviewFormAdmin(long timestamp, @NotNull String checkId, @NotNull ReviewAdminVO reviewAdminVO, @NotNull HttpServletRequest request) {
        log.info("[Service] 执行 reSendReviewFormOrganize 方法");
        // 获取用户资料
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUserDO != null) {
            // 获取管理账户资料
            ApproveManageDO getApproveAdminById = reviewDAO.getApproveAdminById(checkId);
            if (getApproveAdminById != null) {
                if (getApproveAdminById.getApproveUuid().equals(getUserDO.getUuid())) {
                    if (getApproveAdminById.getCertificationStatus() != 0) {
                        // 对资料进行编辑
                        Base64 base64 = new Base64();
                        try {
                            // 对图片数据进行 Base64 解码重命名保存在指定位置
                            byte[] license = base64.decode(extractImageData(reviewAdminVO.getOrganizeAuthorize()));
                            OutputStream licenseOutput = new FileOutputStream("upload/license/" + getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize()));
                            licenseOutput.write(license);
                            licenseOutput.flush();
                            licenseOutput.close();
                            // 对身份证数据进行 Base64 解码重命名保存在指定位置
                            byte[] idCardFront = base64.decode(extractImageData(reviewAdminVO.getLegalIdCardFront()));
                            OutputStream idCardFrontOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront()));
                            idCardFrontOutput.write(idCardFront);
                            idCardFrontOutput.flush();
                            idCardFrontOutput.close();
                            // 对身份证数据进行 Base64 解码重命名保存在指定位置
                            byte[] idCardBack = base64.decode(extractImageData(reviewAdminVO.getLegalIdCardBack()));
                            OutputStream idCardBackOutput = new FileOutputStream("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewAdminVO.getLegalIdCardBack()));
                            idCardBackOutput.write(idCardBack);
                            idCardBackOutput.flush();
                            idCardBackOutput.close();
                        } catch (Exception e) {
                            log.error("[Service] 文件处理错误: {}", e.getMessage(), e);
                            // 删除文件（处理无关数据）
                            if (!new File("upload/license/" + getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize())).delete()) {
                                log.debug("[Service] License 资料不存在");
                            }
                            if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront())).delete()) {
                                log.debug("[Service] IdCardFront 资料不存在");
                            }
                            if (!new File("upload/legal_id_card/" + getUserDO.getUuid() + "_back." + getImageType(reviewAdminVO.getLegalIdCardBack())).delete()) {
                                log.debug("[Service] IdCardBack 资料不存在");
                            }
                            return ResultUtil.error(timestamp, "文件处理错误", ErrorCode.SERVER_INTERNAL_ERROR);
                        }
                        // 资料进行更新
                        getApproveAdminById
                                .setAccountUuid(getUserDO.getUuid())
                                .setAccountType(reviewAdminVO.getType())
                                .setOrganizeName(reviewAdminVO.getOrganizeName())
                                .setOrganizeAuthorizeUrl(getUserDO.getUuid() + "_authorize." + getImageType(reviewAdminVO.getOrganizeAuthorize()))
                                .setLegalRepresentativeName(reviewAdminVO.getLegalRepresentativeName())
                                .setLegalRepresentativeId(reviewAdminVO.getLegalRepresentativeId())
                                .setLegalIdCardFrontUrl(getUserDO.getUuid() + "_front." + getImageType(reviewAdminVO.getLegalIdCardFront()))
                                .setLegalIdCardBackUrl(getUserDO.getUuid() + "_back." + getImageType(reviewAdminVO.getLegalIdCardBack()))
                                .setRemarks(reviewAdminVO.getRemarks())
                                .setApplyTime(new Date(System.currentTimeMillis()))
                                .setUpdatedAt(new Timestamp(System.currentTimeMillis()))
                                .setCertificationStatus((short) 0)
                                .setApproveTime(null)
                                .setApproveUuid(null)
                                .setApproveRemarks(null);
                        reviewDAO.updateReviewAdminApprove(getApproveAdminById);
                        return ResultUtil.success(timestamp, "申请已提交");
                    } else {
                        return ResultUtil.error(timestamp, "您的资料正在等待审核", ErrorCode.REVIEW_ERROR);
                    }
                } else {
                    return ResultUtil.error(timestamp, "您没有权限重新编辑此内容", ErrorCode.REVIEW_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "资料不存在", ErrorCode.REVIEW_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getReviewList(long timestamp, @NotNull HttpServletRequest request) {
        // 数据检查转换
        // 筛选数据
        ArrayList<ApproveOrganizeDO> getApproveOrganizeDOList = reviewDAO.getApproveOrganizeList();
        ArrayList<ApproveManageDO> getApproveAdminDOList = reviewDAO.getApproveAdminList();
        ArrayList<BackReviewListVO> newApproveOrganizeDOList = new ArrayList<>();
        getApproveOrganizeDOList.forEach(it -> {
            BackReviewListVO newApproveOrganizeDO = new BackReviewListVO();
            // 根据 uuid 查找账户
            UserDO getUserDO = userDAO.getUserByUuid(it.getAccountUuid());
            BackUserVO backUserVO = new BackUserVO();
            backUserVO
                    .setUserName(getUserDO.getUserName())
                    .setEmail(getUserDO.getEmail());
            newApproveOrganizeDO
                    .setAccount(backUserVO)
                    .setOrganizeName(it.getOrganizeName())
                    .setLegalRepresentativeName(it.getLegalRepresentativeName())
                    .setApplyTime(it.getApplyTime());
            newApproveOrganizeDOList.add(newApproveOrganizeDO);
        });
        getApproveAdminDOList.forEach(it -> {
            BackReviewListVO newApproveAdminDO = new BackReviewListVO();
            // 根据 uuid 查找账户
            UserDO getUserDO = userDAO.getUserByUuid(it.getAccountUuid());
            BackUserVO backUserVO = new BackUserVO();
            backUserVO
                    .setUserName(getUserDO.getUserName())
                    .setEmail(getUserDO.getEmail());
            newApproveAdminDO
                    .setAccount(backUserVO)
                    .setOrganizeName(it.getOrganizeName())
                    .setLegalRepresentativeName(it.getLegalRepresentativeName())
                    .setApplyTime(it.getApplyTime());
            newApproveOrganizeDOList.add(newApproveAdminDO);
        });
        return ResultUtil.success(timestamp, newApproveOrganizeDOList);
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getReview(long timestamp, @NotNull String type, @NotNull String id, @NotNull HttpServletRequest request) {
        // 根据 id 获取审核信息
        if ("organize".equals(type)) {
            ApproveOrganizeDO getApproveOrganizeDO = reviewDAO.getApproveOrganizeById(id);
            if (getApproveOrganizeDO != null) {
                // 根据 uuid 查找账户
                UserDO getUserDO = userDAO.getUserByUuid(getApproveOrganizeDO.getAccountUuid());
                BackUserVO backUserVO = new BackUserVO();
                backUserVO
                        .setUuid(getUserDO.getUuid())
                        .setUserName(getUserDO.getUserName())
                        .setNickName(getUserDO.getNickName())
                        .setRealName(getUserDO.getRealName())
                        .setEmail(getUserDO.getEmail())
                        .setPhone(getUserDO.getPhone())
                        .setAvatar(getUserDO.getAvatar())
                        .setCreatedAt(getUserDO.getCreatedAt())
                        .setUpdatedAt(getUserDO.getUpdatedAt());
                BackReviewOrganizeVO newApproveOrganizeDO = new BackReviewOrganizeVO();
                newApproveOrganizeDO
                        .setAccount(backUserVO)
                        .setType(getApproveOrganizeDO.getType())
                        .setOrganizeName(getApproveOrganizeDO.getOrganizeName())
                        .setOrganizeCreditCode(getApproveOrganizeDO.getOrganizeCreditCode())
                        .setOrganizeRegisteredCapital(getApproveOrganizeDO.getOrganizeRegisteredCapital())
                        .setOrganizeEstablishmentDate(getApproveOrganizeDO.getOrganizeEstablishmentDate())
                        .setOrganizeLicenseUrl(getApproveOrganizeDO.getOrganizeLicenseUrl())
                        .setLegalRepresentativeName(getApproveOrganizeDO.getLegalRepresentativeName())
                        .setLegalRepresentativeId(getApproveOrganizeDO.getLegalRepresentativeId())
                        .setLegalIdCardFrontUrl(getApproveOrganizeDO.getLegalIdCardFrontUrl())
                        .setLegalIdCardBackUrl(getApproveOrganizeDO.getLegalIdCardBackUrl())
                        .setApplyTime(getApproveOrganizeDO.getApplyTime())
                        .setUpdatedAt(getApproveOrganizeDO.getUpdatedAt())
                        .setRemarks(getApproveOrganizeDO.getRemarks());
                return ResultUtil.success(timestamp, newApproveOrganizeDO);
            } else {
                return ResultUtil.error(timestamp, "审核内容不存在", ErrorCode.REVIEW_ERROR);
            }
        } else {
            ApproveManageDO getApproveAdminDO = reviewDAO.getApproveAdminById(id);
            if (getApproveAdminDO != null) {
                // 根据 uuid 查找账户
                UserDO getUserDO = userDAO.getUserByUuid(getApproveAdminDO.getAccountUuid());
                BackUserVO backUserVO = new BackUserVO();
                backUserVO
                        .setUuid(getUserDO.getUuid())
                        .setUserName(getUserDO.getUserName())
                        .setNickName(getUserDO.getNickName())
                        .setRealName(getUserDO.getRealName())
                        .setEmail(getUserDO.getEmail())
                        .setPhone(getUserDO.getPhone())
                        .setAvatar(getUserDO.getAvatar())
                        .setCreatedAt(getUserDO.getCreatedAt())
                        .setUpdatedAt(getUserDO.getUpdatedAt());
                BackReviewAdminVO newApproveAdminDO = new BackReviewAdminVO();
                newApproveAdminDO
                        .setAccount(backUserVO)
                        .setAccountType(getApproveAdminDO.getAccountType())
                        .setOrganizeName(getApproveAdminDO.getOrganizeName())
                        .setOrganizeAuthorizeUrl(getApproveAdminDO.getOrganizeAuthorizeUrl())
                        .setLegalRepresentativeName(getApproveAdminDO.getLegalRepresentativeName())
                        .setLegalRepresentativeId(getApproveAdminDO.getLegalRepresentativeId())
                        .setLegalIdCardFrontUrl(getApproveAdminDO.getLegalIdCardFrontUrl())
                        .setLegalIdCardBackUrl(getApproveAdminDO.getLegalIdCardBackUrl())
                        .setCertificationStatus(getApproveAdminDO.getCertificationStatus())
                        .setApplyTime(getApproveAdminDO.getApplyTime())
                        .setRemarks(getApproveAdminDO.getRemarks());
                return ResultUtil.success(timestamp, newApproveAdminDO);
            } else {
                return ResultUtil.error(timestamp, "审核内容不存在", ErrorCode.REVIEW_ERROR);
            }
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getReviewReport(long timestamp, @NotNull HttpServletRequest request) {
        // 获取用户信息
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getUserDO != null) {
            // 获取自己的审核报告
            ApproveOrganizeDO getApproveOrganizeDO = reviewDAO.getApproveOrganizeByUuid(getUserDO.getUuid());
            if (getApproveOrganizeDO != null) {
                return ResultUtil.success(timestamp, getApproveOrganizeDO);
            } else {
                ApproveManageDO getApproveAdminDO = reviewDAO.getApproveAdminByUuid(getUserDO.getUuid());
                if (getApproveAdminDO != null) {
                    return ResultUtil.success(timestamp, getApproveAdminDO);
                } else {
                    return ResultUtil.error(timestamp, "没有审核报告", ErrorCode.REVIEW_ERROR);
                }
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }
}
