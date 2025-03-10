package com.frontleaves.greenchaincarbonledger.dao;

import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
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
     * 通过组织uuid获取碳排放报告列表
     *
     * @param uuid   组织 UUID
     * @param limit  每页限制的结果数
     * @param page   要查询的页数
     * @param order  排序方式，可选值包括 "asc"（升序）和 "desc"（降序）
     * @return 包含碳排放报告信息的列表 -List
     */
    public List<CarbonReportDO> getReportByUuid(String uuid, String limit, String page, String order) {
        log.info("[DAO] 执行 getReportByUuid 方法");
        log.info("Mysql 读取");
        return carbonMapper.getReportByUuid(uuid, Integer.parseInt(limit), Integer.parseInt(page), order);
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
        log.info("[DAO] 执行 getReportByStatus 操作");
        log.info("Mysql 读取");
        return carbonMapper.getReportByStatus(uuid, search, Integer.parseInt(limit), Integer.parseInt(page), order);
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
    public List<CarbonReportDO> getReportBySearch(String uuid, String search, String limit, String page, String order) {
        log.info("[DAO] 执行 getReportBySearch 方法");
        log.info("Mysql 读取");
        return carbonMapper.getReportBySearch(uuid, search, Integer.parseInt(limit), Integer.parseInt(page), order);
    }

    /**
     * 通过UUID获取碳核算数据表
     *
     * @param uuid-用户UUID
     * @return 符合条件的碳核算数据列表 -List
     */
    public List<CarbonAccountingDO> getAccountByUuid(String uuid) {
        log.info("[DAO] 执行 getAccountByUuid 方法");
        log.info("Mysql 读取");
        return carbonMapper.getAccountByUuid(uuid);
    }

    public CarbonQuotaDO getOrganizeQuotaByUuid(String uuid) {
        log.info("[DAO] 执行 getTotalQuotaByUuid 方法");
        log.info("Mysql 读取");
        return carbonMapper.getOrganizeQuotaByUuid(uuid);
    }

    /**
     * 通过UUID获取碳交易发布
     *
     * @param uuid -用户UUID
     * @return 符合条件的碳交易发布列表 -List
     */
    public List<CarbonTradeDO> getTradeByUuid(String uuid) {
        log.info("[DAO] 执行 getTradeByUuid");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeByUuid(uuid);
    }

    /**
     * 通过id来获取碳交易发布
     *
     * @param id- 交易id
     * @return 返回CarbonTradeDO
     */
    public CarbonTradeDO getTradeById(String id) {
        log.info("[DAO] 执行 getTradeById");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeById(id);
    }

    /**
     * 更新碳交易发布状态
     *
     * @param id-交易id
     * @param status-状态
     * @return 是否更新成功
     */
    public Boolean changeStatus(String id, String status) {
        log.info("[DAO] 执行 changeStatus");
        log.info("\t> Mysql 更新");
        return carbonMapper.changeStatus(id, status);
    }

    /**
     * 获取当前组织的全部碳交易发布信息列表
     *
     * @param uuid-组织uuid
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getTradeListAll(String uuid, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getTradeListAll");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeListAll(uuid, limit, page, order);
    }


    /**
     * 通过Status获取当前组织的全部碳交易发布信息列表
     *
     * @param uuid-组织uuid
     * @param search-status
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getTradeListByStatus(String uuid, String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getTradeListByStatus");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeListByStatus(uuid, search, limit, page, order);
    }

    /**
     * 通过模糊查询获取当前组织的全部碳交易发布信息列表
     *
     * @param uuid-组织uuid
     * @param search-status
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getTradeListBySearch(String uuid, String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getTradeListBySearch");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeListBySearch(uuid, search, limit, page, order);
    }

    /**
     * 通过uuid和id获取碳交易发布
     *
     * @param getUuid-组织uuid
     * @param id-交易id
     * @return 返回CarbonTradeDO
     */
    public CarbonTradeDO getTradeByUuidAndId(String getUuid, String id) {
        log.info("[DAO] 执行 getTradeByUuidAndId");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeByUuidAndId(getUuid, id);
    }

    /**
     * 获取全部可用的碳交易发布信息列表
     *
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getAvailableTradeListAll(Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getAvailableTradeListAll");
        log.info("\t> Mysql 读取");
        return carbonMapper.getAvailableTradeListAll(limit, page, order);
    }

    /**
     * 通过Status获取全部可用的碳交易发布信息列表
     *
     * @param search-status
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getAvailableTradeList(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getAvailableTradeList");
        log.info("\t> Mysql 读取");
        return carbonMapper.getAvailableTradeList(search, limit, page, order);
    }

    /**
     * 获取全部已完成的碳交易发布信息列表
     *
     * @param search-status
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getCompletedTradeList(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getCompletedTradeList");
        log.info("\t> Mysql 读取");
        return carbonMapper.getCompletedTradeList(search, limit, page, order);
    }

    /**
     * 通过模糊查询获取全部已完成的碳交易发布信息列表
     *
     * @param search-status
     * @param limit-页数限制
     * @param page-页码
     * @param order-顺序
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getSearchTradeList(String search, Integer limit, Integer page, String order) {
        log.info("[DAO] 执行 getSearchTradeList");
        log.info("\t> Mysql 读取");
        return carbonMapper.getSearchTradeList(search, limit, page, order);
    }

    /**
     * 审核碳交易发布
     *
     * @param carbonTradeDO-审核信息
     * @return 成功返回ture，失败返回false
     */
    public Boolean reviewTrade(CarbonTradeDO carbonTradeDO) {
        log.info("[DAO] 执行 reviewTrade");
        log.info("\t> Mysql 更新");
        return carbonMapper.reviewTrade(carbonTradeDO);
    }

    /**
     * 获取当前组织的全部碳交易购买信息列表
     *
     * @param uuid-组织uuid
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getBuyTradeListByUuid(String uuid) {
        log.info("[DAO] 执行 getBuyTradeListByUuid");
        log.info("\t> Mysql 读取");
        return carbonMapper.getBuyTradeListByUuid(uuid);
    }

    /**
     * 获取需要审核的碳交易发布信息列表
     *
     * @return 碳交易发布信息列表
     */
    public List<CarbonTradeDO> getTradeNeedReview() {
        log.info("[DAO] 执行 getTradeNeedReview");
        log.info("\t> Mysql 读取");
        return carbonMapper.getTradeNeedReview();
    }

    /**
     * 更新组织配额
     *
     * @param uuid-组织uuid
     * @param totalQuota-组织配额
     */
    public void changeTotalQuota(String uuid, double totalQuota) {
        log.info("[DAO] 执行 changeTotalQuota");
        log.info("\t> Mysql 更新");
        carbonMapper.changeTotalQuota(uuid, totalQuota);
    }

    /**
     * 更新碳交易发布信息
     *
     * @param uuid 组织uuid
     * @param id 交易id
     */
    public void setTradeBuyUuid(String id, String uuid) {
        log.info("[DAO] 执行 setTradeBuyUuid");
        log.info("\t> Mysql 更新");
        carbonMapper.setTradeBuyUuid(id, uuid);
    }

    /**
     * 添加审计日志
     *
     * @param uuid 组织uuid
     * @param auditLog 审计日志
     */
    public void addAuditLog(String uuid, String auditLog) {
        log.info("[DAO] 执行 addAuditLog");
        log.info("\t> Mysql 插入");
        carbonMapper.addAuditLog(uuid, auditLog);
    }

    /**
     * 获取碳交易报告
     *
     * @param getReportId 获取报告 ID
     * @return 返回报告内容实体
     */
    public CarbonReportDO getReportById(Long getReportId) {
        log.info("[DAO] 执行 getReportById");
        log.info("\t> Mysql 读取");
        return carbonMapper.getReportById(getReportId);
    }

    /**
     * 获取碳交易报告
     *
     * @param reportId 获取报告 ID
     * @return 返回报告内容实体列表
     */
    public CarbonAccountingDO getAccountingByReportId(long reportId) {
        log.info("[DAO] 执行 getAccountingByReportId");
        log.info("\t> Mysql 读取");
        return carbonMapper.getAccountingByReportId(reportId);
    }

    /**
     * 获取碳交易报告
     *
     * @param pending 获取报告状态
     * @return 返回报告内容实体列表
     */
    public List<CarbonReportDO> getCarbonReportListByStatus(String pending) {
        log.info("[DAO] 执行 getCarbonReportListByStatus");
        log.info("\t> Mysql 读取");
        return carbonMapper.getCarbonReportListByStatus(pending);
    }

    /**
     * 获取碳交易报告
     *
     * @param id 获取报告 ID
     * @return 返回报告内容实体列表
     */
    public CarbonCompensationMaterialDO getCarbonCompensationMaterialByAccountId(Long id) {
        log.info("[DAO] 执行 getCarbonCompensationMaterialByAccountId");
        log.info("\t> Mysql 读取");
        return carbonMapper.getCarbonCompensationMaterialByAccountId(id);
    }

    /**
     * 修改碳排放报告的状态。
     * @param id 报告的唯一标识符。
     * @param approved 新的状态，通常表示是否批准该报告。
     */
    public void changeCarbonReportStatus(Long id, String approved) {
        log.info("[DAO] 执行 changeCarbonReportStatus");
        log.info("\t> Mysql 更新");
        carbonMapper.changeCarbonReportStatus(id, approved);
    }

    /**
     * 修改碳排放核算报告状态。
     * @param id 报告的唯一标识符。
     * @param verified 新的状态，通常表示是否批准该报告。
     */
    public void changeCarbonAccountingStatus(Long id, String verified) {
        log.info("[DAO] 执行 changeCarbonAccountingStatus");
        log.info("\t> Mysql 更新");
        carbonMapper.changeCarbonAccountingStatus(id, verified);
    }

    /**
     * 修改使用的配额量。
     * 该方法用于更新指定UUID的实体的已使用配额总量。
     *
     * @param uuid 实体的唯一标识符，用于确定哪个实体的配额被修改。
     * @param totalQuota 修改后的总配额量。
     */
    public void changeUsedQuota(String uuid, double totalQuota) {
        log.info("[DAO] 执行 changeUsedQuota");
        log.info("\t> Mysql 更新");
        carbonMapper.changeUsedQuota(uuid, totalQuota);
    }
}
