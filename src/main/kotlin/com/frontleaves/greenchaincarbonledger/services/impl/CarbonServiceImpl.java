package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.*;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonTypeMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonConsumeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.EditTradeVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.TradeReleaseVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonAccountingVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonQuotaVO;
import com.frontleaves.greenchaincarbonledger.models.voData.returnData.BackCarbonReportVO;
import com.frontleaves.greenchaincarbonledger.services.CarbonService;
import com.frontleaves.greenchaincarbonledger.utils.BaseResponse;
import com.frontleaves.greenchaincarbonledger.utils.ErrorCode;
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil;
import com.frontleaves.greenchaincarbonledger.utils.ResultUtil;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final Gson gson;
    private final CarbonReportDAO carbonReportDAO;
    private final CarbonTypeMapper carbonTypeMapper;
    private final CarbonCompensationMaterialDAO carbonCompensationMaterialDAO;
    private final CarbonItemTypeDAO carbonItemTypeDAO;
    private final ProcessEmissionFactorDAO processEmissionFactorDAO;
    private final OtherEmissionFactorDAO otherEmissionFactorDAO;

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
        if (carbonAccountingDOList != null) {
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
        if (nowQuota > 0 && allocatedQuota > usedQuota) {
            if (tradeReleaseVO.getDraft()) {
                carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO, "draft");
            } else {
                carbonMapper.insertTradeByUuid(getUuid, tradeReleaseVO, "pending_review");
            }
            return ResultUtil.success(timestamp, "交易发布成功");
        } else {
            if (nowQuota <= 0) {
                return ResultUtil.error(timestamp, "当前组织碳配额量小于0，无法发布交易", ErrorCode.RELEASE_TRADE_FAILURE);
            } else {
                return ResultUtil.error(timestamp, "无法使用购入的碳配额量进行交易", ErrorCode.RELEASE_TRADE_FAILURE);
            }
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> editCarbonTrade(long timestamp, @NotNull HttpServletRequest request, @NotNull EditTradeVO editTradeVO, @NotNull String id) {
        log.info("[Service] 执行 releaseCarbonTrade 方法");
        String getUuid = ProcessingUtil.getAuthorizeUserUuid(request);
        // 判断用户是否发布过交易
        // 判断交易是否已经发布
        CarbonTradeDO carbonTradeDO = carbonDAO.getTradeByUuidAndId(getUuid, id);
        String status = carbonTradeDO.getStatus();
        if ("draft".equals(status) || "pending_review".equals(status)) {
            // 判断编辑的信息是否合法有效，如果有效则可以提交编辑
            if (editTradeVO.getDraft()) {
                carbonMapper.updateTradeByUuid(getUuid, editTradeVO, "draft", id);
            } else {
                carbonMapper.updateTradeByUuid(getUuid, editTradeVO, "pending_review", id);
            }
            return ResultUtil.success(timestamp, "交易发布信息修改成功");
        } else {
            return ResultUtil.error(timestamp, ErrorCode.EDIT_TRADE_FAILURE);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> createCarbonReport(long timestamp, @NotNull HttpServletRequest request, @NotNull CarbonConsumeVO carbonConsumeVO) {
        //获取时间
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date startDate;
        Date endDate;
        try {
            startDate = inputDateFormat.parse(carbonConsumeVO.getStartTime());
            endDate = inputDateFormat.parse(carbonConsumeVO.getEndTime());
        } catch (ParseException e) {
            log.error("[Service] 日期读取错误：{}", e.getMessage(), e);
            return ResultUtil.error(timestamp, "日期读取错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        String formattedStartDate = outputDateFormat.format(startDate);
        String formattedEndDate = outputDateFormat.format(endDate);
        String formattedDateRange = formattedStartDate + "-" + formattedEndDate;
        //进行判断是否这次要创建的报告的核算开始时间是否和之前的结束时间有冲突
        //首先取出之前的报告结束时间,若没有无需进行判断，若有进行判断
        List<CarbonReportDO> carbonReportDOList = carbonReportDAO.getReportListByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        if (!carbonReportDOList.isEmpty()) {
            //获取最新的碳核算报告
            CarbonReportDO lastCarbonReportDO = carbonReportDOList.get(0);
            // 获取时间范围的结束日期
            String accountingPeriod = lastCarbonReportDO.getAccountingPeriod();
            String[] dateRange = accountingPeriod.split("-");
            String endDateString = dateRange[1];
            // 将结束日期字符串转换为日期对象
            Date endLastDate;
            try {
                endLastDate = inputDateFormat.parse(endDateString);
            } catch (ParseException e) {
                log.error("[Service] 日期解析错误：{}", e.getMessage(), e);
                return ResultUtil.error(timestamp, "日期解析错误", ErrorCode.SERVER_INTERNAL_ERROR);
            }
            //若endLastDate 晚于或者等于 endDate
            if (endLastDate.compareTo(startDate) >= 0) {
                return ResultUtil.error(timestamp, "碳核算周期时间冲突", ErrorCode.PATH_VARIABLE_ERROR);
            }
        }
        //取出报告类型(通过type)
        CarbonTypeDO getCarbonType = carbonTypeMapper.getTypeByName(carbonConsumeVO.getType());
        //进行数据库初始化本次碳核算报告表
        if (carbonReportDAO.initializationReportMapper(ProcessingUtil.getAuthorizeUserUuid(request), carbonConsumeVO.getTitle(), getCarbonType.getUuid(), formattedDateRange, "draft", carbonConsumeVO.getSummary())) {
            //进行查询
            List<CarbonReportDO> getCarbonReportListDO = carbonReportDAO.getReportListByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
            //获取最新的碳核算报告
            CarbonReportDO getCarbonReportDO = getCarbonReportListDO.get(0);
            //初始化本次碳核算数据表
            if (carbonAccountingDAO.initializationCarbonAccounting(ProcessingUtil.getAuthorizeUserUuid(request), getCarbonReportDO.getId(), getCarbonType.getUuid(), formattedDateRange, "pending")) {
                //查询碳核算数据表
                List<CarbonAccountingDO> carbonAccountingDOList = carbonAccountingDAO.getCarbonAccountingListByUuidDesc(ProcessingUtil.getAuthorizeUserUuid(request));
                CarbonAccountingDO getCarbonAccounting = carbonAccountingDOList.get(0);
                //初始化原料表
                if (carbonCompensationMaterialDAO.initializationCarbonCompensationMaterial(getCarbonAccounting.getId(), carbonConsumeVO.getMaterials())) {
                    //进行碳核算计算
                    //解析materials
                    String materialsJson = carbonConsumeVO.getMaterials();
                    MaterialsDO materialsDO = gson.fromJson(materialsJson, MaterialsDO.class);
                    //获取materials列表
                    List<MaterialsDO.Materials> materialsList = materialsDO.getMaterials();
                    // 声明一个变量来存储累加的结果
                    double totalCombustion = 0.0;
                    for (MaterialsDO.Materials material : materialsList) {
                        //获取材料名称
                        CarbonItemTypeDO carbonItemTypeDO = carbonItemTypeDAO.getCarbonItemTypeByName(material.getName());
                        //获取净消耗量
                        MaterialsDO.Material materialData = material.getMaterial();
                        // 计算净消耗量
                        double netConsumption = Double.parseDouble(materialData.getBuy()) + (Double.parseDouble(materialData.getOpeningInv())
                                - Double.parseDouble(materialData.getEndingInv())) + Double.parseDouble(materialData.getOutSide()) + Double.parseDouble(materialData.getExport());
                        //计算E燃烧
                        double eCombustion = carbonItemTypeDO.getLowCalorific() * netConsumption * carbonItemTypeDO.getCarbonUnitCalorific() * carbonItemTypeDO.getFuelOxidationRate() / ((double) 44 / 12);
                        // 累加
                        totalCombustion += eCombustion;
                    }
                    // 创建一个CarbonAccountingEmissionsVolumeDO对象
                    CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO = new CarbonAccountingEmissionsVolumeDO();
                    // 设置Materials对象
                    CarbonAccountingEmissionsVolumeDO.Materials materials = new CarbonAccountingEmissionsVolumeDO.Materials();
                    materials.setName("E燃烧：")
                            .setCarbonEmissions(totalCombustion);
                    totalCombustion = 0;
                    // 获取courses列表
                    List<MaterialsDO.Courses> coursesList = materialsDO.getCourses();
                    for (MaterialsDO.Courses courses : coursesList) {
                        //获取材料名称
                        ProcessEmissionFactorDO processEmissionFactorDO = processEmissionFactorDAO.getFactorByName(courses.getName());
                        //获取净消耗量
                        MaterialsDO.Material coursesData = courses.getMaterial();
                        // 计算净消耗量
                        double netConsumption = Double.parseDouble(coursesData.getBuy()) + (Double.parseDouble(coursesData.getOpeningInv())
                                - Double.parseDouble(coursesData.getEndingInv())) + Double.parseDouble(coursesData.getOutSide()) + Double.parseDouble(coursesData.getExport());
                        //计算E过程
                        double eProcess = netConsumption * processEmissionFactorDO.getFactor();
                        // 累加
                        totalCombustion += eProcess;
                    }
                    // 设置Courses对象
                    CarbonAccountingEmissionsVolumeDO.Courses courses = new CarbonAccountingEmissionsVolumeDO.Courses();
                    courses.setName("E过程")
                            .setCarbonEmissions(totalCombustion);
                    carbonAccountingEmissionsVolumeDO.setCourses(courses);
                    totalCombustion = 0;
                    //获取carbonSequestrations列表
                    List<MaterialsDO.CarbonSequestration> carbonSequestrationList = materialsDO.getCarbonSequestrations();
                    for (MaterialsDO.CarbonSequestration carbonSequestration : carbonSequestrationList) {
                        //获取名字
                        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonSequestration.getName());
                        //获取净消耗量
                        MaterialsDO.CarbonSequestration.MaterialSequestration sequestrationData = carbonSequestration.getMaterial();
                        //计算净消耗量
                        double netConsumption = Double.parseDouble(sequestrationData.getExport()) + Double.parseDouble(sequestrationData.getEndingInv())
                                - Double.parseDouble(sequestrationData.getOpeningInv());
                        //计算R固碳
                        double eSequestration = netConsumption * otherEmissionFactorDO.getFactor();
                        //累加
                        totalCombustion += eSequestration;
                    }
                    // 设置CarbonSequestration对象
                    CarbonAccountingEmissionsVolumeDO.CarbonSequestration carbonSequestration = new CarbonAccountingEmissionsVolumeDO.CarbonSequestration();
                    carbonSequestration.setName("固碳排放：")
                            .setCarbonEmissions(totalCombustion);
                    carbonAccountingEmissionsVolumeDO.setCarbonSequestrations(carbonSequestration);
                    totalCombustion = 0;
                    //计算电和热
                    //获取Heat列表
                    List<MaterialsDO.Heat> heatList = materialsDO.getHeat();
                    for (MaterialsDO.Heat heat : heatList) {
                        //获取名字
                        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName("thermalPower");
                        //计算消耗量
                        double netConsumption = Double.parseDouble(heat.getBuy()) - Double.parseDouble(heat.getExport())
                                - Double.parseDouble(heat.getExport());
                        //计算E火力
                        double hCombustion = netConsumption * otherEmissionFactorDO.getFactor();
                        //累加
                        totalCombustion += hCombustion;
                    }
                    //计算电力
                    //获取电力排放因子
                    OtherEmissionFactorDO otherEmissionFactorDO =otherEmissionFactorDAO.getFactorByName(carbonConsumeVO.getElectricCompany());
                    //计算E电力
                    double electricCombustion =(Double.parseDouble(carbonConsumeVO.getElectricBuy()) - Double.parseDouble(carbonConsumeVO.getElectricOutside()) -Double.parseDouble(carbonConsumeVO.getElectricExport()) )
                            * otherEmissionFactorDO.getFactor();
                    //E电和E火
                    totalCombustion += electricCombustion;
                    CarbonAccountingEmissionsVolumeDO.ElectricHeat electricHeat =new CarbonAccountingEmissionsVolumeDO.ElectricHeat();
                    electricHeat.setName("E电和火：")
                            .setElectricHeatEmissions(totalCombustion);
                    carbonAccountingEmissionsVolumeDO.setElectricHeat(electricHeat);
                    return ResultUtil.success(timestamp);
                } else {
                    return ResultUtil.error(timestamp, "初始化碳原料数据表失败", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "初始化碳核算数据表失败", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } else {
            return ResultUtil.error(timestamp, "初始化碳核算报告失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }
}




















