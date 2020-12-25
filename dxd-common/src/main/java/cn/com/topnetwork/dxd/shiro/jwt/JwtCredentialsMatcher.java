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

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;

import java.util.List;

/**
 * JWT证书匹配
 *
 * @author tby
 * @date 2019-09-30
 * @since 1.3.0.RELEASE
 **/
@Slf4j
public class JwtCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        String token = authenticationToken.getCredentials().toString();
        List<String> cacheTokenList = (List<String>) authenticationInfo.getCredentials();
        try {
            if(cacheTokenList!=null && cacheTokenList.size()>0){
                return cacheTokenList.contains(token);
            }
        } catch (Exception e) {
            log.error("JWT Token CredentialsMatch Exception:" + e.getMessage(), e);
        }
        return false;
    }

}
