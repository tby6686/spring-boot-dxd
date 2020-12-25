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

package cn.com.topnetwork.dxd.config;

import cn.com.topnetwork.dxd.constant.properties.SpringBootFilterProperties;
import cn.com.topnetwork.dxd.constant.properties.SpringBootInterceptorProperties;
import cn.com.topnetwork.dxd.constant.properties.SpringBootProperties;
import cn.com.topnetwork.dxd.framework.filter.RequestDetailFilter;
import cn.com.topnetwork.dxd.framework.interceptor.PermissionInterceptor;
import cn.com.topnetwork.dxd.framework.interceptor.ResourceInterceptor;
import cn.com.topnetwork.dxd.framework.xss.XssFilter;
import cn.com.topnetwork.dxd.util.IniUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

/**
 * WebMvc配置
 *
 * @author tby
 * @date 2018-11-08
 */
@Slf4j
@Configuration
public class SpringBootWebMvcConfig implements WebMvcConfigurer {

    /**
     * spring-boot-dxd配置属性
     */
    @Autowired
    private SpringBootProperties springBootProperties;

    /**
     * Filter配置
     */
    @Autowired
    private SpringBootFilterProperties filterConfig;

    /**
     * 拦截器配置
     */
    @Autowired
    private SpringBootInterceptorProperties interceptorConfig;

    /**
     * RequestDetailFilter配置
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean requestDetailFilter() {
        SpringBootFilterProperties.FilterConfig requestFilterConfig = filterConfig.getRequest();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new RequestDetailFilter());
        filterRegistrationBean.setEnabled(requestFilterConfig.isEnable());
        filterRegistrationBean.addUrlPatterns(requestFilterConfig.getUrlPatterns());
        filterRegistrationBean.setOrder(requestFilterConfig.getOrder());
        filterRegistrationBean.setAsyncSupported(requestFilterConfig.isAsync());
        return filterRegistrationBean;
    }

    /**
     * XssFilter配置
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean xssFilter() {
        SpringBootFilterProperties.FilterConfig xssFilterConfig = filterConfig.getXss();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new XssFilter());
        filterRegistrationBean.setEnabled(xssFilterConfig.isEnable());
        filterRegistrationBean.addUrlPatterns(xssFilterConfig.getUrlPatterns());
        filterRegistrationBean.setOrder(xssFilterConfig.getOrder());
        filterRegistrationBean.setAsyncSupported(xssFilterConfig.isAsync());
        return filterRegistrationBean;
    }


    /**
     * 自定义权限拦截器
     *
     * @return
     */
    @Bean
    public PermissionInterceptor permissionInterceptor() {
        return new PermissionInterceptor();
    }

    /**
     * 资源拦截器
     *
     * @return
     */
    @Bean
    public ResourceInterceptor resourceInterceptor() {
        return new ResourceInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 资源拦截器注册
        if (interceptorConfig.getResource().isEnable()) {
            registry.addInterceptor(resourceInterceptor())
                    .addPathPatterns(interceptorConfig.getResource().getIncludePaths());
        }

        // 自定义权限拦截器注册
        if (interceptorConfig.getPermission().isEnable()) {
            registry.addInterceptor(permissionInterceptor())
                    .addPathPatterns(interceptorConfig.getPermission().getIncludePaths())
                    .excludePathPatterns(interceptorConfig.getPermission().getExcludePaths());
        }

    }

    /**
     * 设置静态资源访问路径
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 设置项目静态资源访问
        String resourceHandlers = springBootProperties.getResourceHandlers();
        if (StringUtils.isNotBlank(resourceHandlers)) {
            Map<String, String> map = IniUtil.parseIni(resourceHandlers);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String pathPatterns = entry.getKey();
                String resourceLocations = entry.getValue();
                registry.addResourceHandler(pathPatterns)
                        .addResourceLocations(resourceLocations);
            }
        }
    }

}
