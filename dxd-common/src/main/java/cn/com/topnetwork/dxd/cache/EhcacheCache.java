package cn.com.topnetwork.dxd.cache;

import cn.com.topnetwork.dxd.base.cache.ICache;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EhcacheCache implements ICache {

    @Autowired
    private CacheManager cacheManager;

    /*private volatile static CacheManager cacheManager = null;

    public TokenCache(){
        if (cacheManager == null) {
            synchronized (TokenCache.class) {
                if (cacheManager == null) {
                    //获取EhCacheCacheManager类
                    EhCacheCacheManager cacheCacheManager=ApplicationContextUtil.getBean(EhCacheCacheManager.class);
                    //获取CacheManager类
                    cacheManager=cacheCacheManager.getCacheManager();
                }
            }
        }
    }*/

    /**
     * 添加缓存数据
     * @param cacheName
     * @param key
     * @param value
     * @param expireTime
     */
    @Override
    public void putElement(String cacheName, String key, Object value,Long expireTime) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            Element element = new Element(key, value);
            if(expireTime!=null){
                element.setTimeToIdle(expireTime.intValue());
                element.setTimeToLive(expireTime.intValue());
            }
            cache.put(element);
        } catch (Exception e) {
            log.error("添加缓存失败",e);
        }
    }

    @Override
    public boolean containsKey(String cacheName, String key){
        Cache cache = cacheManager.getCache(cacheName);
        boolean isExist = cache.isKeyInCache(key);
        return isExist;
    }

    /**
     * 获取缓存数据
     * @param cacheName
     * @param key
     * @return
     */
    @Override
    public Object getElementByKey(String cacheName, String key) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            Element element = cache.get(key);
            return element == null ? null : element.getObjectValue();
        } catch (Exception e) {
            log.error("获取缓存数据失败",e);
            return null;
        }
    }

    @Override
    public Map<String, Object> getAllElements(String cacheName) {
        Map<String,Object> resultMap = new HashMap<>();
        try {
            Cache cache = cacheManager.getCache(cacheName);
            List<String> keys = cache.getKeys();
            if(CollectionUtils.isNotEmpty(keys)){
                for(String key:keys){
                    Element element = cache.get(key);
                    Object val = element.getObjectValue();
                    resultMap.put(key,val);
                }
            }
        } catch (Exception e) {
            log.error("获取缓存数据失败",e);
            return null;
        }
        return resultMap;
    }

    @Override
    public List<Object> getElementsByKeyPrefix(String cacheName, String keyPrefix) {
        try {
            Cache cache = cacheManager.getCache(cacheName);

            Attribute<String> keyAttr = cache.getSearchAttribute("key");
            Query query = cache.createQuery();
            query.addCriteria(keyAttr.ilike(keyPrefix+"*"));
            query.includeKeys();
            query.includeValues();
            Results results = query.execute();
            List<Object> resultList = new LinkedList<>();
            for(Result res : results.all()){
                resultList.add(res.getValue());
            }
            return resultList;
        } catch (Exception e) {
            log.error("获取缓存数据失败",e);
            return null;
        }
    }

    /**
     * 删除缓存数据
     * @param cacheName
     * @param key
     */
    @Override
    public void delElementByKey(String cacheName, String key) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if(cache.isKeyInCache(key)){
                cache.remove(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除缓存数据失败：{}",e.getMessage());
        }
    }
}
