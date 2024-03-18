package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * CarbonDAO
 * <hr/>
 * 用于碳交易技术,数据库操作,CarbonDAO
 *
 * @author FLASHLACK AND xiao_lfeng AND DC_DC
 * @version v1.0.0-SNAPSHOT
 * @since v1.0.0-SNAPSHOT
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CarbonDAO {
    private final CarbonMapper carbonMapper;

    /**
     * 根据组织uuid和年份获取碳排放配额
     * <hr/>
     * 根据组织uuid和年份获取碳排放配额,返回CarbonQuotaDO
     *
     * @param uuid  组织uuid
     * @param start 开始年份
     * @param end   结束年份
     * @return 返回CarbonQuotaDO
     */
    public ArrayList<CarbonQuotaDO> getQuotaListByOrganizeUuid(String uuid, String start, String end) {
        log.info("[DAO]执行 getQuotaListByOrganizeUuid 操作 ");
        log.info("Mysql 读取");
        return carbonMapper.getQuotaListByOrganizeUuid(uuid, start, end);
    }

    /**
     * 根据给定的 uuid,分页参数和排序参数，从数据库中获取符合条件的碳排放报告列表。
     *
     * @param uuid  查询所需的 uuid，用于匹配报告的唯一标识符
     * @param limit 每页限制的结果数，用于分页查询
     * @param page  要查询的页数，用于分页查询
     * @param order 排序方式，用于指定查询结果的排序顺序
     * @return 符合条件的碳排放报告列表 -List
     */

    public List<CarbonReportDO> getReportByUuid(String uuid, String limit, String page, String order) {
        log.info("[DAO] 执行 getReportByUuid 方法");
        return carbonMapper.getReportByUuid(uuid, limit, page, order);
    }

    /**
     * 通过状态获取碳排放报告列表
     *
     * @param uuid   用户 UUID
     * @param search 搜索关键字
     * @param limit  每页限制的结果数
     * @param page   要查询的页数
     * @param order  排序方式，可选值包括 "asc"（升序）和 "desc"（降序）
     * @return 包含碳排放报告信息的列表 -List
     */
    public List<CarbonReportDO> getReportByStatus(String uuid, String search, String limit, String page, String order) {
        log.info("[DAO] 执行 getReportByStatus 方法");
        return carbonMapper.getReportByStatus(uuid, search, limit, page, order);
    }

    /**
     * 从数据库中获取符合给定状态的碳报告列表。
     *
     * @param uuid   用户UUID
     * @param search 搜索关键字
     * @param limit  单页限制个数
     * @param page   第几页
     * @param order  排序顺序
     * @return 符合条件的碳报告列表 -List
     */
    public List<CarbonReportDO> getReportBySearch(String uuid, String search, String  limit, String page, String order) {
        log.info("[DAO] 执行 etReportBySearch 方法");
        return carbonMapper.getReportBySearch(uuid, search, limit, page, order);
    }

    /**
     * 通过UUID获取碳核算数据表
     * @param uuid-用户UUID
     * @return 符合条件的碳核算数据列表 -List
     */
    public List<CarbonAccountingDO> getAccountByUuid(String uuid) {
        log.info("[DAO] 执行 getAccountByUuid");
        return carbonMapper.getAccountByUuid(uuid);
    }

    /**
     * 通过UUID获取碳交易发布
     * @param uuid -用户UUID
     * @return 符合条件的碳交易发布列表 -List
     */
    public List<CarbonTradeDO> getTradeByUuid(String uuid){
        log.info("[DAO] 执行 getTradeByUuid");
        return carbonMapper.getTradeByUuid(uuid);
    }

    /**
     * 通过id来获取碳交易发布
     * @param id- 交易id
     * @return 返回CarbonTradeDO
     */
    public CarbonTradeDO getTradeById(String id){
        log.info("[DAO] 执行 getTradeById");
        return carbonMapper.getTradeById(id);
    }

    /**
     * 软删除交易发布
     * @param id-交易id
     * @param status-状态（cancelled）
     * @return 成功返回ture，失败返回false
     */
    public Boolean deleteTrade(String id,String status){
        log.info("[DAO] 执行 deleteTrade");
        return carbonMapper.deleteTrade(id,status);
    }
}
