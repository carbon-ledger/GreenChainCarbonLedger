package com.frontleaves.greenchaincarbonledger.config.shiro

import com.frontleaves.greenchaincarbonledger.utils.security.JwtUtil
import com.frontleaves.greenchaincarbonledger.config.filter.CorsFilter
import com.frontleaves.greenchaincarbonledger.config.filter.JwtFilter
import com.frontleaves.greenchaincarbonledger.config.filter.TimestampFilter
import jakarta.servlet.Filter
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Shiro配置
 *
 * Shiro配置类, 用于配置Shiro的拦截器, 过滤器等
 *
 * @author xiao_lfeng
 * @since v1.0.0-SNAPSHOT
 */
@Configuration
open class ShiroConfig(
    private val jwtUtil: JwtUtil
) {

    @Bean
    open fun filterFactoryBean(@Qualifier("manager") manager: DefaultWebSecurityManager?): ShiroFilterFactoryBean {
        val shiroFactory = ShiroFilterFactoryBean()
        shiroFactory.securityManager = manager
        val map = HashMap<String, String>()
            .also {
                it["/**"] = "cors"
                it["/auth/change"] = "cors,time,jwt"
                it["/auth/delete"] = "cors,time,jwt"
                it["/auth/login"] = "cors,time"
                it["/auth/organize/register"] = "cors,time"
                it["/user/**"] = "cors,time,jwt"
                it["/role/**"] = "cors,time,jwt"
                it["/admin/**"] = "cors,time,jwt"
                it["/mail/**"] = "cors,time"
            }
        shiroFactory.filterChainDefinitionMap = map

        // 自定义拦截器
        val customFilter = HashMap<String, Filter>()
            .also { it["jwt"] = JwtFilter(jwtUtil) }
            .also { it["cors"] = CorsFilter() }
            .also { it["time"] = TimestampFilter() }
        shiroFactory.filters = customFilter

        //设置登录页面
        shiroFactory.loginUrl = "/public/login"
        //未授权页面
        shiroFactory.unauthorizedUrl = "/public/unauthorized"
        return shiroFactory
    }


    @Bean
    open fun manager(@Qualifier("myRealm") myRealm: MyRealm?): DefaultWebSecurityManager {
        val manager = DefaultWebSecurityManager()
            .also { it.setRealm(myRealm) }
        return manager
    }

    @Bean
    open fun myRealm(): MyRealm {
        return MyRealm()
    }
}