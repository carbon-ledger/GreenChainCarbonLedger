package com.frontleaves.greenchaincarbonledger.services.impl;

import com.frontleaves.greenchaincarbonledger.dao.*;
import com.frontleaves.greenchaincarbonledger.mappers.CarbonMapper;
import com.frontleaves.greenchaincarbonledger.models.doData.*;
import com.frontleaves.greenchaincarbonledger.models.doData.excel.*;
import com.frontleaves.greenchaincarbonledger.models.doData.ExcelData.*;
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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

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
        } else {
            return getFormatDateRange;
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

    private static List<List<List<String>>> desulfurizationData(@NotNull List<MaterialsDO.Desulfurization> desulfurizationComposition, ProcessEmissionFactorDAO processEmissionFactorDAO){
        List<List<String>> list2 = new ArrayList<>();
        List<List<String>> list3 = new ArrayList<>();
        for (MaterialsDO.Desulfurization des : desulfurizationComposition) {
            List<String> list22 = new ArrayList<>();
            List<String> list33 = new ArrayList<>();
            list22.add("脱硫过程");
            list33.add("脱硫过程");
            // 脱硫剂名称
            list22.add(des.name + "消耗量");
            list33.add(des.name + "的排放因子");
            // 脱硫剂消耗量(前端传入)
            list22.add(String.valueOf(des.material.consumption));
            // 数据库读取
            DesulfurizationFactorDO desulfurizationFactorDO = processEmissionFactorDAO.getDesFactorByName(des.name);
            // 脱硫剂排放因子
            double factor = desulfurizationFactorDO.getFactor();
            list33.add(String.valueOf(factor));
            // 单位
            list22.add("t");
            list33.add("tCO2/t");
            list2.add(list22);
            list3.add(list33);
        }
        List<List<List<String>>> list = new ArrayList<>();
        list.add(list2);
        list.add(list3);
        return list;
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
            double netConsumption = materialData.getBuy() + materialData.getOpeningInv() - materialData.getEndingInv() + materialData.getOutside() + materialData.getExport();
            double eCombustion = carbonItemTypeDO.getLowCalorific() * netConsumption * carbonItemTypeDO.getCarbonUnitCalorific() * carbonItemTypeDO.getFuelOxidationRate() / ((double) 44 / 12);
            // 累加
            value += eCombustion;
        }
        return value;
    }
    /**
    * 计算附表二和三种中的化石燃料的相关数据
    * */
    private static List<List<List<String>>> fuelData(@NotNull List<MaterialsDO.Materials> materialsList, CarbonItemTypeDAO carbonItemTypeDAO){
        List<List<String>> list2 = new ArrayList<>();
        List<List<String>> list3 = new ArrayList<>();
        for (MaterialsDO.Materials material : materialsList) {
            List<String> list22 = new ArrayList<>();
            List<String> list33 = new ArrayList<>();
            list22.add("化石燃料燃烧");
            list33.add("化石燃料燃烧");
            String name = carbonItemTypeDAO.getCarbonItemTypeByName(material.getName()).getDisplayName();
            list22.add(name);
            list33.add(name);
            // 获取碳排放因子
            CarbonItemTypeDO carbonItemTypeDO = carbonItemTypeDAO.getCarbonItemTypeByName(name);
            // 获取能计算出净消耗量的相关参数
            MaterialsDO.Material materialData = material.getMaterial();
            // 计算净消耗量
            double netConsumption = materialData.getBuy() + materialData.getOpeningInv() - materialData.getEndingInv() + materialData.getOutside() + materialData.getExport();
            // 添加净消耗量
            list22.add(String.valueOf(netConsumption));
            // 添加低位发热量
            list22.add(String.valueOf(carbonItemTypeDO.getLowCalorific()));
            // 添加单位热值含碳量
            list33.add(String.valueOf(carbonItemTypeDO.getCarbonUnitCalorific()));
            // 添加碳氧化率
            list33.add(String.valueOf(carbonItemTypeDO.getFuelOxidationRate()));

            list2.add(list22);
            list3.add(list33);
        }
        List<List<List<String>>> list = new ArrayList<>();
        list.add(list2);
        list.add(list3);
        return list;
    }




    /**
     * 获取附表1中燃烧材料的附表
     */
    private static ArrayList<CombustionConsumptionDO> combustionConsumptionList(@NotNull List<MaterialsDO.Materials> materialsList, CarbonItemTypeDAO carbonItemTypeDAO) {
        ArrayList<CombustionConsumptionDO> resultList = new ArrayList<>();
        for (MaterialsDO.Materials material : materialsList) {
            CombustionConsumptionDO combustionConsumptionDO = new CombustionConsumptionDO();
            CarbonItemTypeDO carbonItemTypeDO = carbonItemTypeDAO.getCarbonItemTypeByName(material.getName());
            MaterialsDO.Material materialData = material.getMaterial();
            double netConsumption = materialData.getBuy() + materialData.getOpeningInv() - materialData.getEndingInv() + materialData.getOutside() + materialData.getExport();
            //获取信息
            combustionConsumptionDO
                    .setDisplayName(carbonItemTypeDO.getDisplayName())
                    .setNetConsumption(String.valueOf(netConsumption))
                    .setLowCalorific(String.valueOf(carbonItemTypeDO.getLowCalorific()))
                    .setCarbonUnitCalorific(String.valueOf(carbonItemTypeDO.getCarbonUnitCalorific()))
                    .setFuelOxidationRate(String.valueOf(carbonItemTypeDO.getFuelOxidationRate()));
            resultList.add(combustionConsumptionDO);
        }
        return resultList;
    }


    /**
     * 计算E电力的值
     * <hr/>
     * 计算公式：
     *
     * @return 电力产生的碳
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
    private static ElectricityCombustionDO electricityCombustion(CarbonConsumeVO carbonConsumeVO, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        ElectricityCombustionDO electricityCombustionDO = new ElectricityCombustionDO();
        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonConsumeVO.getElectricCompany());
        electricityCombustionDO
                .setDisplayName(carbonConsumeVO.getElectricCompany())
                .setNetCombustion(String.valueOf((Double.parseDouble(carbonConsumeVO.getElectricBuy())
                        - Double.parseDouble(carbonConsumeVO.getElectricOutside()) - Double.parseDouble(carbonConsumeVO.getElectricExport()))))
                .setFactor(String.valueOf(otherEmissionFactorDO.getFactor()));
        return electricityCombustionDO;
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
            double netConsumption = materialData.getBuy() + (materialData.getOpeningInv() - materialData.getEndingInv()) + materialData.getOutside() + materialData.getExport();
            double eCousers = processEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eCousers;
        }
        return value;
    }

    /**
     * 取出E过程附表
     */
    private static ArrayList<CoursesConsumptionDO> coursesConsumptionList(@NotNull List<MaterialsDO.Materials> coursesList, ProcessEmissionFactorDAO processEmissionFactorDAO) {
        ArrayList<CoursesConsumptionDO> resultList = new ArrayList<>();
        for (MaterialsDO.Materials courses : coursesList) {
            CoursesConsumptionDO coursesConsumptionDO = new CoursesConsumptionDO();
            ProcessEmissionFactorDO processEmissionFactorDO = processEmissionFactorDAO.getFactorByName(courses.getName());
            MaterialsDO.Material materialData = courses.getMaterial();
            double netConsumption = materialData.getBuy() + (materialData.getOpeningInv() - materialData.getEndingInv()) + materialData.getOutside() + materialData.getExport();
            coursesConsumptionDO
                    .setDisplayName(processEmissionFactorDO.getDisplayName())
                    .setNetConsumption(String.valueOf(netConsumption))
                    .setFactor(String.valueOf(processEmissionFactorDO.getFactor()));
            resultList.add(coursesConsumptionDO);
        }
        return resultList;
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
            double netConsumption = materialData.getExport() + materialData.getEndingInv() - materialData.getOpeningInv();
            double eCarbonSequestration = otherEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eCarbonSequestration;
        }
        return value;
    }

    /**
     * 获取固碳的净消耗量和固碳
     */
    private static ArrayList<CarbonSequestrationConsumptionDO> carbonSequestrationConsumptionList(@NotNull List<MaterialsDO.Materials> carbonSequestrationList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        ArrayList<CarbonSequestrationConsumptionDO> resultList = new ArrayList<>();
        for (MaterialsDO.Materials carbonSequestration : carbonSequestrationList){
            CarbonSequestrationConsumptionDO carbonSequestrationConsumptionDO =new CarbonSequestrationConsumptionDO();
            OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonSequestration.getName());
            MaterialsDO.Material materialData = carbonSequestration.getMaterial();
            double netConsumption = materialData.getExport() + materialData.getEndingInv() - materialData.getOpeningInv();
            carbonSequestrationConsumptionDO
                    .setDisplayName(otherEmissionFactorDO.getDisplayName())
                    .setNetConsumption(String.valueOf(netConsumption))
                    .setFactor(String.valueOf(otherEmissionFactorDO.getFactor()));
            resultList.add(carbonSequestrationConsumptionDO);
        }
        return resultList;
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
            double netConsumption = heat.getBuy() - heat.getExport() - heat.getOutside();
            double eHeat = otherEmissionFactorDO.getFactor() * netConsumption;
            //累加
            value += eHeat;
        }
        return value;
    }

    /**
     * 取出热力的Id和消耗量
     */
    private static HeatConsumptionDO heatConsumption(@NotNull List<MaterialsDO.Material> heatList, OtherEmissionFactorDAO otherEmissionFactorDAO) {
        HeatConsumptionDO heatConsumptionDO = new HeatConsumptionDO();
        MaterialsDO.Material heat = heatList.get(0);
        OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName("thermalPower");
        double netConsumption = heat.getBuy() - heat.getExport() - heat.getOutside();
        heatConsumptionDO
                .setDisplayName(otherEmissionFactorDO.getDisplayName())
                .setNetConsumption(String.valueOf(netConsumption))
                .setFactor(String.valueOf(otherEmissionFactorDO.getFactor()));
        return heatConsumptionDO;
    }

    /**
     * 为附表中的单元格赋值
     */
    private static void setCellValue(Sheet sheet, int rowIndex, int columnIndex, String value) {
        // 获取要设置数据的行
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            // 如果行不存在，则创建新行
            row = sheet.createRow(rowIndex);
        }
        // 获取要设置数据的单元格
        Cell cell = row.createCell(columnIndex);
        // 设置单元格的值
        cell.setCellValue(value);
    }

    /**
     * 合并单元格并且赋值
     */
    // 方法用于合并单元格并设置单元格的值和居中对齐
    private static void mergeCellsAndSetValue(Sheet sheet, int startRow, int endRow, int startColumn, int endColumn, String value) {
        // 合并单元格
        sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, startColumn, endColumn));

        // 设置合并后单元格的值和样式
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        for (int i = startRow; i <= endRow; i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(startColumn);
            if (cell == null) {
                cell = row.createCell(startColumn);
            }
            cell.setCellValue(value);
            cell.setCellStyle(style);
        }
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
    public ResponseEntity<BaseResponse> getCarbonReport(long timestamp, @NotNull HttpServletRequest request, @NotNull String type, @NotNull String search, @NotNull String limit, @NotNull String page, String order) {
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
                backCarbonAccountingVO.setId(carbonAccountingDO.getId()).setOrganizeUuid(carbonAccountingDO.getOrganizeUuid()).setEmissionSource(carbonAccountingDO.getEmissionVolume()).setEmissionAmount(carbonAccountingDO.getEmissionAmount()).setAccountingPeriod(carbonAccountingDO.getAccountingPeriod()).setDataVerificationStatus(carbonAccountingDO.getDataVerificationStatus()).setCreateAt(carbonAccountingDO.getCreatedAt()).setUpdateAt(carbonAccountingDO.getUpdatedAt());
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
    public ResponseEntity<BaseResponse> createCarbonReport(long timestamp, @NotNull HttpServletRequest request, @NotNull CarbonConsumeVO carbonConsumeVO, @NotNull List<MaterialsDO.Materials> materials, @NotNull List<MaterialsDO.Materials> courses, @NotNull List<MaterialsDO.Materials> carbonSequestrations, @NotNull List<MaterialsDO.Material> heats) {
        // 从前端获取时间并进行格式化
        CarbonReportDO getOrganizeUserLastCarbonReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        if (checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO) == null) {
            return ResultUtil.error(timestamp, "您此次报告与之前报告冲突或时间范围不正确", ErrorCode.WRONG_DATE);
        }
        // 2. 从VO获取数据向数据库插入此次报告的基本数据
        // 考虑外键约束相关的数据表插入数据顺序：fy_carbon_report、fy_carbon_accounting、fy_carbon_compensation_material
        CarbonTypeDO getCarbonType = carbonTypeDAO.getTypeByName(carbonConsumeVO.getType());
        CarbonReportDO carbonReportDO = new CarbonReportDO();
        carbonReportDO.setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request)).setReportTitle(carbonConsumeVO.getTitle()).setReportType(getCarbonType.getUuid()).setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO)).setReportStatus("draft").setReportSummary(carbonConsumeVO.getSummary());
        if (!(carbonReportDAO.insertReportMapper(carbonReportDO))) {
            return ResultUtil.error(timestamp, "新增碳核算报告数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算报告数据表
        CarbonReportDO getLastReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        CarbonAccountingDO carbonAccountingDO = new CarbonAccountingDO();
        carbonAccountingDO.setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request)).setReportId(getLastReport.getId()).setEmissionType(getCarbonType.getUuid()).setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO)).setDataVerificationStatus("pending");
        if (!(carbonAccountingDAO.insertCarbonAccounting(carbonAccountingDO))) {
            return ResultUtil.error(timestamp, "新增碳核算数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算数据表
        CarbonAccountingDO getLastCarbonAccounting = carbonAccountingDAO.getLastCarbonAccountingByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳排放配额原料表中，插入数据
        // 生成准备存放的DO对象
        CarbonCompensationMaterialDO carbonCompensationMaterialDO = new CarbonCompensationMaterialDO();
        ElectricDO electricDO = new ElectricDO();
        electricDO.setElectricBuy(carbonConsumeVO.getElectricBuy()).setElectricOutside(carbonConsumeVO.getElectricOutside()).setElectricCompany(carbonConsumeVO.getElectricCompany()).setElectricExport(carbonConsumeVO.getElectricExport());
        // 电力数据
        String electricJson = gson.toJson(electricDO);
        HashMap<String, Object> setMaterials = new HashMap<>();
        setMaterials.put("materials", materials);
        setMaterials.put("courses", courses);
        setMaterials.put("carbonSequestrations", carbonSequestrations);
        setMaterials.put("heats", heats);
        carbonCompensationMaterialDO.setAccountingId(getLastCarbonAccounting.getId()).setRawMaterial(gson.toJson(setMaterials)).setElectricMaterial(electricJson);
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
        if (eCombustion < 0) {
            return ResultUtil.error(timestamp, "请检查燃烧消耗量输入值", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eCourses = eCousers(courses, processEmissionFactorDAO);
        if (eCourses < 0) {
            return ResultUtil.error(timestamp, "请检查过程消耗量输入值", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eCarbonSequestration = eCarbonSequestration(carbonSequestrations, otherEmissionFactorDAO);
        if (eCarbonSequestration < 0) {
            return ResultUtil.error(timestamp, "请检查固碳消耗量输入值", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eHeat = eHeat(heats, otherEmissionFactorDAO);
        if (eHeat < 0) {
            return ResultUtil.error(timestamp, "请检查热力材料消耗输入值", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eElectric = electricity(carbonConsumeVO, otherEmissionFactorDAO);
        if (eElectric < 0) {
            return ResultUtil.error(timestamp, "请检检查电力消耗值", ErrorCode.REQUEST_BODY_ERROR);
        }
        // 汇总碳排放
        double totalCombustion = eCombustion + eCourses + eElectric + eHeat - eCarbonSequestration;
        // 创建一个DO存储对象
        CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO = new CarbonAccountingEmissionsVolumeDO();
        CarbonAccountingEmissionsVolumeDO.Material material = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material course = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material carbonSequestration = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Heat heat = new CarbonAccountingEmissionsVolumeDO.Heat();
        CarbonAccountingEmissionsVolumeDO.Electric electric = new CarbonAccountingEmissionsVolumeDO.Electric();
        material.setName("eCombustion").setCarbonEmissions(eCombustion);
        course.setName("eCourse").setCarbonEmissions(eCourses);
        carbonSequestration.setName("eCarbonSequestration").setCarbonEmissions(eCarbonSequestration);
        heat.setName("eCourse").setHeatEmissions(eHeat);
        electric.setName("eElectric").setElectricEmissions(eElectric);
        // 存入总表
        carbonAccountingEmissionsVolumeDO
                .setMaterials(material)
                .setCourses(course)
                .setCarbonSequestrations(carbonSequestration)
                .setHeat(heat)
                .setElectric(electric);
        //读取附表1
        String schedule1;
        try (InputStream inputStream = new ClassPathResource("files/AppendixIron1.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                //获取工作表1
                Sheet sheet1 = workbook.getSheetAt(0);
                // 给第二行第二列的单元格赋值
                setCellValue(sheet1, 1, 1, String.valueOf(totalCombustion));
                // 给第三行第二列的单元格赋值
                setCellValue(sheet1, 2, 1, String.valueOf(eCombustion));
                setCellValue(sheet1, 3, 1, String.valueOf(eCourses));
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
        ArrayList<CombustionConsumptionDO> combustionConsumptionList = combustionConsumptionList(materials, carbonItemTypeDAO);
        //取出E过程的消耗量
        ArrayList<CoursesConsumptionDO> coursesConsumptionList = coursesConsumptionList(courses, processEmissionFactorDAO);
        ElectricityCombustionDO electricityCombustion = electricityCombustion(carbonConsumeVO, otherEmissionFactorDAO);
        HeatConsumptionDO heatConsumption = heatConsumption(heats, otherEmissionFactorDAO);
        ArrayList<CarbonSequestrationConsumptionDO> carbonSequestrationConsumption = carbonSequestrationConsumptionList(carbonSequestrations, otherEmissionFactorDAO);
        String schedule2;
        //读取附表2
        try (InputStream inputStream = new ClassPathResource("files/AppendixIron2.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                //读取工作表1
                Sheet sheet2 = workbook.getSheetAt(0);
                // 给E燃烧赋值
                for (int i = 3; i <= 2 + combustionConsumptionList.size(); i++) {
                    CombustionConsumptionDO combustionConsumptionDO = combustionConsumptionList.get(i - 3);
                    // 填入数据
                    setCellValue(sheet2, i, 1, combustionConsumptionDO.getDisplayName());
                    setCellValue(sheet2, i, 2, combustionConsumptionDO.getNetConsumption());
                    setCellValue(sheet2, i, 3, combustionConsumptionDO.getLowCalorific());

                }
                //合并单元格
                mergeCellsAndSetValue(sheet2, 3, 2 + combustionConsumptionList.size(), 0, 0, "化石燃料燃烧*");
                //设置数据单位
                setCellValue(sheet2, 3 + combustionConsumptionList.size(), 2, "数据");
                setCellValue(sheet2, 3 + combustionConsumptionList.size(), 3, "单位");
                //给E过程材料赋值
                for (int i = 4 + combustionConsumptionList.size(); i <= 3 + combustionConsumptionList.size() + coursesConsumptionList.size(); i++) {
                    CoursesConsumptionDO coursesConsumptionDO = coursesConsumptionList.get(i - 4 + combustionConsumptionList.size());
                    //填入数据
                    setCellValue(sheet2, i, 1, coursesConsumptionDO.getDisplayName());
                    setCellValue(sheet2, i, 2, coursesConsumptionDO.getNetConsumption());
                    setCellValue(sheet2, i, 3, "t");
                }
                //合并单元格
                mergeCellsAndSetValue(sheet2, 3 + combustionConsumptionList.size(), 3 + combustionConsumptionList.size() + coursesConsumptionList.size(), 0, 0, "工业生产过程");
                //设置数据和单位
                setCellValue(sheet2, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, "数据");
                setCellValue(sheet2, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "单位");
                //给电力和热力赋值
                setCellValue(sheet2, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 1, "电力净购入量");
                setCellValue(sheet2, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, electricityCombustion.getNetCombustion());
                setCellValue(sheet2, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "MWh");
                setCellValue(sheet2, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 1, "热力净购入量");
                setCellValue(sheet2, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, heatConsumption.getNetConsumption());
                setCellValue(sheet2, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "GJ");
                //合并单元格
                mergeCellsAndSetValue(sheet2, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(),
                        6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 0, 0, "净购入电力、热力");
                //设置数据和单位
                setCellValue(sheet2, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, "数据");
                setCellValue(sheet2, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "单位");
                //给固碳赋值
                for (int i = 8 + combustionConsumptionList.size() + coursesConsumptionList.size(); i <= 7 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(); i++) {
                    CarbonSequestrationConsumptionDO carbonSequestrationConsumptionDO = carbonSequestrationConsumption.get(i - 8 + combustionConsumptionList.size() + coursesConsumptionList.size());
                    //填入数据
                    setCellValue(sheet2, i, 1, carbonSequestrationConsumptionDO.getDisplayName() + "产量");
                    setCellValue(sheet2, i, 2, carbonSequestrationConsumptionDO.getNetConsumption());
                    setCellValue(sheet2, i, 3, "t");
                }
                //合并单元格
                mergeCellsAndSetValue(sheet2, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(),
                        7 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(), 0, 0, "固碳");
                //表末尾
                setCellValue(sheet2, 8 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(), 0, "* 企业应自行添加未在表中列出但企业实际消耗的其他能源品种");
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
        try (InputStream inputStream = new ClassPathResource("files/AppendixIron3.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet sheet3 = workbook.getSheetAt(0);
                // 给E燃烧赋值
                for (int i = 3; i <= 2 + combustionConsumptionList.size(); i++) {
                   CombustionConsumptionDO combustionConsumptionDO =combustionConsumptionList.get(i - 3);
                    // 填入数据
                    setCellValue(sheet3, i, 1, combustionConsumptionDO.getDisplayName());
                    setCellValue(sheet3, i, 2, combustionConsumptionDO.getCarbonUnitCalorific());
                    setCellValue(sheet3, i, 3, combustionConsumptionDO.getFuelOxidationRate());
                }
                //合并单元格
                mergeCellsAndSetValue(sheet3, 3, 2 + combustionConsumptionList.size(), 0, 0, "化石燃料燃烧*");
                //设置数据单位
                setCellValue(sheet3, 3 + combustionConsumptionList.size(), 2, "数据");
                setCellValue(sheet3, 3 + combustionConsumptionList.size(), 3, "单位");
                //给E过程材料赋值
                for (int i = 4 + combustionConsumptionList.size(); i <= 3 + combustionConsumptionList.size() + coursesConsumptionList.size(); i++) {
                    CoursesConsumptionDO coursesConsumptionDO = coursesConsumptionList.get(i - 4 + combustionConsumptionList.size());
                    //填入数据
                    setCellValue(sheet3, i, 1, coursesConsumptionDO.getDisplayName());
                    setCellValue(sheet3, i, 2, coursesConsumptionDO.getFactor());
                    setCellValue(sheet3, i, 3, "tCO2/t");
                }
                //合并单元格
                mergeCellsAndSetValue(sheet3, 3 + combustionConsumptionList.size(), 3 + combustionConsumptionList.size() + coursesConsumptionList.size(), 0, 0, "工业生产过程");
                //设置数据和单位
                setCellValue(sheet3, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, "数据");
                setCellValue(sheet3, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "单位");
                //给电力和热力赋值
                setCellValue(sheet3, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 1, "电力");
                setCellValue(sheet3, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, electricityCombustion.getFactor());
                setCellValue(sheet3, 5 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "tCO2/MWh");
                setCellValue(sheet3, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 1, "热力");
                setCellValue(sheet3, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, heatConsumption.getFactor());
                setCellValue(sheet3, 6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "tCO2/ GJ");
                //合并单元格
                mergeCellsAndSetValue(sheet3, 4 + combustionConsumptionList.size() + coursesConsumptionList.size(),
                        6 + combustionConsumptionList.size() + coursesConsumptionList.size(), 0, 0, "净购入电力、热力");
                //设置数据和单位
                setCellValue(sheet3, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(), 2, "数据");
                setCellValue(sheet3, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(), 3, "单位");
                //给固碳赋值
                for (int i = 8 + combustionConsumptionList.size() + coursesConsumptionList.size(); i <= 7 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(); i++) {
                    CarbonSequestrationConsumptionDO carbonSequestrationConsumptionDO = carbonSequestrationConsumption.get(i - 8 + combustionConsumptionList.size() + coursesConsumptionList.size());
                    //填入数据
                    setCellValue(sheet3, i, 1, carbonSequestrationConsumptionDO.getDisplayName());
                    setCellValue(sheet3, i, 2, carbonSequestrationConsumptionDO.getFactor());
                    setCellValue(sheet3, i, 3, "tCO2/t");
                }
                //合并单元格
                mergeCellsAndSetValue(sheet3, 7 + combustionConsumptionList.size() + coursesConsumptionList.size(),
                        7 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(), 0, 0, "固碳");
                //表尾
                setCellValue(sheet3, 8 + combustionConsumptionList.size() + coursesConsumptionList.size() + carbonSequestrationConsumption.size(), 0, "* 企业应自行添加未在表中列出但企业实际消耗的其他能源品种");
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
    public ResponseEntity<BaseResponse> createCarbonReport1(long timestamp, @NotNull HttpServletRequest request, @NotNull CarbonConsumeVO carbonConsumeVO, @NotNull List<MaterialsDO.Materials> materials, @NotNull List<MaterialsDO.Desulfurization> desulfurization) {
        // 从数据库获取上一份报告的数据，准备进行比较
        CarbonReportDO getOrganizeUserLastCarbonReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 使用静态方法检查时间冲突
        if (checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO) == null) {
            return ResultUtil.error(timestamp, "您此次报告与之前报告冲突或时间范围不正确", ErrorCode.WRONG_DATE);
        }
        // 从VO获取数据向数据库插入此次报告的基本数据
        // 考虑外键约束相关的数据表插入数据顺序：fy_carbon_report、fy_carbon_accounting、fy_carbon_compensation_material
        CarbonTypeDO getCarbonType = carbonTypeDAO.getTypeByName("generateElectricity");
        CarbonReportDO carbonReportDO = new CarbonReportDO();
        carbonReportDO.setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request)).setReportTitle(carbonConsumeVO.getTitle()).setReportType(getCarbonType.getUuid()).setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO)).setReportStatus("draft").setReportSummary(carbonConsumeVO.getSummary());
        if (!(carbonReportDAO.insertReportMapper(carbonReportDO))) {
            return ResultUtil.error(timestamp, "新增碳核算报告数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算报告数据表
        CarbonReportDO getLastReport = carbonReportDAO.getLastReportByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳核算数据表中，插入数据
        // 生成准备存放的DO对象
        CarbonAccountingDO carbonAccountingDO = new CarbonAccountingDO();
        carbonAccountingDO.setOrganizeUuid(ProcessingUtil.getAuthorizeUserUuid(request)).setReportId(getLastReport.getId()).setEmissionType(getCarbonType.getUuid()).setAccountingPeriod(checkReportTimeHasDuplicate(getOrganizeUserLastCarbonReport, carbonConsumeVO)).setDataVerificationStatus("pending");
        if (!(carbonAccountingDAO.insertCarbonAccounting(carbonAccountingDO))) {
            return ResultUtil.error(timestamp, "新增碳核算数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        // 获取刚刚初始化的碳核算数据表
        CarbonAccountingDO getLastCarbonAccounting = carbonAccountingDAO.getLastCarbonAccountingByUuid(ProcessingUtil.getAuthorizeUserUuid(request));
        // 向碳排放配额原料表中，插入数据
        // 生成准备存放的DO对象
        CarbonCompensationMaterialDO carbonCompensationMaterialDO = new CarbonCompensationMaterialDO();
        ElectricDO electricDO = new ElectricDO();
        electricDO.setElectricBuy(carbonConsumeVO.getElectricBuy()).setElectricOutside(carbonConsumeVO.getElectricOutside()).setElectricCompany(carbonConsumeVO.getElectricCompany()).setElectricExport(carbonConsumeVO.getElectricExport());
        // 电力数据
        String electric1 = gson.toJson(electricDO);
        HashMap<String, Object> setMaterials = new HashMap<>();
        setMaterials.put("materials", materials);
        setMaterials.put("desulfurization", desulfurization);
        carbonCompensationMaterialDO.setAccountingId(getLastCarbonAccounting.getId()).setRawMaterial(gson.toJson(setMaterials)).setElectricMaterial(electric1);
        if (!(carbonCompensationMaterialDAO.insertCarbonCompensationMaterial(carbonCompensationMaterialDO))) {
            return ResultUtil.error(timestamp, "新增碳原料数据表记录失败", ErrorCode.SERVER_INTERNAL_ERROR);
        }
        /*
         * 1. 计算E燃烧
         * 2. 计算E脱硫
         * 3. 计算E电力
         */
        double eCombustion = eCombustion(materials, carbonItemTypeDAO);
        if (eCombustion < 0){
            return ResultUtil.error(timestamp, "化石燃烧相关参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eDesulfurization = eDesulfurization(desulfurization, processEmissionFactorDAO);
        if (eDesulfurization < 0){
            return ResultUtil.error(timestamp, "脱硫过程相关参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        double eElectric = electricity(carbonConsumeVO, otherEmissionFactorDAO);
        if (eElectric < 0){
            return ResultUtil.error(timestamp, "电力相关参数错误", ErrorCode.REQUEST_BODY_ERROR);
        }
        // 汇总碳排放
        double totalCombustion = eCombustion + eDesulfurization + eElectric;
        CarbonAccountingEmissionsVolumeDO carbonAccountingEmissionsVolumeDO = new CarbonAccountingEmissionsVolumeDO();
        CarbonAccountingEmissionsVolumeDO.Material material = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Material desulfuization = new CarbonAccountingEmissionsVolumeDO.Material();
        CarbonAccountingEmissionsVolumeDO.Electric electric = new CarbonAccountingEmissionsVolumeDO.Electric();
        material.setName("eCombustion").setCarbonEmissions(eCombustion);
        desulfuization.setName("eDesulfurization").setCarbonEmissions(eDesulfurization);
        electric.setName("eElectric").setElectricEmissions(eElectric);

        carbonAccountingEmissionsVolumeDO.setMaterials(material).setDesulfuizations(desulfuization).setElectric(electric);
        // 创建3个excel附表
        /*
        * 1. 准备DO数据————CarbonDioxideEmissions、EmissionActivityLevel、EmissionFactor
        * 2. 数据插入excel中
        * 3. 返回附表
        * */
        // 获取附表一所需要的数据
        String schedule1;
        ExcelCarbonDioxideEmissionsDO excelCarbonDioxideEmissionsDO = new ExcelCarbonDioxideEmissionsDO();
        excelCarbonDioxideEmissionsDO
            .setTotalEmissions(String.valueOf(totalCombustion))
            .setFuel(String.valueOf(eCombustion))
            .setElectricity(String.valueOf(eElectric))
            .setDesulfurizer(String.valueOf(eDesulfurization));
        // 写入数据到附表一
        try (InputStream inputStream = new ClassPathResource("files1/PowerGeneration1.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                //获取工作表1
                // 获取第一张工作表
                Sheet sheet = workbook.getSheetAt(0);
                // 为单元格赋值
                setCellValue(sheet, 1, 1, excelCarbonDioxideEmissionsDO.getTotalEmissions());
                setCellValue(sheet, 2, 1, excelCarbonDioxideEmissionsDO.getFuel());
                setCellValue(sheet, 3, 1, excelCarbonDioxideEmissionsDO.getDesulfurizer());
                setCellValue(sheet, 4, 1, excelCarbonDioxideEmissionsDO.getElectricity());
                /*
                *
                * 数据表第一行中还有一处年份未进行替换
                * */
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

        String schedule2;
        // 获取附表二所需要的数据，直接获取list向excel表格中写入
        List<List<String>> listFuel2 = fuelData(materials, carbonItemTypeDAO).get(0);
        List<List<String>> listDes2 = desulfurizationData(desulfurization, processEmissionFactorDAO).get(0);
        //创建附表名称
        try (InputStream inputStream = new ClassPathResource("files1/PowerGeneration2.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                //获取工作表1
                // 获取第一张工作表
                Sheet sheet = workbook.getSheetAt(0);
                // 添加化石燃料部分数据
                int rows = 2;
                int count = rows;
                for(List<String> dataList: listFuel2){
                    Row row = sheet.createRow(rows++);
                    for (int i = 0; i < dataList.size(); i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellValue(dataList.get(i));
                    }
                }
                // 获取插入化石燃料燃烧一共多少行
                count = rows - count -1;
                // 进行合并赋值
                mergeCellsAndSetValue(sheet, 2, 2 + count, 0, 0, "化石燃料燃烧");

                // 添加脱硫过程部分数据
                Row row = sheet.createRow(rows++);
                List<String> dataList = Arrays.asList("", "", "数据", "单位");
                for (int i = 0; i < dataList.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList.get(i));
                }
                int count1 = rows;
                for(List<String> dataList1: listDes2){
                    Row row1 = sheet.createRow(rows++);
                    for (int i = 0; i < dataList1.size(); i++) {
                        Cell cell = row1.createCell(i);
                        cell.setCellValue(dataList1.get(i));
                    }
                }
                // 获取脱硫过程中插入了多少行
                count1 = rows - count1 - 1;
                // 进行单元格合并赋值
                mergeCellsAndSetValue(sheet, 3 + count, 3 + count + count1, 0, 0, "脱硫过程");


                // 添加净购入电力相关数据
                List<String> dataList2 = Arrays.asList("", "", "数据", "单位");
                for (int i = 0; i < dataList2.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList2.get(i));
                }
                Double electricBuy = Double.parseDouble(carbonConsumeVO.getElectricBuy());
                Double electricExport = Double.parseDouble(carbonConsumeVO.getElectricExport());
                List<String> dataList3 = Arrays.asList("净购入电力", "电力净购入量", String.valueOf(electricBuy-electricExport), "MWh");
                for (int i = 0; i < dataList3.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList3.get(i));
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
                log.error("读取附表2错误", e);
                return ResultUtil.error(timestamp, "读取附表2错误", ErrorCode.SERVER_INTERNAL_ERROR);
            }
        } catch (IOException e) {
            log.error("读取模板附表2错误", e);
            return ResultUtil.error(timestamp, "读取模板附表2错误", ErrorCode.SERVER_INTERNAL_ERROR);
        }

        // 获取附表三所需要的数据，直接获取list向excel表格中写入
        String schedule3;
        List<List<String>> listFuel3 = fuelData(materials, carbonItemTypeDAO).get(1);
        List<List<String>> listDes3 = desulfurizationData(desulfurization, processEmissionFactorDAO).get(1);
        try (InputStream inputStream = new ClassPathResource("files1/PowerGeneration3.xlsx").getInputStream()) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                //获取工作表1
                // 获取第一张工作表
                Sheet sheet = workbook.getSheetAt(0);
                // 添加化石燃料部分数据
                int rows = 2;
                for(List<String> dataList: listFuel3){
                    Row row = sheet.createRow(rows++);
                    for (int i = 0; i < dataList.size(); i++) {
                        Cell cell = row.createCell(i);
                        cell.setCellValue(dataList.get(i));
                    }
                }
                // 添加脱硫过程部分数据
                Row row = sheet.createRow(rows++);
                List<String> dataList = Arrays.asList("", "", "数据", "单位");
                for (int i = 0; i < dataList.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList.get(i));
                }
                for(List<String> dataList1: listDes3){
                    Row row1 = sheet.createRow(rows++);
                    for (int i = 0; i < dataList1.size(); i++) {
                        Cell cell = row1.createCell(i);
                        cell.setCellValue(dataList1.get(i));
                    }
                }
                // 添加净购入电力相关数据
                List<String> dataList2 = Arrays.asList("", "", "数据", "单位");
                for (int i = 0; i < dataList2.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList2.get(i));
                }
                OtherEmissionFactorDO otherEmissionFactorDO = otherEmissionFactorDAO.getFactorByName(carbonConsumeVO.getElectricCompany());
                String electricFactor = String.valueOf(otherEmissionFactorDO.getFactor());
                List<String> dataList3 = Arrays.asList("净购入电力", "区域电网年平均供电排放因子", electricFactor, "tCO2/MWh");
                for (int i = 0; i < dataList3.size(); i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(dataList3.get(i));
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
        carbonAccountingEmissionsVolumeDO
                .setMaterials(material)
                .setDesulfuizations(desulfuization)
                .setElectric(electric);

        //整理3个文件的链接
        ArrayList<String> listOrReports = new ArrayList<>();
        listOrReports.add(schedule1 + ".xlsx");
        listOrReports.add(schedule2 + ".xlsx");
        listOrReports.add(schedule3 + "xlsx");
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
        carbonQuotaDO.setUuid(ProcessingUtil.createUuid()).setOrganizeUuid(organizeId).setQuotaYear(localYear).setTotalQuota(Double.parseDouble(carbonAddQuotaVO.getQuota())).setAllocatedQuota(Double.parseDouble(carbonAddQuotaVO.getQuota())).setUsedQuota(0).setAllocationDate(new Date(timestamp)).setComplianceStatus(!carbonAddQuotaVO.getStatus()).setAuditLog(gson.toJson(carbonAuditLogList));
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
                getCarbonQuota
                        .setComplianceStatus(!carbonAddQuotaVO.getStatus())
                        .setQuotaYear(localYear)
                        .setAllocatedQuota(Double.parseDouble(carbonAddQuotaVO.getQuota()))
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