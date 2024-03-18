package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.ReviewMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveManageDO;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

/**
 * ReviewDAO
 * <hr/>
 * 用于审核的数据访问对象, 用于Mybatis
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDAO {
    private final ReviewMapper reviewMapper;

    /**
     * 检查组织是否已经审核
     * <hr/>
     * 用于检查组织是否已经审核
     *
     * @param organizeName 组织名称
     * @param creditCode 组织信用代码
     * @return ApproveOrganizeDO
     */
    public ApproveOrganizeDO checkOrganizeHasApprove(@NotNull String organizeName, @NotNull String creditCode) {
        log.info("[DAO] 执行 checkOrganizeHasApprove 方法");
        log.debug("\t> Mysql 读取");
        return reviewMapper.getOrganizeApprove(organizeName, creditCode);
    }

    /**
     * 设置组织审核
     * <hr/>
     * 用于设置组织审核
     *
     * @param newApproveOrganizeDO 新的组织审核DO
     */
    public void setReviewOrganizeApprove(ApproveOrganizeDO newApproveOrganizeDO) {
        log.info("[DAO] 执行 setReviewOrganizeApprove 方法");
        log.debug("\t> Mysql 写入");
        reviewMapper.setApproveOrganize(newApproveOrganizeDO);
    }

    /**
     * 检查管理员是否已经审核
     * <hr/>
     * 用于检查管理员是否已经审核
     *
     * @param organizeName 组织名称
     * @return ApproveManageDO
     */
    public ApproveManageDO checkAdminHasApprove(String organizeName) {
        log.info("[DAO] 执行 checkAdminHasApprove 方法");
        log.debug("\t> Mysql 读取");
        return reviewMapper.getAdminApprove(organizeName);
    }

    /**
     * 设置管理员审核
     * <hr/>
     * 用于设置管理员审核
     *
     * @param newApproveManageDO 新的管理员审核DO
     */
    public void setReviewAdminApprove(ApproveManageDO newApproveManageDO) {
        log.info("[DAO] 执行 setReviewAdminApprove 方法");
        log.debug("\t> Mysql 写入");
        reviewMapper.setApproveAdmin(newApproveManageDO);
    }
}
