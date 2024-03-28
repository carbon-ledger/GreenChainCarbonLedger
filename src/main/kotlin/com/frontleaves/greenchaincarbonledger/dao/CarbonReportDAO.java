package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonReportMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 新增碳核算报告数据表记录
     * @param carbonReportDO 报告表DO对象
     * @return 是否成功插入数据
    * */
    public Boolean insertReportMapper(CarbonReportDO carbonReportDO){
        log.info("[DAO] 执行 insertReportMapper");
        log.info("/t Mysql 插入");
        return carbonReportMapper.insertReportMapper(carbonReportDO);
    }


    /**
     * 通过uuid来获取碳核算报告链表(倒序排列)(ID大在最上面)
     * @param uuid-组织uuid
     * @return 碳核算报告链表
     */
    public List<CarbonReportDO> getReportListByUuid(String uuid){
        log.info("[DAO] 执行 getReportListByUuid");
        log.info("\t> Mysql 查询");
        return carbonReportMapper.getReportListByUuid(uuid);
    }

    /**
     * 获取最后一次报告
     * <hr/>
     * 根据账户的UUID进行对信息的获取，获取最后一次碳核算报告单
     *
     * @param organizeUserUuid 组织用户的UUID
     * @return 最后一次报告
     */
    public CarbonReportDO getLastReportByUuid(String organizeUserUuid) {
        log.info("[DAO] 执行 getLastReportByUuid");
        log.info("\t> Mysql 查询");
        return carbonReportMapper.getLastReportByUuid(organizeUserUuid);
    }

    /**
     * 更新碳核算报告
     * @param totalEmission-总碳排放额
     * @param status-报告状态
     * @param id-报告id
     * @return 是否更新成功
     */
    public Boolean updateEmissionById(Double totalEmission,String status,String id){
        log.info("[DAO] 执行 totalEmission");
        log.info("/t Mysql 更新");
        return carbonReportMapper.updateEmissionById(totalEmission, status, id);
    }
}
