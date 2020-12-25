/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package cn.com.topnetwork.dxd.config;


import cn.com.topnetwork.dxd.base.exception.BaseException;
import cn.com.topnetwork.dxd.constant.properties.ShiroProperties;
import cn.com.topnetwork.dxd.shiro.jwt.JwtCredentialsMatcher;
import cn.com.topnetwork.dxd.shiro.jwt.JwtFilter;
import cn.com.topnetwork.dxd.shiro.realm.DbRealm;
import cn.com.topnetwork.dxd.shiro.realm.JwtRealm;
import cn.com.topnetwork.dxd.system.service.ISysUserService;
import cn.com.topnetwork.dxd.util.IniUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 注意：ShiroFilterFactoryBean属于一个 BeanPostProcessor,以及lifecycleBeanPostProcessor, 它的初始化时机比普通 Bean,从而导致整个依赖链被提前初始化
 * 
 * Shiro配置>>https://shiro.apache.org/spring.html https://shiro.apache.org/spring-boot.html
 *
 * @author tianbaoyan
 * @date 2020-02-16
 * @since 1.3.0.RELEASE
 **/
@Slf4j
@Configuration
@ConditionalOnProperty(value = {"spring-boot-dxd.shiro.enable"}, matchIfMissing = true)
public class ShiroConfig {

    /**
     * Shiro过滤器名称
     */
    private static final String SHIRO_FILTER_NAME = "shiroFilter";

    /**
     * JWT过滤器名称
     */
    private static final String JWT_FILTER_NAME = "jwtFilter";

    /**
     * anon
     */
    private static final String ANON = "anon";

    /**
     * @description 注册shiro的Filter拦截请求
     * @return
     * @throws Exception
     */
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() throws Exception {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<Filter>();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName(SHIRO_FILTER_NAME);
        filterRegistrationBean.setFilter(proxy);
        // bean注入开启异步方式
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setEnabled(true);
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return filterRegistrationBean;
    }

