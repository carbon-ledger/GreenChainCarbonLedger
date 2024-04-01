package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.*;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonAddQuotaVO;
import com.frontleaves.greenchaincarbonledger.models.voData.getData.CarbonConsumeVO;
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
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    private final RoleDAO roleDAO;
    private final CarbonAccountingDAO carbonAccountingDAO;
    private final CarbonQuotaDAO carbonQuotaDAO;
    private final CarbonMapper carbonMapper;
    private final Gson gson;
    private final CarbonReportDAO carbonReportDAO;
    private final CarbonTypeDAO carbonTypeDAO;
    private final CarbonCompensationMaterialDAO carbonCompensationMaterialDAO;
    private final CarbonItemTypeDAO carbonItemTypeDAO;
    private final ProcessEmissionFactorDAO processEmissionFactorDAO;
    private final OtherEmissionFactorDAO otherEmissionFactorDAO;

    /**
     * 检查报告时间是否冲突
     * <hr/>
     * 检查组织的报告时间是否出现冲突，例如与上一次报告时间重复，以及开始时间大于结束时间。
     * 不符合时间常理规范的内容将会被拒绝
     *
     * @param getOrganizeUserLastCarbonReport 存放碳排放报告数据表的数据（上一次报告的内容获取）
     * @return 返回是否通过时间重复性检查（不通过为 true 通过为 false）
     */
    @Contract("null, _ -> null")
    private static String checkReportTimeHasDuplicate(CarbonReportDO getOrganizeUserLastCarbonReport, @NotNull CarbonConsumeVO carbonConsumeVO) {
        String getStartTimeReplace = carbonConsumeVO.getStartTime().replace("-", "");
        String getEndTimeReplace = carbonConsumeVO.getEndTime().replace("-", "");
        String getFormatDateRange = getStartTimeReplace + "-" + getEndTimeReplace;
        if (getOrganizeUserLastCarbonReport != null) {
            // 时间字符整理整理
            long nowReportStartTime = Long.parseLong(getStartTimeReplace);
            long nowReportEndTime = Long.parseLong(getEndTimeReplace);
            // 获取时间范围的结束日期
            long lastReportEndTime = Long.parseLong(getOrganizeUserLastCarbonReport.getAccountingPeriod().split("-")[1]);
            // 时间范围检查
            if (nowReportStartTime < nowReportEndTime) {
                if (lastReportEndTime >= nowReportStartTime) {
                    return getFormatDateRange;
                }
            }
        }
        return null;
    }

    /**
     * 计算E脱硫的值
     * <hr/>
     * 计算公式：E脱硫 = 某种脱硫剂中碳酸盐消耗量 X 某种脱硫剂中碳酸盐的排放因子
     *
     * @param desulfurizationComposition 存放企业传入的脱硫剂参数——脱硫剂类型、脱硫剂消耗量
     * @return E脱硫的值
     */
    private static double eDesulfurization(@NotNull List<MaterialsDO.Desulfurization> desulfurizationComposition, ProcessEmissionFactorDAO processEmissionFactorDAO) {
        // E脱硫(脱硫过程产生的所有碳排放)
        double ehCombustion = 0.0;
        for (MaterialsDO.Desulfurization des : desulfurizationComposition) {
            // 碳酸盐类型(前端传入)
            String type = des.name;
            // 脱硫剂消耗量(前端传入)
            double consumption = des.material.consumption;
            // 数据库读取
            DesulfurizationFactorDO desulfurizationFactorDO = processEmissionFactorDAO.getDesFactorByName(type);
            // 脱硫剂中碳酸盐含量
            double carbonateContent = desulfurizationFactorDO.getCarbonateContent();
            double factor = desulfurizationFactorDO.getFactor();
            ehCombustion += consumption * carbonateContent * factor;
        }
        return ehCombustion;
    }

    /**
     * 计算E燃烧的值
     * <hr/>
     * 计算公式：…………
     *
     * @return 返回是否通过时间重复性检查
     */
    private static double eCombustion(@NotNull List<MaterialsDO.Materials> materialsList, CarbonItemTypeDAO carbonItemTypeDAO) {
        double value = 0.0;
        for (MaterialsDO.Materials material : materialsList) {
            // 获取碳排放因子
            CarbonItemTypeDO carbonItemTypeDO = carbonItemTypeDAO.getCarbonItemTypeByName(material.getName());
            // 获取能计算出净消耗量的相关参数
            MaterialsDO.Material materialData = material.getMaterial();
            // 计算净消耗量
            double netConsumption = Double.parseDouble(materialData.getBuy()) + (Double.parseDouble(materialData.getOpeningInv()) - Double.parseDouble(materialData.getEndingInv())) + Double.parseDouble(materialData.getOutSide()) + Double.parseDouble(materialData.getExport());
            double eCombustion = carbonItemTypeDO.getLowCalorific() * netConsumption * carbonItemTypeDO.getCarbonUnitCalorific() * carbonItemTypeDO.getFuelOxidationRate() / ((double) 44 / 12);
            // 累加
            value += eCombustion;
        }
        return value;
    }

    /**
     * 获取附表1中燃烧材料的附表
     */
    private static String[][] combustionConsumption(@NotNull List<MaterialsDO.Materials> materialsList, CarbonItemTypeDAO carbonItemTypeDAO) {
        String[][] result = new String[materialsList.size()][5]; // 二维数组，每个内部数组包含五个信息

        for (int i = 0; i < materialsList.size(); i++) {
            MaterialsDO.Materials material = new MaterialsDO.Materials();
            CarbonItemTypeDO carbonItemTypeDO = carbonItemTypeDAO.getCarbonItemTypeByName(material.getName());
            // 获取信息
            // 名称
            result[i][0] = carbonItemTypeDO.getDisplayName();
            // 净消耗量
            result[i][1] = String.valueOf(Double.parseDouble(material.getMaterial().getBuy()) +
                    (Double.parseDouble(material.getMaterial().getOpeningInv()) - Double.parseDouble(material.getMaterial().getEndingInv())) +
                    Double.parseDouble(material.getMaterial().getOutSide()) +
                    Double.parseDouble(material.getMaterial().getExport()));
            // 低热值
            result[i][2] = String.valueOf(carbonItemTypeDO.getLowCalorific());
            // 碳单元热值
            result[i][3] = String.valueOf(carbonItemTypeDO.getCarbonUnitCalorific());
            // 燃料氧化率
            result[i][4] = String.valueOf(carbonItemTypeDO.getFuelOxidationRate());
        }
        return result;
    }


    /**
     * 计算E电力的值
     * <hr/>
     * 计算公式：
     *
     * @return 返回是否通过时间重复性检查
     */
    private static double electricity(CarbonConsumeVO carbonConsumeVO, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        //获取电力排放因子
        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonConsumeVO.getElectricCompany());
        //计算E电力
        double electricCombustion;
        electricCombustion = (Double.parseDouble(carbonConsumeVO.getElectricBuy()) - Double.parseDouble(carbonConsumeVO.getElectricOutside()) - Double.parseDouble(carbonConsumeVO.getElectricExport())) * otherEmissionFactorDO.getFactor();
        return electricCombustion;
    }

    /**
     * 获取电力附表所需值
     */
    private static String[] electricityCombustion(CarbonConsumeVO carbonConsumeVO, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonConsumeVO.getElectricCompany());
        String displayName = otherEmissionFactorDO.getDisplayName();
        String netCombustion = String.valueOf((Double.parseDouble(carbonConsumeVO.getElectricBuy()) - Double.parseDouble(carbonConsumeVO.getElectricOutside()) - Double.parseDouble(carbonConsumeVO.getElectricExport())));
        String factor = String.valueOf(otherEmissionFactorDO.getFactor());
        return new String[]{displayName, netCombustion, factor};
    }

    /**
     * 计算E过程的值
     *
     * @return E过程的值
     */
    private static double eCousers(List<MaterialsDO.Materials> coursesList, ProcessEmissionFactorDAO processEmissionFactorDAO) {
        double value = 0.0;
        for (MaterialsDO.Materials courses : coursesList) {
            //获取碳排放因子
            ProcessEmissionFactorDO processEmissionFactorDO = processEmissionFactorDAO.getFactorByName(courses.getName());
            // 获取能计算出净消耗量的相关参数
            MaterialsDO.Material materialData = courses.getMaterial();
            // 计算净消耗量
            double netConsumption = Double.parseDouble(materialData.getBuy()) + (Double.parseDouble(materialData.getOpeningInv()) - Double.parseDouble(materialData.getEndingInv())) + Double.parseDouble(materialData.getOutSide()) + Double.parseDouble(materialData.getExport());
            double eCousers = processEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eCousers;
        }
        return value;
    }

    /**
     * 取出E过程附表
     */
    private static String[][] coursesConsumption(@NotNull List<MaterialsDO.Materials> coursesList, ProcessEmissionFactorDAO processEmissionFactorDAO) {
        String[][] result = new String[coursesList.size()][3];
        for (int i = 0; i < coursesList.size(); i++) {
            MaterialsDO.Materials courses = new MaterialsDO.Materials();
            ProcessEmissionFactorDO processEmissionFactorDO = processEmissionFactorDAO.getFactorByName(courses.getName());
            //获取名字
            result[i][0] = processEmissionFactorDO.getDisplayName();
            // 获取能计算出净消耗量的相关参数
            MaterialsDO.Material materialData = courses.getMaterial();
            result[i][1] = String.valueOf(Double.parseDouble(materialData.getBuy()) + (Double.parseDouble(materialData.getOpeningInv()) - Double.parseDouble(materialData.getEndingInv())) + Double.parseDouble(materialData.getOutSide()) + Double.parseDouble(materialData.getExport()));
            result[i][2] = String.valueOf(processEmissionFactorDO.getFactor());
        }
        return result;
    }

    /**
     * 计算R固碳
     *
     * @return R固碳的值
     */
    private static double eCarbonSequestration(List<MaterialsDO.Materials> carbonSequestrationList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        double value = 0.0;
        for (MaterialsDO.Materials carbonSequestration : carbonSequestrationList) {
            //获取排放因子
            OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonSequestration.getName());
            // 获取能计算出净消耗量的相关参数
            MaterialsDO.Material materialData = carbonSequestration.getMaterial();
            //计算净消耗量
            double netConsumption = Double.parseDouble(materialData.getExport()) + Double.parseDouble(materialData.getEndingInv()) - Double.parseDouble(materialData.getOpeningInv());
            double eCarbonSequestration = otherEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eCarbonSequestration;
        }
        return value;
    }

    /**
     * 获取固碳的净消耗量和固碳
     */
    private static String[][] carbonSequestrationConsumption(@NotNull List<MaterialsDO.Materials> carbonSequestrationList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        String[][] result = new String[carbonSequestrationList.size()][3];
        for (int i = 0; i < carbonSequestrationList.size(); i++) {
            MaterialsDO.Materials carbonSequestration = new MaterialsDO.Materials();
            OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonSequestration.getName());
            //获取
            result[i][0] = otherEmissionFactorDO.getDisplayName();
            MaterialsDO.Material materialData = carbonSequestration.getMaterial();
            double netConsumption = Double.parseDouble(materialData.getExport()) + Double.parseDouble(materialData.getEndingInv()) - Double.parseDouble(materialData.getOpeningInv());
            result[i][1] = String.valueOf(netConsumption);
            result[i][2] = String.valueOf(otherEmissionFactorDO.getFactor());
        }
        return result;
    }

    /**
     * 计算E热力的值
     * <hr/>
     * 计算公式：
     *
     * @return 返回是否通过时间重复性检查
     */
    private static double eHeat(List<MaterialsDO.Material> heatList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        double value = 0.0;
        for (MaterialsDO.Material heat : heatList) {
            //获取排放因子
            OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName("thermalPower");
            // 计算得出净消耗量
            double netConsumption = Double.parseDouble(heat.getBuy()) - Double.parseDouble(heat.getExport()) - Double.parseDouble(heat.getOutSide());
            double eHeat = otherEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eHeat;
        }
        return value;
    }

    /**
     * 取出热力的Id和消耗量
     */
    private static String[][] heatConsumption(@NotNull List<MaterialsDO.Material> heatList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        String[][] result = new String[heatList.size()][3];
        for (int i = 0; i < heatList.size(); i++) {
            MaterialsDO.Material heat = new MaterialsDO.Material();
            //获取
            OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName("thermalPower");
           result[i][0] = otherEmissionFactorDO.getDisplayName();
            double netConsumption = Double.parseDouble(heat.getBuy()) - Double.parseDouble(heat.getExport()) - Double.parseDouble(heat.getOutSide());
            result[i][1] = String.valueOf(netConsumption);
            result[i][2] = String.valueOf(otherEmissionFactorDO.getFactor());
        }
        return result;
    }

    /**
     * 为附表中的单元格赋值
     */
    private static void setCellValue(Sheet sheet, int rowIndex, int columnIndex, String value) {
        Row row = sheet.getRow(rowIndex); // 获取要设置数据的行
        if (row == null) {
            row = sheet.createRow(rowIndex); // 如果行不存在，则创建新行
        }
        Cell cell = row.createCell(columnIndex); // 获取要设置数据的单元格
        cell.setCellValue(value); // 设置单元格的值
    }


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
                        .setOrganizeUuid(carbonQuotaDO.getOrganizeUuid())
                        .setAuditLog(gson.fromJson(carbonQuotaDO.getAuditLog(), new TypeToken<ArrayList<AuditLogDO>>() {
                        }.getType()))
                        .setQuotaYear(carbonQuotaDO.getQuotaYear())
                        .setTotalQuota(carbonQuotaDO.getTotalQuota())
                        .setAllocatedQuota(carbonQuotaDO.getAllocatedQuota())
                        .setUsedQuota(carbonQuotaDO.getUsedQuota())
                        .setAllocationDate(carbonQuotaDO.getAllocationDate().toString())
                        .setComplianceStatus(carbonQuotaDO.isComplianceStatus())
                        .setCreatedAt(carbonQuotaDO.getCreatedAt())
                        .setUpdatedAt(carbonQuotaDO.getUpdatedAt());
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
            log.debug("[Service] 校验参数");
            // 检查参数，如果未设置（即为null），则使用默认值
            limit = (limit.isEmpty() || Integer.parseInt(limit) > 100) ? "20" : limit;
            page = (page.isEmpty()) ? "1" : page;
            if (order.isBlank()) {
                order = "ASC";
            }
            log.debug("\t> limit: {}, page: {}, order: {}", limit, page, order);
            //进行type值判断
            log.debug("[Service] 校验type参数");
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
            log.debug("[Service] 整理输出数据");
            ArrayList<BackCarbonReportVO> backCarbonReportList = new ArrayList<>();
            if (getReportList != null) {
                for (CarbonReportDO getReport : getReportList) {
                    BackCarbonReportVO backCarbonReportVO = new BackCarbonReportVO();
                    backCarbonReportVO
                            .setId(getReport.getId())
                            .setOrganizeUuid(getReport.getOrganizeUuid())
                            .setAccountingPeriod(getReport.getAccountingPeriod())
                            .setTotalEmission(getReport.getTotalEmission())
                            .setReportStatus(getReport.getReportStatus())
                            .setListOfReports(getReport.getListOfReports())
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
                        .setEmissionSource(carbonAccountingDO.getEmissionVolume())
                        .setEmissionAmount(carbonAccountingDO.getEmissionAmount())
                        .setAccountingPeriod(carbonAccountingDO.getAccountingPeriod())
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
        CarbonQuotaDO carbonQuotaDO = carbonDAO.getOrganizeQuotaByUuid(getUuid);
        if (carbonQuotaDO != null) {
            // 获取年份
            if (new SimpleDateFormat("yyyy").format(timestamp).equals(carbonQuotaDO.getQuotaYear().toString())) {
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
        }
        return ResultUtil.error(timestamp, "您还未申请碳配额", ErrorCode.RELEASE_TRADE_FAILURE);
    }


    @NotNull
    @Override
    public ResponseEntity<BaseResponse> createCarbonReport(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull CarbonConsumeVO carbonConsumeVO,
            @NotNull List<MaterialsDO.Materials> materials,
            @NotNull List<MaterialsDO.Materials> courses,
            @NotNull List<MaterialsDO.Materials> carbonSequestrations,
            @NotNull List<MaterialsDO.Material> heats
    ) {
        // 从前端获取时间并进行格式化
        CarbonReportDO getOrganizeUserLastCarbonReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        if (checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO) == null) {
            return ResultUtil.error(timestamp, "您此次报告与之前报告冲突或时间范围不正确", ErrorCode.WRONG_DATE);
        }
        // 2. 从VO获取数据向数据库插入此次报告的基本数据
        // 考虑外键约束相关的数据表插入数据顺序：fy_carbon_report、fy_carbon_accounting、fy_carbon_compensation_material
        CarbonTypeDO getCarbonType = carbonTypeDAO.getTypeByName(carbonConsumeVO.getType());
        CarbonReportDO carbonReportDO = new CarbonReportDO();
        carbonReportDO
                .setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request))
                .setReportTitle(carbonConsumeVO.getTitle())
                .setReportType(getCarbonType.getUuid())
                .setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO))
                .setReportStatus("draft")
                .setReportSummary(carbonConsumeVO.getSummary());
        if (!(carbonReportDAO.insertReportMapper(carbonReportDO))) {
            return ResultUtil.error(timestamp, "新增碳核算报告数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算报告数据表
        CarbonReportDO getLastReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        CarbonAccountingDO carbonAccountingDO = new CarbonAccountingDO();
        carbonAccountingDO
                .setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request))
                .setReportId(getLastReport.getId())
                .setEmissionType(getCarbonType.getUuid())
                .setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO))
                .setDataVerificationStatus("pending");
        if (!(carbonAccountingDAO.insertCarbonAccounting(carbonAccountingDO))) {
            return ResultUtil.error(timestamp, "新增碳核算数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算数据表
        CarbonAccountingDO getLastCarbonAccounting = carbonAccountingDAO.getLastCarbonAccountingByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳排放配额原料表中，插入数据
        // 生成准备存放的DO对象
        CarbonCompensationMaterialDO carbonCompensationMaterialDO = new CarbonCompensationMaterialDO();
        ElectricDO electricDO = new ElectricDO();
        electricDO
                .setElectricBuy(carbonConsumeVO.getElectricBuy())
                .setElectricOutside(carbonConsumeVO.getElectricOutside())
                .setElectricCompany(carbonConsumeVO.getElectricCompany())
                .setElectricExport(carbonConsumeVO.getElectricExport());
        // 电力数据
        String electricJson = gson.toJson(electricDO);
        HashMap<String, Object> setMaterials = new HashMap<>();
        setMaterials.put("materials", materials);
        setMaterials.put("courses", courses);
        setMaterials.put("carbonSequestrations", carbonSequestrations);
        setMaterials.put("heats", heats);
        carbonCompensationMaterialDO
                .setAccountingId(getLastCarbonAccounting.getId())
                .setRawMaterial(gson.toJson(setMaterials))
                .setElectricMaterial(electricJson);
        if (!(carbonCompensationMaterialDAO.insertCarbonCompensationMaterial(carbonCompensationMaterialDO))) {
            return ResultUtil.error(timestamp, "新增碳原料数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        /*
         * 1. 计算E燃烧
         * 2. 计算E过程
         * 3. 计算E固碳
         * 4. 计算E热
         * 5. 计算E电力
         */
        double eCombustion = eCombustion(materials, carbonItemTypeDAO);
        double eCourses = eCousers(courses, processEmissionFactorDAO);
        double eCarbonSequestration = eCarbonSequestration(carbonSequestrations, otherEmissionFactorDAO);
        double eHeat = eHeat(heats, otherEmissionFactorDAO);
        double eElectric = electricity(carbonConsumeVO, otherEmissionFactorDAO);
        // 汇总碳排放
        double totalCombustion = eCombustion + eCourses + eElectric + eHeat - eCarbonSequestration;
        // 创建一个DO存储对象
        CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO = new CarbonAccountingEmissionsVolumeDO();
        CarbonAccountingEmissionsVolumeDO.Material material = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material course = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material carbonSequestration = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Heat heat = new CarbonAccountingEmissionsVolumeDO.Heat();
        CarbonAccountingEmissionsVolumeDO.Electric electric = new CarbonAccountingEmissionsVolumeDO.Electric();
        material
                .setName("eCombustion")
                .setCarbonEmissions(eCombustion);
        course
                .setName("eCourse")
                .setCarbonEmissions(eCourses);
        carbonSequestration
                .setName("eCarbonSequestration")
                .setCarbonEmissions(eCarbonSequestration);
        heat
                .setName("eCourse")
                .setHeatEmissions(eHeat);
        electric
                .setName("eElectric")
                .setElectricEmissions(eElectric);
        // 存入总表
        carbonAccountingEmissionsVolumeDO
                .setMaterials(material)
                .setCourses(course)
                .setCarbonSequestrations(carbonSequestration)
                .setHeat(heat)
                .setElectric(electric);
        //读取附表1
        String schedule1;
        try (FileInputStream inputStream = new FileInputStream("AppendixIron1.xlsx")) {
            try (Workbook workbook = new HSSFWorkbook(inputStream)) {
                //获取工作表1
                Sheet sheet1 = workbook.getSheetAt(0);
                // 给第二行第二列的单元格赋值
                setCellValue(sheet1, 1, 1, String.valueOf(totalCombustion));
                // 给第三行第二列的单元格赋值
                setCellValue(sheet1, 2, 1, String.valueOf(eCombustion));
                setCellValue(sheet1, 3, 3, String.valueOf(eCourses));
                setCellValue(sheet1, 4, 1, String.valueOf(eElectric + eHeat));
                setCellValue(sheet1, 5, 1, String.valueOf(eCarbonSequestration));
                //创建附表名称
                schedule1 = ProcessingUtil.createUuid();
                String filePath = "workLoad/" + schedule1 + ".xlsx";
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                    log.info("附表1创建成功");
                } catch (IOException e) {
                    log.error("附表1创建失败", e);
                    return ResultUtil.error(timestamp, "附表1创建失败", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } catch (IOException e) {
                log.error("读取附表1错误", e);
                return ResultUtil.error(timestamp, "读取附表1错误", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } catch (IOException e) {
            log.error("读取模板附表1错误", e);
            return ResultUtil.error(timestamp, "读取模板附表1错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        //取出E材料燃烧消耗和ID
        String[][] combustionConsumption = combustionConsumption(materials, carbonItemTypeDAO);
        //取出E过程的消耗量
        String[][] courseConsumption = coursesConsumption(materials, processEmissionFactorDAO);
        String[] electricityCombustion = electricityCombustion(carbonConsumeVO, otherEmissionFactorDAO);
        String[][] heatConsumption = heatConsumption(heats, otherEmissionFactorDAO);
        String[][] carbonSequestrationConsumption = carbonSequestrationConsumption(carbonSequestrations, otherEmissionFactorDAO);
        String schedule2;
        //读取附表2
        try (FileInputStream inputStream = new FileInputStream("AppendixIron2.xlsx")) {
            try (Workbook workbook = new HSSFWorkbook(inputStream)) {
                //读取工作表1
                Sheet sheet2 = workbook.getSheetAt(0);
                // 获取 Excel 中的数据并进行匹配
                for (int i = 3; i < 24; i++) {
                    // 获取当前行
                    Row row = sheet2.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue();
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : combustionConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet2, i, 2, materialInfo[1]);
                            setCellValue(sheet2, i, 3, materialInfo[2]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //给E过程材料赋值
                // 获取 Excel 中的数据并进行匹配
                for (int i = 26; i < 33; i++) {
                    // 获取当前行
                    Row row = sheet2.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue() + "消耗量";
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : courseConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet2, i, 2, materialInfo[1]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //给E电消耗赋值
                setCellValue(sheet2, 35, 2, electricityCombustion[1]);
                //给热力赋值
                setCellValue(sheet2, 36, 2, Arrays.toString(heatConsumption[1]));
                //给固碳赋值
                // 获取 Excel 中的数据并进行匹配
                for (int i = 38; i < 41; i++) {
                    // 获取当前行
                    Row row = sheet2.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue() + "购入量";
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : carbonSequestrationConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet2, i, 2, materialInfo[1]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //创建附表名称
                schedule2 = ProcessingUtil.createUuid();
                String filePath = "workLoad/" + schedule2 + ".xlsx";
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                    log.info("附表2创建成功");
                } catch (IOException e) {
                    log.error("附表2创建失败", e);
                    return ResultUtil.error(timestamp, "附表2创建失败", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } catch (IOException e) {
                log.error("读取附表2错误");
                return ResultUtil.error(timestamp, "读取附表2错误", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } catch (IOException e) {
            log.error("读取模板附表2错误");
            return ResultUtil.error(timestamp, "读取模板附表2错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        String schedule3;
        //读取附表3
        try (FileInputStream inputStream = new FileInputStream("AppendixIron3.xlsx")) {
            try (Workbook workbook = new HSSFWorkbook(inputStream)) {
                Sheet sheet3 = workbook.getSheetAt(0);
                //为燃烧赋值
                // 获取 Excel 中的数据并进行匹配
                for (int i = 3; i < 24; i++) {
                    // 获取当前行
                    Row row = sheet3.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue();
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : combustionConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet3, i, 2, materialInfo[3]);
                            setCellValue(sheet3, i, 3, materialInfo[4]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //为过程赋值
                for (int i = 26; i < 33; i++) {
                    // 获取当前行
                    Row row = sheet3.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue();
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : courseConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet3, i, 2, materialInfo[2]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //给E电消耗赋值
                setCellValue(sheet3, 35, 2, electricityCombustion[2]);
                //给热力赋值
                setCellValue(sheet3, 36, 2, Arrays.toString(heatConsumption[2]));
                //为固碳赋值
                // 获取 Excel 中的数据并进行匹配
                for (int i = 38; i < 41; i++) {
                    // 获取当前行
                    Row row = sheet3.getRow(i);
                    // 获取当前行的第二列单元格
                    Cell cell = row.getCell(1);
                    // 获取当前单元格的值
                    String excelName = cell.getStringCellValue();
                    // 遍历数组，匹配名称
                    for (String[] materialInfo : carbonSequestrationConsumption) {
                        String arrayName = materialInfo[0];
                        // 如果 Excel 中的名称与数组中的名称匹配成功
                        if (excelName.equals(arrayName)) {
                            // 将数组中的第二个值填入当前行的数据中
                            setCellValue(sheet3, i, 2, materialInfo[2]);
                            // 匹配成功后跳出内层循环
                            break;
                        }
                    }
                }
                //创建附表名称
                schedule3 = ProcessingUtil.createUuid();
                String filePath = "workLoad/" + schedule3 + ".xlsx";
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                    log.info("附表3创建成功");
                } catch (IOException e) {
                    log.error("附表3创建失败", e);
                    return ResultUtil.error(timestamp, "附表3创建失败", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } catch (IOException e) {
                log.error("读取附表3错误", e);
                return ResultUtil.error(timestamp, "读取附表3错误", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } catch (IOException e) {
            log.error("读取模板附表3错误", e);
            return ResultUtil.error(timestamp, "读取模板附表3错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        //整理3个文件的链接
        ArrayList<String> listOrReports = new ArrayList<>();
        listOrReports.add(schedule1 + ".xlsx");
        listOrReports.add(schedule2 + ".xlsx");
        listOrReports.add(schedule3 + "xlsx");
        // 更新碳核算报告数据表——修正碳总排放量
        return getBaseResponseResponseEntity(timestamp, carbonConsumeVO, getLastReport, getLastCarbonAccounting, totalCombustion, carbonAccountingEmissionsVolumeDO, listOrReports);
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> createCarbonReport1(
            long timestamp,
            @NotNull HttpServletRequest request,
            @NotNull CarbonConsumeVO carbonConsumeVO,
            @NotNull List<MaterialsDO.Materials> materials,
            @NotNull List<MaterialsDO.Desulfurization> desulfurization
    ) {
        // 从数据库获取上一份报告的数据，准备进行比较
        CarbonReportDO getOrganizeUserLastCarbonReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 使用静态方法检查时间冲突
        if (checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO) == null) {
            return ResultUtil.error(timestamp, "您此次报告与之前报告冲突或时间范围不正确", ErrorCode.WRONG_DATE);
        }
        // 2. 从VO获取数据向数据库插入此次报告的基本数据
        // 考虑外键约束相关的数据表插入数据顺序：fy_carbon_report、fy_carbon_accounting、fy_carbon_compensation_material
        CarbonTypeDO getCarbonType = carbonTypeDAO.getTypeByName("generateElectricity");
        CarbonReportDO carbonReportDO = new CarbonReportDO();
        carbonReportDO
                .setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request))
                .setReportTitle(carbonConsumeVO.getTitle())
                .setReportType(getCarbonType.getUuid())
                .setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO))
                .setReportStatus("draft")
                .setReportSummary(carbonConsumeVO.getSummary());
        if (!(carbonReportDAO.insertReportMapper(carbonReportDO))) {
            return ResultUtil.error(timestamp, "新增碳核算报告数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算报告数据表
        CarbonReportDO getLastReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳核算数据表中，插入数据
        // 生成准备存放的DO对象
        CarbonAccountingDO carbonAccountingDO = new CarbonAccountingDO();
        carbonAccountingDO
                .setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request))
                .setReportId(getLastReport.getId())
                .setEmissionType(getCarbonType.getUuid())
                .setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO))
                .setDataVerificationStatus("pending");
        if (!(carbonAccountingDAO.insertCarbonAccounting(carbonAccountingDO))) {
            return ResultUtil.error(timestamp, "新增碳核算数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算数据表
        CarbonAccountingDO getLastCarbonAccounting = carbonAccountingDAO.getLastCarbonAccountingByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳排放配额原料表中，插入数据
        // 生成准备存放的DO对象
        CarbonCompensationMaterialDO carbonCompensationMaterialDO = new CarbonCompensationMaterialDO();
        ElectricDO electricDO = new ElectricDO();
        electricDO
                .setElectricBuy(carbonConsumeVO.getElectricBuy())
                .setElectricOutside(carbonConsumeVO.getElectricOutside())
                .setElectricCompany(carbonConsumeVO.getElectricCompany())
                .setElectricExport(carbonConsumeVO.getElectricExport());
        // 电力数据
        String electric1 = gson.toJson(electricDO);
        HashMap<String, Object> setMaterials = new HashMap<>();
        setMaterials.put("materials", materials);
        setMaterials.put("desulfurization", desulfurization);
        carbonCompensationMaterialDO
                .setAccountingId(getLastCarbonAccounting.getId())
                .setRawMaterial(gson.toJson(setMaterials))
                .setElectricMaterial(electric1);
        if (!(carbonCompensationMaterialDAO.insertCarbonCompensationMaterial(carbonCompensationMaterialDO))) {
            return ResultUtil.error(timestamp, "新增碳原料数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        /*
         * 1. 计算E燃烧
         * 2. 计算E脱硫
         * 3. 计算E电力
         */
        double eCombustion = eCombustion(materials, carbonItemTypeDAO);
        double eDesulfurization = eDesulfurization(desulfurization, processEmissionFactorDAO);
        double eElectric = electricity(carbonConsumeVO, otherEmissionFactorDAO);
        // 汇总碳排放
        double totalCombustion = eCombustion + eDesulfurization + eElectric;
        CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO = new CarbonAccountingEmissionsVolumeDO();
        CarbonAccountingEmissionsVolumeDO.Material material = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material desulfuization = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Electric electric = new CarbonAccountingEmissionsVolumeDO.Electric();
        material
                .setName("eCombustion")
                .setCarbonEmissions(eCombustion);
        desulfuization
                .setName("eDesulfurization")
                .setCarbonEmissions(eDesulfurization);
        electric
                .setName("eElectric")
                .setElectricEmissions(eElectric);

        carbonAccountingEmissionsVolumeDO
                .setMaterials(material)
                .setDesulfuizations(desulfuization)
                .setElectric(electric);
        ArrayList<String> listOrReports = new ArrayList<>();
        listOrReports.add("11111");
        // 更新碳核算报告数据表——修正碳总排放量
        return getBaseResponseResponseEntity(timestamp, carbonConsumeVO, getLastReport, getLastCarbonAccounting, totalCombustion, carbonAccountingEmissionsVolumeDO, listOrReports);
    }

    @NotNull
    private ResponseEntity<BaseResponse> getBaseResponseResponseEntity(
            long timestamp, @NotNull CarbonConsumeVO carbonConsumeVO, CarbonReportDO getLastReport, @NotNull CarbonAccountingDO getLastCarbonAccounting, double totalCombustion, CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO, ArrayList<String> listOrReports) {
        if (!(carbonAccountingDAO.updateEmissionByUuidId(gson.toJson(carbonAccountingEmissionsVolumeDO), totalCombustion, getLastCarbonAccounting.getId()))) {
            return ResultUtil.error(timestamp, "更新碳核算数据表错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        if (carbonConsumeVO.getSend()) {
            //进入待审状态
            if (carbonReportDAO.updateEmissionById(totalCombustion, "pending_review", getLastReport.getId(), gson.toJson(listOrReports))) {
                return ResultUtil.success(timestamp, "您的碳核算报告已经成功创建");
            }
        } else {
            //进入草稿状态
            if (carbonReportDAO.updateEmissionById(totalCombustion, "draft", getLastReport.getId(), gson.toJson(listOrReports))) {
                return ResultUtil.success(timestamp, "您的碳核算报告已经成功创建");
            }
        }
        return ResultUtil.error(timestamp, "更新碳核算报告失败", ErrorCode.SERVER_INTERNAL_ERROR);
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> addOrganizeIdQuota(long timestamp, @NotNull HttpServletRequest request, @NotNull String organizeId, @NotNull CarbonAddQuotaVO carbonAddQuotaVO) {
        //校验organizeId是否存在
        UserDO getOrganizeId = userDAO.getUserByUuid(organizeId);
        if (getOrganizeId == null) {
            return ResultUtil.error(timestamp, "抱歉您要添加的组织不存在", ErrorCode.UUID_NOT_EXIST);
        }
        //校验要添加的账号角色是否为组织账号角色
        RoleDO getRoleDO = roleDAO.getRoleByUuid(getOrganizeId.getRole());
        if (!"organize".equals(getRoleDO.getName())) {
            return ResultUtil.error(timestamp, "您只能为组织添加碳配额", ErrorCode.NO_PERMISSION_ERROR);
        }
        //提取年份
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        int localYear = Integer.parseInt(simpleDateFormat.format(timestamp));
        String combinedDate = new SimpleDateFormat("yyyy-MM-dd").format(timestamp);
        //校验是否在同一年已经创建了碳配额
        if (carbonQuotaDAO.getCarbonQuota(localYear, organizeId) != null) {
            return ResultUtil.error(timestamp, "请勿重复创建碳配额", ErrorCode.DUPLICATE_CREATE);
        }
        //创建此时今年的碳排放配额
        //编辑审计日志
        UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
        ArrayList<CarbonAuditLogDO> carbonAuditLogList = new ArrayList<>();
        CarbonAuditLogDO carbonAuditLog = new CarbonAuditLogDO();
        if (getUserDO != null) {
            carbonAuditLog
                    .setDate(combinedDate)
                    .setLog("添加 " + carbonAddQuotaVO.getQuota() + " 的交易配额，此次为初建碳排放配额表")
                    .setOperate(getUserDO.getUserName());
        }
        carbonAuditLogList.add(carbonAuditLog);
        //整理数据
        CarbonQuotaDO carbonQuotaDO = new CarbonQuotaDO();
        carbonQuotaDO.setUuid(ProcessingUtil.createUuid())
                .setOrganizeUuid(organizeId)
                .setQuotaYear(localYear)
                .setTotalQuota(Double.parseDouble(carbonAddQuotaVO.getQuota()))
                .setAllocatedQuota(Double.parseDouble(carbonAddQuotaVO.getQuota()))
                .setUsedQuota(0)
                .setAllocationDate(new Date(timestamp))
                .setComplianceStatus(!carbonAddQuotaVO.getStatus())
                .setAuditLog(gson.toJson(carbonAuditLogList));
        //进行数据库
        if (carbonQuotaDAO.createCarbonQuota(carbonQuotaDO)) {
            return ResultUtil.success(timestamp, "您已成功添加碳排放配额");
        } else {
            return ResultUtil.error(timestamp, "添加碳排放配额失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<BaseResponse> editCarbonQuota(long timestamp, @NotNull HttpServletRequest request, @NotNull String organizeId, @NotNull CarbonAddQuotaVO carbonAddQuotaVO) {
        //获取当前年份
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        int localYear = Integer.parseInt(simpleDateFormat.format(timestamp));
        // 将年份、月份和日期合并
        log.debug("[Service]时间的合并");
        SimpleDateFormat combinedFormat = new SimpleDateFormat("yyyy-MM-dd");
        String combinedDate = combinedFormat.format(timestamp);
        //查找组织uuid
        CarbonQuotaDO getCarbonQuota = carbonQuotaDAO.getCarbonQuota(localYear, organizeId);
        if (getCarbonQuota != null) {
            //找到后进行修改
            //进行总额配额的修改
            //校验传进来的是正还是负值
            log.debug("[Service]碳配额的添加的计算");
            double carbonTotalQuota;
            if (Double.parseDouble(carbonAddQuotaVO.getQuota()) >= 0) {
                carbonTotalQuota = getCarbonQuota.getTotalQuota() + Double.parseDouble(carbonAddQuotaVO.getQuota());
            } else {
                carbonTotalQuota = getCarbonQuota.getTotalQuota() - Double.parseDouble(carbonAddQuotaVO.getQuota());
            }
            ArrayList<CarbonAuditLogDO> oldCarbonAuditLogList = gson.fromJson(getCarbonQuota.getAuditLog(), new TypeToken<ArrayList<CarbonAuditLogDO>>() {
            }.getType());
            UserDO getUserDO = ProcessingUtil.getUserByHeaderUuid(request, userDAO);
            CarbonAuditLogDO newCarbonAuditLog = new CarbonAuditLogDO();
            if (getUserDO != null) {
                log.debug("[Service]审计日志添加");
                newCarbonAuditLog.setDate(combinedDate)
                        .setLog("进行碳配额的修改 " + Double.parseDouble(carbonAddQuotaVO.getQuota()))
                        .setOperate(getUserDO.getUserName());
                oldCarbonAuditLogList.add(newCarbonAuditLog);
                String carbonAuditLog = gson.toJson(oldCarbonAuditLogList);
                //整理更新数据
                getCarbonQuota.setTotalQuota(carbonTotalQuota)
                        .setComplianceStatus(!carbonAddQuotaVO.getStatus())
                        .setQuotaYear(localYear)
                        .setAuditLog(carbonAuditLog);
                if (carbonQuotaDAO.editCarbonQuota(getCarbonQuota)) {
                    return ResultUtil.success(timestamp, "修改成功");
                } else {
                    return ResultUtil.error(timestamp, "修改失败", ErrorCode.SERVER_INTERNAL_ERROR);
                }
            } else {
                return ResultUtil.error(timestamp, "抱歉用户不存在", ErrorCode.UUID_NOT_EXIST);
            }
        } else {
            return ResultUtil.error(timestamp, "请检查要修改的组织id", ErrorCode.ID_ERROR);
        }

    }
}