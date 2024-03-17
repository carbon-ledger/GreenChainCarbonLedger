package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.dao.UserDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonAccountingDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonReportDO;
import com.frontleaves.greenchaincarbonledger.models.doData.UserDO;
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
    public ResponseEntity<BaseResponse> getCarbonReport(long timestamp,
                                                        @NotNull @jakarta.validation.constraints.NotNull HttpServletRequest request, @NotNull String type,
                                                        @NotNull String search, @Nullable Integer limit, @Nullable Integer page, @NotNull String order) {
        log.info("[Service]执行 getCarbonReport 方法");
        String getUuid = request.getHeader("X-Auth-UUID");
        //校验组织是否在系统中进行碳核算
        int status = 0;
        List<CarbonAccountingDO> getAccountList = carbonDAO.getAccountByUuid(getUuid);
        for (CarbonAccountingDO carbonAccount : getAccountList) {
            CarbonAccountingDO accountingDO = new CarbonAccountingDO();
            accountingDO.setDataVerificationStatus(carbonAccount.getDataVerificationStatus());
            if ("verified'".equals(accountingDO.getDataVerificationStatus())) {
                status = 1;
                break;
            }
        }
        if (status != 1) {
            return ResultUtil.error(timestamp, ErrorCode.CAN_T_ACCOUNT_FOR_CARBON);
        }
        // 检查参数，如果未设置（即为null），则使用默认值
        limit = (limit == null || limit > 100) ? 20 : limit;
        page = (page == null) ? 1 : page;
        if (order.isBlank()) {
            order = "ASC";
        }
        log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
        //进行type值判断
        List<CarbonReportDO> getReportList = null;
        switch (type) {
            case "all" -> {
                order = "id " + order;
                getReportList = carbonDAO.getReportByUuid(getUuid, limit, page, order);
            }
            case "search" -> {
                order = "id" + order;
                getReportList = carbonDAO.getReportBySearch(getUuid, search, limit, page, order);
            }
            case "draft", "pending_review", "approved", "rejected" -> {
                order = "id" + order;
                getReportList = carbonDAO.getReportByStatus(getUuid, search, limit, page, order);
            }
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
    }
}
