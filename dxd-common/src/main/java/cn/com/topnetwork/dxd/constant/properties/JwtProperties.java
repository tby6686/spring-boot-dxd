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

import cn.com.topnetwork.dxd.util.ReflectionUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * JWT属性配置
 *
 * @author tby
 * @date 2019-05-22
 **/
@Data
@Component
public class JwtProperties {

    @Autowired
    private Environment env;

    private static final String PREFIX = "spring-boot-dxd.jwt.";

    /**
     * token名称,默认名称为：token
     */
    public static String tokenName;

    /**
     * 密钥
     */
    public static String secret;

    /**
     * 主题(用户)
     */
    public static String subject;

    /**
     * 签发人
     */
    public static String issuer;

    /**
     * 签发的目标
     */
    public static String audience;

    /**
     * token过期时间,单位：秒 默认30分钟（1800秒）
     */
    public static Long expireSecond;

    /**
     * 缓存中token失效时间，默认 60分钟（3600秒）
     */
    public static Long expireCacheSecond;

    /**
     * 是否刷新token，默认为true
     */
    public static boolean refreshToken = true;

    /**
     * 刷新token倒计时，单位：分钟，默认5分钟.
     */
    public static Integer refreshTokenCountdown;

    /**
     * 过渡时间: 防止并发请求下，token刷新后旧token失效问题，单位：秒 默认2分钟
     */
    public static Long transitionSecond;

    @PostConstruct
    public void readConfig() {
        ReflectionUtil.staticFieldSetWithEnv(env, PREFIX, JwtProperties.class);
    }

}
