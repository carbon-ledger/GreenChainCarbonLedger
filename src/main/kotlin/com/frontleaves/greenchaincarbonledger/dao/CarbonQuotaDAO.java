package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonQuotaMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用于对碳排放额的数据库查询和更新
 *
 * @author FLSHALCK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonQuotaDAO {
    private final CarbonQuotaMapper carbonQuotaMapper;

    /**
     * 通过年份和uuid来得到对应的碳排放配额
     *
     * @param year-年份
     * @param uuid-组织uuid
     * @return 符合条件的碳排放配额
     */
    public CarbonQuotaDO getCarbonQuota(Integer year, String uuid) {
        log.info("[DAO] 执行 getCarbonQuota 操作");
        log.info("\t> Mysql查询");
        return carbonQuotaMapper.getCarbonQuota(year, uuid);
    }

    /**
     * 完成碳交易后的数据库更新
     *
     * @param totalQuota-更新后的总额
     * @param uuid-组织uuid
     * @param year-年份
     * @return 是否更新成功
     */
    public Boolean finishCarbonTrade(Double totalQuota, String uuid, Integer year) {
        log.info("[DAO] 执行 finishCarbonTrade 操作");
        log.info("\t> Mysql更新");
        return carbonQuotaMapper.finishCarbonTrade(totalQuota, uuid, year);
    }

    /**
     * 创建碳排放配额
     *
     * @param carbonQuotaDO-碳排放配额
     * @return 是否完成
     */
    public Boolean createCarbonQuota(CarbonQuotaDO carbonQuotaDO) {
        log.info("[DAO] 执行 CreateCarbonQuota 操作");
        log.info("\t> Mysql 插入");
        return carbonQuotaMapper.createCarbonQuota(carbonQuotaDO);
    }

    /**
     * 为组织修改碳配额
     *
     * @param uuid-组织uuid
     * @param year-年份
     * @param totalQuota-配额总
     * @param status-合规状态
     * @param auditLog-审计日志
     * @return 是否完成
     */
    public Boolean editCarbonQuota(String uuid, Integer year, Double totalQuota, boolean status, String auditLog) {
        log.info("[DAO] 执行 editCarbonQuota 操作");
        log.info("\t> Mysql更新");
        return carbonQuotaMapper.editCarbonQuota(uuid, year, totalQuota, status, auditLog);
    }
}