    /**
     * ShiroFilterFactoryBean配置
     *
     * @param securityManager
     * @param shiroProperties
     * @return
     */
    @Bean(SHIRO_FILTER_NAME)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, ShiroProperties shiroProperties) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必需属性，指定一个SecurityManager的实例，
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 设置UnauthorizedUrl
        //shiroFilterFactoryBean.setUnauthorizedUrl("");
        //shiroFilterFactoryBean.setLoginUrl(Constants.LOGIN_URL);

        // 添加Filters
        Map<String, Filter> filterMap = getFilterMap();
        shiroFilterFactoryBean.setFilters(filterMap);
        // 添加URL路径权限配置
        Map<String, String> filterChainMap = getFilterChainDefinitionMap(shiroProperties);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 获取filter map
     * 
     * @return
     */
    private Map<String, Filter> getFilterMap() {
        Map<String, Filter> filterMap = new LinkedHashMap<String, Filter>();
        filterMap.put(JWT_FILTER_NAME, new JwtFilter());
        return filterMap;
    }

    /**
     * 
     * @description Shiro路径权限配置 <br>
     *              <b>注意</b>：权限配置url范围 从小到大
     * @param shiroProperties
     * @return
     */
    private Map<String, String> getFilterChainDefinitionMap(ShiroProperties shiroProperties) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

        // 获取排除的路径
        List<String[]> anonList = shiroProperties.getAnon();
        log.debug("anonList:{}", JSON.toJSONString(anonList));
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(anonList)) {
            anonList.forEach(anonArray -> {
                if (ArrayUtils.isNotEmpty(anonArray)) {
                    for (String anonPath : anonArray) {
                        filterChainDefinitionMap.put(anonPath, ANON);
                    }
                }
            });
        }

        // 获取ini格式配置
        String definitions = shiroProperties.getFilterChainDefinitions();
        if (StringUtils.isNotBlank(definitions)) {
            Map<String, String> section = IniUtil.parseIni(definitions);
            log.debug("definitions:{}", JSON.toJSONString(section));
            for (Map.Entry<String, String> entry : section.entrySet()) {
                filterChainDefinitionMap.put(entry.getKey(), entry.getValue());
            }
        }

        // 获取自定义权限路径配置集合
        List<ShiroProperties.ShiroPermissionProperties> permissionConfigs = shiroProperties.getPermissionList();
        log.debug("permissionConfigs:{}", JSON.toJSONString(permissionConfigs));
        if (CollectionUtils.isNotEmpty(permissionConfigs)) {
            for (ShiroProperties.ShiroPermissionProperties permissionConfig : permissionConfigs) {
                String url = permissionConfig.getUrl();
                String[] urls = permissionConfig.getUrls();
                String permission = permissionConfig.getPermission();
                if (StringUtils.isBlank(url) && ArrayUtils.isEmpty(urls)) {
                    throw new BaseException("shiro permission config url路径不能为空");
                }
                if (StringUtils.isBlank(permission)) {
                    throw new BaseException("shiro permission config permission不能为空");
                }

                if (StringUtils.isNotBlank(url)) {
                    filterChainDefinitionMap.put(url, permission);
                }
                if (ArrayUtils.isNotEmpty(urls)) {
                    for (String string : urls) {
                        filterChainDefinitionMap.put(string, permission);
                    }
                }
            }
        }

        // 如果启用shiro，则设置最后一个设置为JWTFilter，否则全部路径放行
        if (shiroProperties.isEnable()) {
            filterChainDefinitionMap.put("/**", JWT_FILTER_NAME);
        } else {
            filterChainDefinitionMap.put("/**", "anon");
        }

        log.debug("filterChainMap:{}", JSON.toJSONString(filterChainDefinitionMap));

        return filterChainDefinitionMap;
    }

    // --------------------------数据源realm配置-------------------//

    /**
     * @description DB认证的realm
     * @param sysUserService
     * @return
     */
    @Bean("dbRealm")
    public Realm dbRealm(ISysUserService sysUserService) {
        DbRealm dbRealm = new DbRealm(sysUserService);
        dbRealm.setCachingEnabled(false);
        return dbRealm;
    }

    /**
     * @description JWT认证的realm
     * @return
     */
    @Bean("jwtRealm")
    public Realm jwtRealm() {
        JwtRealm jwtRealm = new JwtRealm();
        jwtRealm.setCachingEnabled(false);
        jwtRealm.setCredentialsMatcher(credentialsMatcher());
        return jwtRealm;
    }

    /**
     * @description 证书匹配器
     * @return
     */
   @Bean
   public CredentialsMatcher credentialsMatcher() {
        return new JwtCredentialsMatcher();
    }


    // --------------------------认证器初始化-------------------//
    /**
     * @description 初始化authenticator
     * @param sysUserService
     * @return
     */
    @Bean
    public Authenticator authenticator(ISysUserService sysUserService) {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setRealms(Arrays.asList(dbRealm(sysUserService), jwtRealm()));
        authenticator.setAuthenticationStrategy(new FirstSuccessfulStrategy());
        return authenticator;
    }

    // --------------------------安全器管理配置-------------------//

    /**
     * 安全管理器配置
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager(ISysUserService sysUserService) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealms(Arrays.asList(dbRealm(sysUserService), jwtRealm()));
        securityManager.setAuthenticator(authenticator(sysUserService));
        securityManager.setSubjectDAO(subjectDAO());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * @description 禁用session
     * @return
     */
    @Bean
    public SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultWebSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(true);
        return sessionStorageEvaluator;
    }

    @Bean
    public DefaultSubjectDAO subjectDAO() {
        DefaultSubjectDAO defaultSubjectDAO = new DefaultSubjectDAO();
        defaultSubjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        return defaultSubjectDAO;
    }


    /**
     * Enabling Shiro Annotations
     * 
     * @return
     */
   @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        //该行会导致doGetAuthorizationInfo()方法执行两次
        //defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

}
