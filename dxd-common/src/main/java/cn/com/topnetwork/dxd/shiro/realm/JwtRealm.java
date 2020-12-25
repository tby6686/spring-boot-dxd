/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.com.topnetwork.dxd.shiro.realm;

import cn.com.topnetwork.dxd.shiro.jwt.JwtToken;
import cn.com.topnetwork.dxd.system.entity.LoginSysUserVo;
import cn.com.topnetwork.dxd.util.CacheUtil;
import cn.com.topnetwork.dxd.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

/**
 * Shiro 授权认证
 *
 * @author tby
 * @date 2019-09-27
 * @since 1.3.0.RELEASE
 **/
@Slf4j
public class JwtRealm extends AuthorizingRealm {
    @Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof JwtToken;
    }

    /**
     * 授权认证,设置角色/权限信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log.debug("doGetAuthorizationInfo principalCollection...");
        //获取登陆用户缓存信息
        LoginSysUserVo loginSysUserVo = (LoginSysUserVo) principalCollection.getPrimaryPrincipal();
        System.out.println("loginUserVo:" + loginSysUserVo);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 设置角色
        authorizationInfo.setRoles(SetUtils.hashSet(loginSysUserVo.getRoleCode()));
        // 设置权限
        authorizationInfo.setStringPermissions(loginSysUserVo.getPermissionCodes());
        return authorizationInfo;
    }

    /**
     * 登录认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.debug("doGetAuthenticationInfo authenticationToken...");
        // 校验token
        JwtToken jwtToken = (JwtToken) authenticationToken;
        if (jwtToken == null) {
            throw new AuthenticationException("jwtToken不能为空");
        }
        String username = JwtUtil.getUsername(jwtToken.getToken());
        if (StringUtils.isEmpty(username)) {
            throw new AuthenticationException("token 无效！用户为空！");
        }

        //获取登陆用户缓存对象
        LoginSysUserVo loginSysUserVo = CacheUtil.getLoginSysUserByUsername(username);
        if (loginSysUserVo == null) {
            throw new AuthenticationException("根据token获取缓存登陆用户信息失败！");
        }

        //获取缓存中的用户token进行认证
        //包括:该用户当前在使用的token列表 + 该用户过渡期间的旧token列表
        List<String> userCacheAllList = CacheUtil.getLoginSysUserTokenAllByUsername(loginSysUserVo.getUsername());

        return new SimpleAuthenticationInfo(loginSysUserVo,
                userCacheAllList,
                getName()
        );

    }

}