package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.CarbonDAO;
import com.frontleaves.greenchaincarbonledger.models.doData.CarbonQuotaDO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonQuotaVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author FLASHLACK
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CarbonServiceImpl implements CarbonService {
    private final CarbonDAO carbonDAO;
    private final Gson gson;
    @NotNull
    @Override
    public ResponseEntity<BaseResponse> getOwnCarbonQuota(long timestamp, @NotNull HttpServletRequest request, @Nullable Integer start, @Nullable Integer end) {
        log.info("[Service] 执行 getOwnCarbonQuota ");
        String getUuid = request.getHeader("\"X-Auth-UUID\"");
        SimpleDateFormat thisYear = new SimpleDateFormat("yyyy");
        //检查数据
        if (start == null){
            start = Integer.parseInt(thisYear.format(System.currentTimeMillis()));
        }
        if (end == null){
            end = Integer.parseInt(thisYear.format(System.currentTimeMillis()));
        }
        ArrayList<CarbonQuotaDO> newCarbonQuotaList = new ArrayList<>();
        while (start<=end){
            CarbonQuotaDO[] carbonQuotaArray = carbonDAO.getQuotaByUuidYear(getUuid,start);
            newCarbonQuotaList.addAll(Arrays.asList(carbonQuotaArray));
            start ++;
        }
        //整理数据
        ArrayList<BackCarbonQuotaVO> backCarbonQuotaVOList = new ArrayList<>();
        for (CarbonQuotaDO carbonQuota : newCarbonQuotaList){
            BackCarbonQuotaVO backCarbonQuotaVO = new BackCarbonQuotaVO();
            backCarbonQuotaVO.setUuid(carbonQuota.getUuid());
            backCarbonQuotaVO.setOrganizeUuid(carbonQuota.organizeUuid);
            backCarbonQuotaVO.setQuotaYear(carbonQuota.quotaYear);
            backCarbonQuotaVO.setTotalQuota(carbonQuota.totalQuota);
            backCarbonQuotaVO.setAllocatedQuota(carbonQuota.getAllocatedQuota());
            backCarbonQuotaVO.setUsedQuota(carbonQuota.usedQuota);
            backCarbonQuotaVO.setAllocationDate(carbonQuota.allocationDate);
            backCarbonQuotaVO.setComplianceStatus(carbonQuota.complianceStatus);
            backCarbonQuotaVO.setCreatedAt(carbonQuota.createdAt);
            backCarbonQuotaVO.setUpdatedAt(carbonQuota.updatedAt);
            backCarbonQuotaVO.setAuditLog(gson.fromJson(String.valueOf(carbonQuota.getAuditLog()), new TypeToken<ArrayList<String>>() {
            }.getType()));
            backCarbonQuotaVOList.add(backCarbonQuotaVO);
        }
        return ResultUtil.success(timestamp,backCarbonQuotaVOList);

    }






























}
