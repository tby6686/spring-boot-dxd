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

import cn.com.topnetwork.dxd.base.cache.ICache;
import cn.com.topnetwork.dxd.constant.ConstantFrame;
import cn.com.topnetwork.dxd.constant.properties.JwtProperties;
import cn.com.topnetwork.dxd.system.entity.LoginSysUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 缓存操作工具类
 *
 * @author tby
 * @date 2018-11-08
 */
@Slf4j
@Component
public class CacheUtil {


    /**
     * 登录用户username token(在用)
     * login:user:token:[username]:[tokenMd5]
     */
    public static String CACHENAME_LOGIN_USER_TOKEN_CURRENT="LOGIN_USER_TOKEN_CURRENT";

    /**
     * 登录用户username token(已过期)
     * login:user:token:[username]:[tokenMd5]
     */
    public static String CACHENAME_LOGIN_USER_TOKEN_EXPIRED="LOGIN_USER_TOKEN_EXPIRED";
    private static String KEY_LOGIN_USER_TOKEN = "login:user:token:%s:%s";

    /**
     * 根据用户名称查询从属该用户的所有token
     */
    private static String KEY_LOGIN_USER_TOKEN_ALLPREFIX = "login:user:token:%s:";

    /**
     * 登录用户信息key(在线)
     * login:user:[username]
     */
    private static String KEY_LOGIN_USER_ONLINE = "login:user:%s";
    public static String CACHENAME_LONGIN_USER_ONLINE="LONGIN_USER_ONLINE";

    private static ICache cache;

    public CacheUtil() {
        CacheUtil.cache = ApplicationContextUtil.getBeanByClassPath(ConstantFrame.cacheClassPath,ICache.class);
        log.debug("CacheUtil 初始化>>cache:"+cache);
    }


    /**
     * 当前登陆用户对象缓存  >>保存用户
     * @param loginSysUserVo
     */
    public static void cacheLoginSysUser(LoginSysUserVo loginSysUserVo){
        String userOnlineCacheKey = String.format(KEY_LOGIN_USER_ONLINE,loginSysUserVo.getUsername());
        cache.putElement(CACHENAME_LONGIN_USER_ONLINE,userOnlineCacheKey,loginSysUserVo,null);
    }

    /**
     * 当前登陆用户对象缓存  >>是否存在key(username)
     * @param username
     * @return
     */
    public static boolean judgeLoginSysUserContainsUsername(String username){
        String cacheUserKey = String.format(KEY_LOGIN_USER_ONLINE, username);
        boolean isContains =  cache.containsKey(CACHENAME_LONGIN_USER_ONLINE,cacheUserKey);
        return isContains;
    }

    /**
     * 当前登陆用户对象缓存  >>获取用户(by username)
     * @param username
     */
    public static LoginSysUserVo getLoginSysUserByUsername(String username){
        String cacheUserKey = String.format(KEY_LOGIN_USER_ONLINE, username);
        LoginSysUserVo loginSysUserVo = (LoginSysUserVo) cache.getElementByKey(CACHENAME_LONGIN_USER_ONLINE, cacheUserKey);
        return loginSysUserVo;
    }


    /**
     * 当前登陆用户对象缓存  >>删除用户(by username)
     * @param username
     */
    public static void delLoginSysUserByUsername(String username){
        //由于支持用户并发访问，存放多个token。当用户token不存在时，删除
        List<String> userAllTokenCache = getLoginSysUserTokenAllByUsername(username);
        if(CollectionUtils.isEmpty(userAllTokenCache)){
            String cacheUserKey = String.format(KEY_LOGIN_USER_ONLINE,username);
            cache.delElementByKey(CACHENAME_LONGIN_USER_ONLINE,cacheUserKey);
        }
    }

    /**
     * 当前登陆用户token缓存  >>保存TOKEN
     * @param username
     * @param token
     * @param isCurrentTokenCache  true=当前在用token缓存/false=过期token缓存
     */
    public static void cacheLoginSysUserToken(String username,String token,boolean isCurrentTokenCache){
        String tokenMd5 = DigestUtils.md5Hex(token);
        String tokenCacheKey = String.format(KEY_LOGIN_USER_TOKEN,username,tokenMd5);
        if(isCurrentTokenCache){
            cache.putElement(CACHENAME_LOGIN_USER_TOKEN_CURRENT,tokenCacheKey,token,JwtProperties.expireCacheSecond);
        }else {
            cache.putElement(CACHENAME_LOGIN_USER_TOKEN_EXPIRED,tokenCacheKey,token,JwtProperties.transitionSecond);
        }
    }


