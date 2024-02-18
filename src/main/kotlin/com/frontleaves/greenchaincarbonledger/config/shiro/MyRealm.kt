package com.frontleaves.greenchaincarbonledger.config.shiro

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection

/**
 * MyRealm - 自定义 Realm
 *
 * @since v1.0.0-SNAPSHOT
 * @author xiao_lfeng
 */
class MyRealm : AuthorizingRealm() {
    override fun doGetAuthenticationInfo(token: AuthenticationToken?): AuthenticationInfo? {
        return null
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection?): AuthorizationInfo? {
        return null
    }
}