package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.ApproveMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.ApproveOrganizeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 审核组织 DAO
 * <hr/>
 * 用于操作相关的 fy_approve 用于审核操作相关的数据表内容管理
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ApproveDAO {

    private final ApproveMapper approveMapper;

    /**
     * 获取通过审核的列表
     * <hr/>
     * 获取通过审核的组织，默认按照申请时间排序
     *
     * @return List<ApproveOrganizeDO>
     */
    public List<ApproveOrganizeDO> getApproveOrganizeList() {
        log.info("[DAO] 操作 getApproveOrganizeList 方法");
        log.info("\t> 读取 Mysql");
        return approveMapper.getApproveOrganizeList();
    }

    /**
     * 获取组织账户信息
     * <hr/>
     * 获取组织账户信息，用于组织审核
     *
     * @param organizeUuid 组织 UUID
     * @return ApproveOrganizeDO
     */
    public ApproveOrganizeDO getOrganizeAccountByUuid(String organizeUuid) {
        log.info("[DAO] 操作 getOrganizeAccountByUuid 方法");
        log.info("\t> 读取 Mysql");
        return approveMapper.getOrganizeAccountByUuid(organizeUuid);
    }
}
