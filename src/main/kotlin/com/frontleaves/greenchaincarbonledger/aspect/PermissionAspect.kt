package com.frontleaves.greenchaincarbonledger.aspect

import com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission
import com.frontleaves.greenchaincarbonledger.annotations.KotlinSlf4j.Companion.log
import com.frontleaves.greenchaincarbonledger.dao.RoleDAO
import com.frontleaves.greenchaincarbonledger.dao.UserDAO
import com.frontleaves.greenchaincarbonledger.exceptions.NotEnoughPermissionException
import com.frontleaves.greenchaincarbonledger.exceptions.RoleNotEnoughPermissionException
import com.frontleaves.greenchaincarbonledger.exceptions.RoleNotFoundException
import com.frontleaves.greenchaincarbonledger.utils.ProcessingUtil
import com.google.gson.Gson
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 权限检查切面
 *
 * @since v1.0.0-SNAPSHOT
 * @version v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
@Aspect
@Component
class PermissionAspect(
    private val roleDAO: RoleDAO,
    private val userDAO: UserDAO,
    private val gson: Gson
) {

    /**
     * 检查用户权限
     *
     * 从请求头中获取用户UUID，从数据库中获取用户角色，从角色中获取权限列表，检查用户是否有权限访问该资源
     *
     * @since v1.0.0-SNAPSHOT
     * @version v1.0.0-SNAPSHOT
     * @author xiao_lfeng
     * @param pjp ProceedingJoinPoint
     * @throws NotEnoughPermissionException 权限不足异常
     * @throws RoleNotEnoughPermissionException 角色权限不足异常
     * @throws RoleNotFoundException 角色未找到异常
     */
    @Around("@annotation(com.frontleaves.greenchaincarbonledger.annotations.CheckAccountPermission)")
    fun checkPermission(pjp: ProceedingJoinPoint): Any? {
        log.info("[AOP] 检查用户权限 CheckAccountPermission")
        // 从ServletRequest中获取用户信息
        val servletRequestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes

        // 获取用户
        val getUserUuid = ProcessingUtil.getAuthorizeUserUuid(servletRequestAttributes.request)
        log.debug("\t> 登陆用户UUID: {}", getUserUuid)

        // 获取方法签名
        val signature = pjp.signature as MethodSignature
        val checkAccountPermission = signature.method.getAnnotation(CheckAccountPermission::class.java)
        val getPermission = checkAccountPermission.value

        // 获取用户所属角色
        val getUserDO = userDAO.getUserByUuid(getUserUuid)
        if (getUserDO != null) {
            val getRoleWithUser = roleDAO.getRoleByUuid(getUserDO.role)
            log.debug("\t> 需求权限: {}", getPermission.joinToString(","))

            // 从 Role 获取权限列表
            val getRolePermissions = gson.fromJson(getRoleWithUser.permission, Array<String>::class.java)
            val getUserPermissions = gson.fromJson(getUserDO.permission, Array<String>::class.java)
            var hasPermission = false

            // 权限匹配
            getRolePermissions.forEach {
                if (getRolePermissions.toList().contains(it)) {
                    log.debug("\t> 权限已匹配")
                    hasPermission = true
                    return@forEach
                }
            }
            if (!hasPermission && getUserPermissions != null) {
                getUserPermissions.forEach {
                    if (getUserPermissions.toList().contains(it)) {
                        log.debug("\t> 权限已匹配")
                        hasPermission = true
                        return@forEach
                    }
                }
            }

            // 匹配校验
            if (hasPermission) {
                return pjp.proceed()
            } else {
                throw NotEnoughPermissionException("您没有足够的权限访问该资源", getPermission)
            }
        } else {
            throw NotEnoughPermissionException("您没有足够的权限访问该资源", getPermission)
        }
    }
}