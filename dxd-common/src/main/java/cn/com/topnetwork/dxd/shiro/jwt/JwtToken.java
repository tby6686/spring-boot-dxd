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

package cn.com.topnetwork.dxd.shiro.jwt;

import cn.com.topnetwork.dxd.util.IpUtil;
import lombok.Data;
import org.apache.shiro.authc.HostAuthenticationToken;

/**
 * Shiro JwtToken对象
 *
 * @author tianbaoyan
 * @date 2020-02-15
 * @since 1.3.0.RELEASE
 **/
@Data
public class JwtToken implements HostAuthenticationToken {

    private static final long serialVersionUID = -7375302043888882790L;

    private String token;
    private String host;

    public JwtToken(String token,String host) {
        this.token = token;
        this.host = host;
    }

    public JwtToken(String token) {
        this(token,IpUtil.getRequestIp());
    }

    /** 注意这里的重写方法，后续使用中，以此处返回值为准 */
    @Override
    public Object getPrincipal() {
        return this.token;
    }

    /** 注意这里的重写方法，后续使用中，以此处返回值为准 */
    @Override
    public Object getCredentials() {
        return this.token;
    }
}