    /**
     * 当前登陆用户token缓存  >>是否存在key(username + token)
     * @param username
     * @param token
     * @param isCurrentTokenCache  true=当前在用token缓存/fale=过期token缓存
     * @return
     */
    public static boolean judgeLoginSysUserTokenContainsKey(String username,String token,boolean isCurrentTokenCache){
        boolean result = false;
        String tokenMd5 = DigestUtils.md5Hex(token);
        String tokenCacheKey = String.format(KEY_LOGIN_USER_TOKEN,username,tokenMd5);
        if(isCurrentTokenCache){
            result = cache.containsKey(CACHENAME_LOGIN_USER_TOKEN_CURRENT,tokenCacheKey);
        }else {
            result = cache.containsKey(CACHENAME_LOGIN_USER_TOKEN_EXPIRED,tokenCacheKey);
        }
        return result;
    }

    /**
     * 当前登陆用户token缓存  >>获取缓存信息(username + token)
     * @param username
     * @param token
     * @param isCurrentTokenCache  true=当前在用token缓存/fale=过期token缓存
     * @return
     */
    public static String getLoginSysUserTokenByKey(String username,String token,boolean isCurrentTokenCache){
        String result = "";
        String tokenMd5 = DigestUtils.md5Hex(token);
        String tokenCacheKey = String.format(KEY_LOGIN_USER_TOKEN,username,tokenMd5);
        if(isCurrentTokenCache){
            result = cache.getElementByKey(CACHENAME_LOGIN_USER_TOKEN_CURRENT,tokenCacheKey)+"";
        }else {
            result = cache.getElementByKey(CACHENAME_LOGIN_USER_TOKEN_EXPIRED,tokenCacheKey)+"";
        }
        return result;
    }

    /**
     * 当前登陆用户token缓存  >>获取缓存信息(username)
     * 根据用户名，从TOKEN_CURRENT和TOKEN_EXPIRED 中获取用户缓存的所有token信息
     * @param username
     * @return
     */
    public static List<String> getLoginSysUserTokenAllByUsername(String username){
        List<Object> userCacheAllList = new LinkedList<>();
        String cacheKeyPrefix = String.format(KEY_LOGIN_USER_TOKEN_ALLPREFIX, username);
        List<Object> currentList = cache.getElementsByKeyPrefix(CACHENAME_LOGIN_USER_TOKEN_CURRENT, cacheKeyPrefix);
        List<Object> expiredList = cache.getElementsByKeyPrefix(CACHENAME_LOGIN_USER_TOKEN_EXPIRED, cacheKeyPrefix);
        userCacheAllList.addAll(currentList);
        userCacheAllList.addAll(expiredList);
        List<String> resultList = userCacheAllList.stream().map(Object::toString).collect(Collectors.toList());
        return resultList;
    }


    /**
     * 当前登陆用户token缓存  >>删除缓存信息(username + token)
     * @param username
     * @param token
     * @param isCurrentTokenCache  true=当前在用token缓存/fale=过期token缓存
     * @return
     */
    public static void delLoginSysUserTokenByKey(String username,String token,boolean isCurrentTokenCache){
        String tokenMd5 = DigestUtils.md5Hex(token);
        String tokenCacheKey = String.format(KEY_LOGIN_USER_TOKEN,username,tokenMd5);
        if(isCurrentTokenCache){
            cache.delElementByKey(CACHENAME_LOGIN_USER_TOKEN_CURRENT,tokenCacheKey);
        }else {
            cache.delElementByKey(CACHENAME_LOGIN_USER_TOKEN_EXPIRED,tokenCacheKey);
        }
    }

    /**
     * 打印缓存中所有数据
     */
    public static void printCacheAllElement(){

        Map<String,Object> loginUserCache = cache.getAllElements(CACHENAME_LONGIN_USER_ONLINE);
        System.out.println(CACHENAME_LONGIN_USER_ONLINE+"size["+loginUserCache.size()+"]");
        for(Map.Entry<String,Object> entry : loginUserCache.entrySet()){
            System.out.println("key>>"+entry.getKey()+",val>>" + entry.getValue());
        }
        System.out.println("====================================");

        Map<String,Object> loginUserTokenCurrCache = cache.getAllElements(CACHENAME_LOGIN_USER_TOKEN_CURRENT);
        System.out.println(CACHENAME_LOGIN_USER_TOKEN_CURRENT+"size["+loginUserTokenCurrCache.size()+"]");
        for(Map.Entry<String,Object> entry : loginUserTokenCurrCache.entrySet()){
            System.out.println("key>>"+entry.getKey()+",val>>" + entry.getValue());
        }
        System.out.println("====================================");

        Map<String,Object> loginUserTokenExpiredCache = cache.getAllElements(CACHENAME_LOGIN_USER_TOKEN_EXPIRED);
        System.out.println(CACHENAME_LOGIN_USER_TOKEN_EXPIRED+"size["+loginUserTokenExpiredCache.size()+"]");
        for(Map.Entry<String,Object> entry : loginUserTokenExpiredCache.entrySet()){
            System.out.println("key>>"+entry.getKey()+",val>>" + entry.getValue());
        }
        System.out.println("====================================");
    }

}
