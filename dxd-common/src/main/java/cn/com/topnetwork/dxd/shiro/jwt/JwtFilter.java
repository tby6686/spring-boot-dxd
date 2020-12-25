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

package cn.com.topnetwork.dxd.shiro.jwt;

import cn.com.topnetwork.dxd.api.ApiCode;
import cn.com.topnetwork.dxd.constant.properties.JwtProperties;
import cn.com.topnetwork.dxd.util.CacheUtil;
import cn.com.topnetwork.dxd.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shiro JWT授权过滤器
 *
 * @author tby
 * @date 2019-09-27
 * @since 1.3.0.RELEASE
 **/
@Slf4j
public class JwtFilter extends AuthenticatingFilter{
    @Override
    protected boolean preHandle(ServletRequest request,ServletResponse response) throws Exception {
        log.info("JwtAuthFilter.preHandle");
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        //对于OPTION请求做拦截，不做token校验
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())){
            return false;
        }
        return super.preHandle(request,response);
    }

    /**
     * 将JWT Token包装成AuthenticationToken
     *
     * @param servletRequest
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse){
        log.info("JwtAuthFilter.createToken");
        String tokenStr = JwtUtil.getTokenFromRequest(WebUtils.toHttp(servletRequest));
        if (StringUtils.isBlank(tokenStr)) {
            throw new AuthenticationException("token不能为空");
        }

        String username = JwtUtil.getUsername(tokenStr);
        //token必须通过用户登陆而产生
        boolean isContains = CacheUtil.judgeLoginSysUserContainsUsername(username);
        if(!isContains){
            throw new AuthenticationException("token 非法!");
        }
        return refreshToken(tokenStr,username,servletResponse);
    }

    /**
     * 刷新token
     * @param tokenStr
     * @param username
     * @param servletResponse
     * @return
     */
    private JwtToken refreshToken(String tokenStr,String username,ServletResponse servletResponse){
        JwtToken resultToken =  new JwtToken(tokenStr);
        //1.判断token是否已过期或即将过期
        boolean isRefresh = JwtUtil.isRefreshToken(tokenStr);
        if(isRefresh){
            // 刷新 token 需要进行同步，防止并发请求重复刷新
            synchronized (this){
                boolean isCurrTokenCacheExists = CacheUtil.judgeLoginSysUserTokenContainsKey(username,tokenStr,true);
                //2.传递过来的token与缓存token(当前在用)一致
                if(isCurrTokenCacheExists){
                    //(1)创建新token
                    String newTokenStr = JwtUtil.generateNewTokenFormOldToken(tokenStr);
                    resultToken = new JwtToken(newTokenStr);
                    //(2)更新缓存信息
                    //2.1 在用token缓存中>>将过期的token移除
                    CacheUtil.getLoginSysUserTokenByKey(username,tokenStr,true);
                    //2.2 在用token缓存中>>将新的token添加
                    CacheUtil.cacheLoginSysUserToken(username,newTokenStr,true);
                    //2.3 设置过期 token 的过渡时间，用于在并发请求时，放行后面的请求
                    CacheUtil.cacheLoginSysUserToken(username,tokenStr,false);
                    //(3)将新token放入response中
                    HttpServletResponse httpServletResponse = WebUtils.toHttp(servletResponse);
                    httpServletResponse.setHeader(JwtProperties.tokenName, newTokenStr);
                }
            }
        }
        return  resultToken;
    }

    /**
     * 判断是否允许访问
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){
        String url = WebUtils.toHttp(request).getRequestURI();
        log.debug("isAccessAllowed url:{}", url);
        if (this.isLoginRequest(request, response)) {
            return true;
        }
        boolean allowed = false;
        try {
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { //not found any token
            log.error("Token不能为空", e);
        } catch (Exception e) {
            log.error("访问错误", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    /**
     * 登陆成功处理
     * @param token
     * @param subject
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response){
        log.info("Validate token success, token:{}", token.toString());
        return true;
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        log.error("Validate token fail, token:{}", token.toString(), e);
        return false;
    }

    /**
     * 访问失败处理
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.setStatus(ApiCode.UNAUTHORIZED.getCode());
        this.fillCorsHeader(WebUtils.toHttp(request), httpServletResponse);
        return false;
    }

    // cors 跨域设置
    private void fillCorsHeader(HttpServletRequest toHttp, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", toHttp.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", toHttp.getHeader("Access-Control-Request-Headers"));
    }
}
