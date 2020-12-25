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

package cn.com.topnetwork.dxd.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 公共常量  《框架属性》
 * @author tby
 * @date 2019-05-23
 **/
@Component
public class ConstantFrame {

    public static String cacheClassPath;

    @Value("${spring.cache.class-path:cn.com.topnetwork.dxd.cache.EhcacheCache}")
    public void setCacheClassName(String cacheClassPath) {
        ConstantFrame.cacheClassPath = cacheClassPath;
    }
}
