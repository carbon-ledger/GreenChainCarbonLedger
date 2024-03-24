package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonReportMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 用于对于碳核算报告的操作
 * @author FLASHLACK
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonReportDAO {
    private final CarbonReportMapper carbonReportMapper;

    /**
     * 初始化碳核算报告
     * @param uuid-组织uuid
     * @param title-报告标题
     * @param type-碳核算类型（外键）
     * @param period-核算周期
     * @param status-状态
     * @param summary-报告摘要
     * @return 是否完成创建
     */
    public Boolean initializationReportMapper(String uuid,String title,String type,String period,String status,String summary){
        log.info("[DAO] 执行 initializationReportMapper");
        log.info("/t Mysql 插入");
        return carbonReportMapper.initializationReportMapper(uuid, title, type, period, status, summary);
    }
}
