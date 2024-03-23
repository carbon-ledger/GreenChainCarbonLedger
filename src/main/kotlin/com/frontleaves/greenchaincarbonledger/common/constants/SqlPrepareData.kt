package com.frontleaves.greenchaincarbonledger.common.constants

/**
 * SQL 准备数据
 *
 * SQL 准备数据, 用于系统启动时进行数据准备
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
object SqlPrepareData {

    /**
     * SQL 权限列表
     *
     * 获取本程序的所有权限组的内容信息
     */
    val PERMISSION_LIST = ArrayList<ArrayList<String>>().apply {
        add(ArrayList(listOf("auth:userChangePassword", "用户修改密码")))
        add(ArrayList(listOf("auth:userDelete", "用户删除")))
        add(ArrayList(listOf("auth:userLogout", "用户登出")))
        add(ArrayList(listOf("role:addRole", "添加角色")))
        add(ArrayList(listOf("role:getCurrentRole", "获取当前角色")))
        add(ArrayList(listOf("role:getRoleList", "获取角色列表")))
        add(ArrayList(listOf("role:editRole", "编辑角色")))
        add(ArrayList(listOf("role:deleteRole", "删除角色")))
        add(ArrayList(listOf("user:getUserList", "获取用户列表")))
        add(ArrayList(listOf("user:editUserInformation", "编辑用户信息")))
        add(ArrayList(listOf("user:putUserForceEdit", "强制编辑用户信息")))
        add(ArrayList(listOf("user:getUserCurrent", "获取当前用户")))
        add(ArrayList(listOf("user:banUser", "封禁用户")))
        add(ArrayList(listOf("permission:getPermissionList", "获取权限列表")))
        add(ArrayList(listOf("admin:resetUserPassword", "重置用户密码")))
        add(ArrayList(listOf("super:closeServer", "关闭服务器")))
        add(ArrayList(listOf("super:resetSql", "重置数据库")))
        add(ArrayList(listOf("super:resetUploadFolder", "重置上传文件夹")))
        add(ArrayList(listOf("carbon:getOwnCarbonQuota", "获取自己的碳配额")))
        add(ArrayList(listOf("carbon:getCarbonReport", "获取碳排放报告")))
        add(ArrayList(listOf("review:addOrganize", "添加组织账户审核信息")))
        add(ArrayList(listOf("review:addAdmin", "添加监管账户审核信息")))
        add(ArrayList(listOf("review:checkOrganize", "审核组织账户")))
        add(ArrayList(listOf("review:checkAdmin", "审核监管账户")))
        add(ArrayList(listOf("review:reSendOrganize", "重新发送组织账户审核信息")))
        add(ArrayList(listOf("review:reSendAdmin", "重新发送监管账户审核信息")))
        add(ArrayList(listOf("review:getList", "获取审核列表")))
        add(ArrayList(listOf("review:getReview", "获取审核信息")))
        add(ArrayList(listOf("review:getReport", "获取审核报告")))
    }

    /**
     * SQL 角色列表
     *
     * CONSOLE 控制台角色
     */
    val SQL_ROLE_CONSOLE_PERMISSION_LIST = ArrayList<String>().apply {
        PERMISSION_LIST.forEach {
            add(it[0])
        }
    }

    /**
     * SQL 角色列表
     *
     * DEFAULT 默认角色
     */
    val SQL_ROLE_DEFAULT_PERMISSION_LIST = ArrayList<String>().apply {
        add("auth:userChangePassword")
        add("auth:userDelete")
        add("auth:userLogout")
        add("role:getCurrentRole")
        add("user:editUserInformation")
        add("user:getUserCurrent")
    }

    /**
     * SQL 角色列表
     *
     * ADMIN 管理员角色
     */
    val SQL_ROLE_ADMIN_PERMISSION_LIST = ArrayList<String>().apply {
        SQL_ROLE_DEFAULT_PERMISSION_LIST.forEach {
            add(it)
        }
    }


    /**
     * SQL 角色列表
     *
     * ORGANIZE 组织角色
     */
    val SQL_ROLE_ORGANIZE_PERMISSION_LIST = ArrayList<String>().apply {
        SQL_ROLE_DEFAULT_PERMISSION_LIST.forEach {
            add(it)
        }
    }

    val SQL_CARBON_ITEM_LIST = ArrayList<HashMap<String, String>>().also {
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "anthracite")
            put("displayName", "无烟煤")
            put("lowCalorific", "20.304")
            put("carbonUnitCalorific", "27.49")
            put("fuelOxidationRate", "0.94")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "bituminousCoal")
            put("displayName", "烟煤")
            put("lowCalorific", "19.570")
            put("carbonUnitCalorific", "26.18")
            put("fuelOxidationRate", "0.93")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "lignite")
            put("displayName", "褐煤")
            put("lowCalorific", "14.080")
            put("carbonUnitCalorific", "28.00")
            put("fuelOxidationRate", "0.96")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "cleanCoal")
            put("displayName", "洗精煤")
            put("lowCalorific", "26.344")
            put("carbonUnitCalorific", "25.40")
            put("fuelOxidationRate", "0.90")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "otherCoalWashing")
            put("displayName", "其他洗煤")
            put("lowCalorific", "8.363")
            put("carbonUnitCalorific", "25.40")
            put("fuelOxidationRate", "0.90")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "otherCoalProducts")
            put("displayName", "其他煤制品")
            put("lowCalorific", "17.460")
            put("carbonUnitCalorific", "33.60")
            put("fuelOxidationRate", "0.90")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "固体燃料")
            put("name", "coke")
            put("displayName", "焦炭")
            put("lowCalorific", "28.447")
            put("carbonUnitCalorific", "29.50")
            put("fuelOxidationRate", "0.93")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "crude")
            put("displayName", "原油")
            put("lowCalorific", "41.816")
            put("carbonUnitCalorific", "20.10")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "fuelOil")
            put("displayName", "燃料油")
            put("lowCalorific", "41.816")
            put("carbonUnitCalorific", "21.10")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "gasoline")
            put("displayName", "汽油")
            put("lowCalorific", "43.070")
            put("carbonUnitCalorific", "18.90")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "diesel")
            put("displayName", "柴油")
            put("lowCalorific", "42.652")
            put("carbonUnitCalorific", "20.20")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "generalKerosene")
            put("displayName", "一般煤油")
            put("lowCalorific", "44.750")
            put("carbonUnitCalorific", "19.60")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "liquefiedNaturalGas")
            put("displayName", "液化天然气")
            put("lowCalorific", "41.868")
            put("carbonUnitCalorific", "17.20")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "liquefiedPetroleumGas")
            put("displayName", "液化石油气")
            put("lowCalorific", "50.179")
            put("carbonUnitCalorific", "17.20")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "tar")
            put("displayName", "焦油")
            put("lowCalorific", "33.453")
            put("carbonUnitCalorific", "22.00")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "液体燃料")
            put("name", "crudeBenzene")
            put("displayName", "粗苯")
            put("lowCalorific", "41.816")
            put("carbonUnitCalorific", "22.70")
            put("fuelOxidationRate", "0.98")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "cokeOvenGas")
            put("displayName", "焦炉煤气")
            put("lowCalorific", "173.540")
            put("carbonUnitCalorific", "12.10")
            put("fuelOxidationRate", "0.99")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "blastFurnaceGas")
            put("displayName", "高炉煤气")
            put("lowCalorific", "33.000")
            put("carbonUnitCalorific", "70.80")
            put("fuelOxidationRate", "0.99")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "converterGas")
            put("displayName", "转炉煤气")
            put("lowCalorific", "84.000")
            put("carbonUnitCalorific", "49.60")
            put("fuelOxidationRate", "0.99")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "otherGas")
            put("displayName", "其他煤气")
            put("lowCalorific", "52.270")
            put("carbonUnitCalorific", "12.20")
            put("fuelOxidationRate", "0.99")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "naturalGas")
            put("displayName", "天然气")
            put("lowCalorific", "389.310")
            put("carbonUnitCalorific", "15.30")
            put("fuelOxidationRate", "0.99")
        })
        it.add(HashMap<String, String>().apply {
            put("mode", "化石燃料")
            put("type", "气体燃料")
            put("name", "refineryDryGas")
            put("displayName", "炼厂干气")
            put("lowCalorific", "45.998")
            put("carbonUnitCalorific", "18.20")
            put("fuelOxidationRate", "0.99")
        })
    }

    val SQL_PROCESS_EMISSION_FACTOR = ArrayList<HashMap<String,String>>().also {
        it.add(HashMap<String, String>().apply {
            put("name", "limestone")
            put("displayName", "石灰石")
            put("factor", "0.440")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "dolomite")
            put("displayName", "白云石")
            put("factor", "0.471")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "electrode")
            put("displayName", "电极")
            put("factor", "3.663")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "pig_iron")
            put("displayName", "生铁")
            put("factor", "0.172")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "direct_reduced_iron")
            put("displayName", "直接还原铁")
            put("factor", "0.073")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "ferronickelAlloy")
            put("displayName", "镍铁合金")
            put("factor", "0.037")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "ferrochromeAlloy")
            put("displayName", "铬铁合金")
            put("factor", "0.275")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "molybdenumIronAlloy")
            put("displayName", "钼铁合金")
            put("factor", "0.018")
        })
    }

    val SQL_OTHER_EMISSION_FACTOR = ArrayList<HashMap<String,String>>().also {
        it.add(HashMap<String, String>().apply {
            put("name", "electricity")
            put("displayName", "电力")
            put("factor", "use the latest national value") // Replace with the actual value once known.
            put("unit", "tCO2/MWh")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "thermalPower")
            put("displayName", "热力")
            put("factor", "0.11")
            put("unit", "tCO2/GJ")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "crudeSteel")
            put("displayName", "粗钢")
            put("factor", "0.0154")
            put("unit", "tCO2/t")
        })
        it.add(HashMap<String, String>().apply {
            put("name", "methanol")
            put("displayName", "甲醇")
            put("factor", "1.375")
            put("unit", "tCO2/t")
        })
    }
}