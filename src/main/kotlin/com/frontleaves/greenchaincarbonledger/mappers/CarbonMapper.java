package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.*;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.ArrayList;
import java.util.List;


/**
 * 用于碳交易的查询和更改
 * <hr/>
 * 用于碳交易的查询和更改
 *
 * @author FLAHSLACK
 */
@Mapper
public interface CarbonMapper {
    @Select("SELECT * FROM fy_carbon_quota WHERE organize_uuid = #{uuid} AND quota_year >= #{start} AND quota_year <= #{end}")
    ArrayList<CarbonQuotaDO> getQuotaListByOrganizeUuid(String uuid, String start, String end);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid = #{uuid}  ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByUuid(String uuid, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_status =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByStatus(String uuid, String search, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_summary =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportBySearch(String uuid, String search, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid}")
    List<CarbonAccountingDO> getAccountByUuid(String uuid);

    @Select("SELECT * FROM fy_carbon_quota WHERE organize_uuid = #{uuid} ORDER BY quota_year DESC LIMIT 1")
    CarbonQuotaDO getOrganizeQuotaByUuid(String uuid);

    @Insert("INSERT INTO fy_carbon_trade (organize_uuid, quota_amount, price_per_unit, description, status, blockchain_tx_id, created_at) VALUES (#{uuid}, #{amount}, #{unit}, #{text}, #{status}, #{blockChainId}, NOW())")
    void insertTradeByUuid(String uuid, String amount, String unit, String text, String blockChainId, String status);

    @Select("SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}")
    List<CarbonTradeDO> getTradeByUuid(String uuid);

    @Select("SELECT * FROM fy_carbon_trade WHERE id=#{id}")
    CarbonTradeDO getTradeById(String id);

    @Update("UPDATE fy_carbon_trade SET status=#{status}, updated_at=now() WHERE id=#{id}")
    Boolean changeStatus(String id, String status);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getTradeListAll(String uuid, Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_carbon_trade
            WHERE organize_uuid=#{uuid}
            AND status LIKE CONCAT('%', #{search}, '%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getTradeListByStatus(String uuid, String search, Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_carbon_trade
            WHERE organize_uuid=#{uuid}
            AND description LIKE CONCAT('%', #{search}, '%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getTradeListBySearch(String uuid, String search, Integer limit, Integer page, String order);

    @Update("UPDATE fy_carbon_trade SET quota_amount = #{amount}, price_per_unit = #{unit}, description = #{text}, status = #{status}, updated_at = NOW() WHERE organize_uuid = #{uuid} AND id = #{id}")
    void updateTradeByUuid(String uuid, String amount, String unit, String text, String status, String id);

    @Select("SELECT * FROM fy_carbon_trade WHERE id = #{id} AND organize_uuid = #{getUuid}")
    CarbonTradeDO getTradeByUuidAndId(String getUuid, String id);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE status = 'active' OR status = 'completed'
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getAvailableTradeListAll(Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE status = #{active}
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getAvailableTradeList(String search, Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE status = #{completed}
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getCompletedTradeList(String search, Integer limit, Integer page, String order);

    @Select("""
            SELECT * FROM fy_carbon_trade
            WHERE description LIKE CONCAT('%', #{search}, '%')
            OR quota_amount  LIKE CONCAT('%', #{search}, '%')
            OR price_per_unit  LIKE CONCAT('%', #{search}, '%')
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
           """)
    List<CarbonTradeDO> getSearchTradeList(String search, Integer limit, Integer page, String order);

    @Update("UPDATE fy_carbon_trade SET verify_uuid = #{verifyUuid}, status = #{status}, updated_at = now()  WHERE id = #{id}")
    Boolean reviewTrade(CarbonTradeDO carbonTradeDO);

    @Select("SELECT * FROM fy_carbon_trade ORDER BY id DESC LIMIT 1")
    CarbonTradeDO getLastThird();

    @Select("SELECT * FROM fy_carbon_trade WHERE buy_uuid = #{uuid} ORDER BY id DESC")
    List<CarbonTradeDO> getBuyTradeListByUuid(String uuid);

    @Select("SELECT * FROM fy_carbon_trade WHERE status = 'pending_review'")
    List<CarbonTradeDO> getTradeNeedReview();

    @Update("UPDATE fy_carbon_quota SET total_quota = #{totalQuota}, updated_at = now() WHERE uuid = #{uuid}")
    void changeTotalQuota(String uuid, double totalQuota);

    @Update("UPDATE fy_carbon_trade SET buy_uuid = #{uuid}, updated_at = now() WHERE id = #{id}")
    void setTradeBuyUuid(String id, String uuid);

    @Update("UPDATE fy_carbon_quota SET audit_log = #{auditLog}, updated_at = now() WHERE uuid = #{uuid}")
    void addAuditLog(String uuid, String auditLog);

    @Select("SELECT * FROM fy_carbon_report WHERE id = #{getReportId}")
    CarbonReportDO getReportById(Long getReportId);

    @Select("SELECT * FROM fy_carbon_accounting WHERE report_id = #{reportId}")
    CarbonAccountingDO getAccountingByReportId(long reportId);

    @Select("SELECT * FROM fy_carbon_report WHERE report_status = #{pending}")
    List<CarbonReportDO> getCarbonReportListByStatus(String pending);

    @Select("SELECT * FROM fy_carbon_compensation_material WHERE accounting_id = #{id}")
    CarbonCompensationMaterialDO getCarbonCompensationMaterialByAccountId(Long id);

    @Update("UPDATE fy_carbon_report SET report_status = #{approved}, updated_at = now() WHERE id = #{id}")
    void changeCarbonReportStatus(Long id, String approved);

    @Update("UPDATE fy_carbon_accounting SET data_verification_status = #{verified}, updated_at = now() WHERE id = #{id}")
    void changeCarbonAccountingStatus(Long id, String verified);

    @Update("UPDATE fy_carbon_quota SET used_quota = #{totalQuota}, updated_at = now() WHERE uuid = #{uuid}")
    void changeUsedQuota(String uuid, double totalQuota);
}
