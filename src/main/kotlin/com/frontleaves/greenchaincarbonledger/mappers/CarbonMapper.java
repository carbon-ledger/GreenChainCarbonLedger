package com.frontleaves.greenchaincarbonledger.mappers;

import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonTradeDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
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
    List<CarbonReportDO> getReportByUuid(String uuid, String limit, String page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_status =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportByStatus(String uuid, String search, String limit, String page, String order);

    @Select("SELECT * FROM fy_carbon_report WHERE organize_uuid=#{uuid} AND report_summary =#{search} ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}")
    List<CarbonReportDO> getReportBySearch(String uuid, String search, String limit, String page, String order);

    @Select("SELECT * FROM fy_carbon_accounting WHERE organize_uuid=#{uuid}")
    List<CarbonAccountingDO> getAccountByUuid(String uuid);

    @Select("SELECT * FROM fy_carbon_quota WHERE uuid = #{uuid}")
    CarbonQuotaDO getQuotaByUuid(String uuid);

    @Insert("INSERT INTO fy_carbon_trade (organize_uuid, quota_amount, price_per_unit, description, status, created_at) VALUES (#{uuid}, #{amount}, #{unit}, #{text}, #{status}, NOW())")
    void insertTradeByUuid(String uuid, TradeReleaseVO tradeReleaseVO, String status);

    @Select("SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}")
    List<CarbonTradeDO> getTradeByUuid(String uuid);

    @Select("SELECT * FROM fy_carbon_trade WHERE id=#{id}")
    CarbonTradeDO getTradeById(String id);

    @Update("UPDATE fy_carbon_trade SET status=#{status} AND updated_at=now() WHERE id=#{id}")
    Boolean deleteTrade(String id, String status);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}
            ORDER BY ${order} LIMIT #{limit} OFFSET ${(page-1) * limit}
            """)
    List<CarbonTradeDO> getTradeListAll(String uuid, Integer limit, Integer page, String order);

    @Select("SELECT * FROM fy_carbon_trade WHERE organize_uuid=#{uuid}")
    Boolean getTradeListByUuid(String uuid);

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
    void updateTradeByUuid(String uuid, EditTradeVO editTradeVO, String status, String id);

    @Select("SELECT * FROM fy_carbon_trade WHERE id = #{id} AND organize_uuid = #{getUuid}")
    CarbonTradeDO getTradeByUuidAndId(String getUuid, String id);

    @Select("""
            SELECT * FROM fy_carbon_trade WHERE status = #{active} OR status = #{completed}
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
}
