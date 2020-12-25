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

package cn.com.topnetwork.dxd.constant.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shiro配置映射类
 *
 * @author tby
 * @date 2019-09-28
 **/
@Data
@Component
@ConfigurationProperties(prefix = "spring-boot-dxd.shiro")
public class ShiroProperties {

    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 路径权限配置
     */
    private String filterChainDefinitions;

    /**
     * 设置无需权限路径集合
     */
    private List<String[]> anon;

    /**
     * 权限配置集合
     * 属性为list对象集合>>
     * (1)内部类定义为static,才可读取到yml中的配置
     * (2)内部类定义为static,可不用手动new 集合对象
     * 属性为对象>>
     * (1)内部类不必须定义为static,也可读取到yml中的配置
     * (2)内部类无论是否为static,均需要手动new对象
     * 总结:
     * (1)@NestedConfigurationProperty修饰,进行new 操作
     * (2)类定义为static
     */
    @NestedConfigurationProperty
    private List<ShiroPermissionProperties> permissionList;

    @Data
    public static class ShiroPermissionProperties {

        /**
         * 路径
         */
        private String url;
        /**
         * 路径数组
         */
        private String[] urls;

        /**
         * 权限
         */
        private String permission;

    }

}
