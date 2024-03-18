package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonAccountingDAO;
import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonAccountingVO;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonQuotaVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonReportVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FLASHLACK
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarbonServiceImpl implements CarbonService {
    private final CarbonDAO carbonDAO;
    private final UserDAO userDAO;
    private final CarbonAccountingDAO carbonAccountingDAO;
    private final CarbonMapper carbonMapper;

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getOwnCarbonQuota(long timestamp, @NotNull HttpServletRequest request, String start, @Nullable String end) {
        log.info("[Service] 执行 getOwnCarbonQuota 方法");
        UserDO getAuthUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        if (getAuthUserDO != null) {
            //检查数据
            if (start == null || start.isEmpty()) {
                start = new SimpleDateFormat("yyyy").format(getAuthUserDO.getCreatedAt());
            }
            // 数据库读取数据
            ArrayList<CarbonQuotaDO> carbonQuotaList = carbonDAO.getQuotaListByOrganizeUuid(getAuthUserDO.getUuid(), start, end);
            ArrayList<BackCarbonQuotaVO> backCarbonQuotaVOList = new ArrayList<>();
            carbonQuotaList.forEach(carbonQuotaDO -> {
                BackCarbonQuotaVO backCarbonQuotaVO = new BackCarbonQuotaVO();
                backCarbonQuotaVO
                        .setUuid(carbonQuotaDO.getUuid())
                        .setOrganizeUuid(carbonQuotaDO.organizeUuid)
                        .setQuotaYear(carbonQuotaDO.quotaYear)
                        .setTotalQuota(carbonQuotaDO.totalQuota)
                        .setAllocatedQuota(carbonQuotaDO.getAllocatedQuota())
                        .setUsedQuota(carbonQuotaDO.usedQuota)
                        .setAllocationDate(carbonQuotaDO.allocationDate)
                        .setComplianceStatus(carbonQuotaDO.complianceStatus)
                        .setCreatedAt(carbonQuotaDO.createdAt)
                        .setUpdatedAt(carbonQuotaDO.updatedAt);
                backCarbonQuotaVOList.add(backCarbonQuotaVO);
            });
            return ResultUtil.success(timestamp, backCarbonQuotaVOList);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.USER_NOT_EXISTED);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getCarbonReport(
            long timestamp,
            @NotNull HttpServletRequest request, @NotNull String type, @NotNull String search, @NotNull String limit,
            @NotNull String page, String order
    ) {
        log.info("[Service] 执行 getCarbonReport 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        //校验组织是否在系统中进行碳核算
        List<CarbonAccountingDO> getAccountList = carbonDAO.getAccountByUuid(getUuid);
        if (!getAccountList.isEmpty()) {
            // 检查参数，如果未设置（即为null），则使用默认值
            limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
            page = (page.isEmpty()) ? "1" : page;
            if (order.isBlank()) {
                order = "ASC";
            }
            log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
            //进行type值判断
            List<CarbonReportDO> getReportList;
            order = "id " + order;
            switch (type) {
                case "all" -> getReportList = carbonDAO.getReportByUuid(getUuid, limit, page, order);
                case "search" -> getReportList = carbonDAO.getReportBySearch(getUuid, search, limit, page, order);
                case "draft", "pending_review", "approved", "rejected" ->
                        getReportList = carbonDAO.getReportByStatus(getUuid, search, limit, page, order);
                default -> {
                    return ResultUtil.error(timestamp, "type 参数有误", ErrorCode.REQUEST_BODY_ERROR);
                }
            }
            //整理数据
            ArrayList<BackCarbonReportVO> backCarbonReportList = new ArrayList<>();
            if (getReportList != null) {
                for (CarbonReportDO getReport : getReportList) {
                    BackCarbonReportVO backCarbonReportVO = new BackCarbonReportVO();
                    backCarbonReportVO
                            .setId(getReport.getId())
                            .setOrganizeUuid(getReport.getOrganizeUuid())
                            .setAccountingPeriod(getReport.getAccountingPeriod())
                            .setTotalEmission(getReport.getTotalEmission())
                            .setEmissionReduction(getReport.getEmissionReduction())
                            .setNetEmission(getReport.getNetEmission())
                            .setReportStatus(getReport.getReportStatus())
                            .setCreatedAt(getReport.getCreatedAt())
                            .setUpdatedAt(getReport.getUpdateAt());
                    backCarbonReportList.add(backCarbonReportVO);
                }
                //输出
                return ResultUtil.success(timestamp, "获取自己组织碳核算报告信息已准备完毕", backCarbonReportList);
            } else {
                return ResultUtil.error(timestamp, ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, ErrorCode.CAN_T_ACCOUNT_FOR_CARBON);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getCarbonAccounting(long timestamp, @NotNull HttpServletRequest request, String limit, String page, @NotNull String order) {
        log.info("[Service] 执行 getCarbonAccounting 方法");
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
        page = (page.isEmpty()) ? "1" : page;
        if (order.isBlank()) {
            order = "id ASC";
        } else {
            order = "id " + order;
        }
        // 获取自己企业的Uuid
        String organizeUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        List<BackCarbonAccountingVO> carbonAccountinglist = new ArrayList<>();
        List<CarbonAccountingDO> carbonAccountingDOList = carbonAccountingDAO.getCarbonAccountingList(organizeUuid, Integer.parseInt(limit), Integer.parseInt(page), order);
        if (carbonAccountingDOList != null){
            for (CarbonAccountingDO carbonAccountingDO : carbonAccountingDOList) {
                BackCarbonAccountingVO backCarbonAccountingVO = new BackCarbonAccountingVO();
                backCarbonAccountingVO
                        .setId(carbonAccountingDO.getId())
                        .setOrganizeUuid(carbonAccountingDO.getOrganizeUuid())
                        .setEmissionSource(carbonAccountingDO.getEmissionSource())
                        .setEmissionAmount(carbonAccountingDO.getEmissionAmount())
                        .setAccountingPeriod(carbonAccountingDO.getAccountingPeriod())
                        .setEmissionSource(carbonAccountingDO.getEmissionSource())
                        .setDataVerificationStatus(carbonAccountingDO.getDataVerificationStatus())
                        .setCreateAt(carbonAccountingDO.getCreatedAt())
                        .setUpdateAt(carbonAccountingDO.getUpdatedAt());
                carbonAccountinglist.add(backCarbonAccountingVO);
            }
            return ResultUtil.success(timestamp, "数据已准备完毕", carbonAccountinglist);
        } else {
            return ResultUtil.error(timestamp, ErrorCode.SELECT_DATA_ERROR);
        }
    }
    @NotNull
    @Override
    public ResponseEntity<BaseResponse> releaseCarbonTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull TradeReleaseVO tradeReleaseVO) {
        log.info("[Service] 执行 releaseCarbonTrade 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        // 先对自己组织剩余的碳配额量进行判断
        // 1.获取 总配额量total_quota、已分配额量allocated_quota、已使用配额量used_quota
        CarbonQuotaDO carbonQuotaDO = carbonDAO.getQuotaByUuid(getUuid);
        double totalQuota = carbonQuotaDO.getTotalQuota();
        double allocatedQuota = carbonQuotaDO.getAllocatedQuota();
        double usedQuota = carbonQuotaDO.getUsedQuota();
        // 2.根据三个数据获取组织现有的碳配额量
        double nowQuota = totalQuota - usedQuota;
        // 3.如果企业的 已使用配额量used_quota 小于 已分配额量allocated_quota 的话，才允许发布碳交易
        // 达到允许条件下，则可发布交易
        if (nowQuota > 0 && allocatedQuota > usedQuota){
            if (tradeReleaseVO.getDraft()){
                carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO, "draft");
            } else {
                carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO, "pending_review");
            }
            return ResultUtil.success(timestamp, "交易发布成功");
        } else {
            if (nowQuota <= 0) {
                return ResultUtil.error(timestamp, "当前组织碳配额量小于0，无法发布交易", ErrorCode.RELEASE_TRADE_FAILURE);
            }else {
                return ResultUtil.error(timestamp, "无法使用购入的碳配额量进行交易", ErrorCode.RELEASE_TRADE_FAILURE);
            }
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> editCarbonTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull EditTradeVO editTradeVO) {
        log.info("[Service] 执行 releaseCarbonTrade 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        // 判断用户是否发布过交易
        // 判断交易是否已经发布
        CarbonTradeDO carbonTradeDO = carbonDAO.getTradeByUuid(getUuid);
        String status = carbonTradeDO.getStatus();
        if ("draft".equals(status) || "pending_review".equals(status)){
            // 判断编辑的信息是否合法有效，如果有效则可以提交编辑
            if (editTradeVO.getDraft()){
                carbonMapper.updateTradeByUuid(getUuid, editTradeVO, "draft");
            } else {
                carbonMapper.updateTradeByUuid(getUuid, editTradeVO, "pending_review");
            }
            return ResultUtil.success(timestamp, "交易发布信息修改成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.EDIT_TRADE_FAILURE);
        }

    }
}
