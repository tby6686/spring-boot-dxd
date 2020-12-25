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

package cn.com.topnetwork.dxd.util;

import cn.com.topnetwork.dxd.constant.properties.JwtProperties;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * JWT工具类
 * https://github.com/auth0/java-jwt
 *
 * @author tby
 * @date 2019-09-30
 * @since 1.3.0.RELEASE
 **/
@Slf4j
public class JwtUtil {

    /**
     * 生成JWT Token
     * @param userId       用户Id
     * @param username       用户名
     * @return token
     */
    public static String generateToken(Long userId,String username) {
        try {
            if (StringUtils.isBlank(username)||userId==null) {
                log.error("username或userId不能为空");
                return null;
            }
            log.debug("username:{}", username);

            // 盐值使用默认值
            String salt = JwtProperties.secret;

            // 过期时间，单位：秒
            Long expireSecond = JwtProperties.expireSecond;

            Date nowDate = new Date();
            Date expireDate = DateUtils.addSeconds(nowDate, expireSecond.intValue());

            // 生成token
            Algorithm algorithm = Algorithm.HMAC256(salt);
            String token = JWT.create()
                    .withClaim("userId", userId)
                    // jwt唯一id
                    .withJWTId(UUIDUtil.getUuid())
                    // 主题
                    .withSubject(username)
                    // 签发人
                    .withIssuer(JwtProperties.issuer)
                    // 签发的目标
                    .withAudience(JwtProperties.audience)
                    // 签名时间
                    .withIssuedAt(nowDate)
                    // token过期时间
                    .withExpiresAt(expireDate)
                    // 签名
                    .sign(algorithm);
            return token;
        } catch (Exception e) {
            log.error("generateToken exception", e);
        }
        return null;
    }

    /**
     * 生成JWT Token (根据oldToken生成)
     * @param oldToken
     */
    public static String generateNewTokenFormOldToken(String oldToken) {
        try {
            if (StringUtils.isBlank(oldToken)) {
                log.error("oldToken不能为空");
                return null;
            }

            Long userId = getUserId(oldToken);
            String username = getUsername(oldToken);

            String newToken = generateToken(userId,username);
            return newToken;
        } catch (Exception e) {
            log.error("generateNewTokenFormOldToken exception", e);
        }
        return null;
    }

    /**
     * 从请求头或者请求参数中
     *
     * @param request
     * @return
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        if (request == null) {
            request =  HttpServletRequestUtil.getRequest();
        }
        // 从请求头中获取token
        String token = request.getHeader(JwtProperties.tokenName);
        if (StringUtils.isBlank(token)) {
            // 从请求参数中获取token
            token = request.getParameter(JwtProperties.tokenName);
        }
        return token;
    }


    /**
     * 主要是校验token的salt值,和username
     * @param token
     * @param userName
     * @return
     */
   /* public static boolean verifyToken(String token, String userName) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JwtProperties.secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    // 主题
                    .withSubject(userName)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("Verify Token Exception", e);
        }
        return false;
    }*/

    /**
     * 解析token，获取token数据
     *
     * @param token
     * @return
     */
    public static DecodedJWT getJwtInfo(String token) {
        return JWT.decode(token);
    }


    /**
     * 获取用户id
     *
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }
        DecodedJWT decodedJwt = getJwtInfo(token);
        if (decodedJwt == null) {
            return null;
        }
        Long userId = decodedJwt.getClaim("userId").asLong();
        return userId;
    }

    /**
     * 获取用户名
     *
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }
        DecodedJWT decodedJwt = getJwtInfo(token);
        if (decodedJwt == null) {
            return null;
        }
        String username = decodedJwt.getSubject();
        return username;
    }

    /**
     * 获取创建时间
     *
     * @param token
     * @return
     */
    public static Date getIssuedAt(String token) {
        DecodedJWT decodedJwt = getJwtInfo(token);
        if (decodedJwt == null) {
            return null;
        }
        return decodedJwt.getIssuedAt();
    }

    /**
     * 获取过期时间
     *
     * @param token
     * @return
     */
    public static Date getExpireDate(String token) {
        DecodedJWT decodedJwt = getJwtInfo(token);
        if (decodedJwt == null) {
            return null;
        }
        return decodedJwt.getExpiresAt();
    }

    /**
     * 判断token是否已过期
     *
     * @param token
     * @return
     */
    public static boolean isExpired(String token) {
        Date expireDate = getExpireDate(token);
        if (expireDate == null) {
            return true;
        }
        return expireDate.before(new Date());
    }

    /**
     *
     * @description 判断是否刷新token
     * @param token
     * @throws Exception
     */
    public static boolean isRefreshToken(String token) {
        // 判断是否刷新token
        boolean isRefreshToken = JwtProperties.refreshToken;
        if (!isRefreshToken) {
            return false;
        }
        // 获取过期时间
        Date expireDate = JwtUtil.getExpireDate(token);
        // 获取倒计时
        int countdown = JwtProperties.refreshTokenCountdown;
        // 如果(当前时间+倒计时) > 过期时间，则刷新token
        boolean refresh = DateUtils.addMinutes(new Date(), countdown).after(expireDate);

        if (!refresh) {
            return false;
        }

        return true;
    }

}
