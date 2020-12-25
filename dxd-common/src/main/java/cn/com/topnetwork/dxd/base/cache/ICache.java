package cn.com.topnetwork.dxd.base.cache;

import java.util.List;
import java.util.Map;

/**
 * cache 接口
 */
public interface ICache {

    /**
     * 添加缓存数据
     * @param cacheName
     * @param key
     * @param value
     * @param expireTime
     */
    void putElement(String cacheName, String key, Object value,Long expireTime);

    /**
     * 判断key是否存在cache中
     * @param cacheName
     * @param key
     */
    boolean containsKey(String cacheName, String key);

    /**
     * 获取缓存数据  by key
     * @param cacheName
     * @param key
     * @return
     */
    Object getElementByKey(String cacheName, String key);

    /**
     *  获取缓存数据  all
     * @param cacheName
     * @return
     */
    Map<String,Object> getAllElements(String cacheName);

    /**
     * 获取缓存数据  by keyPrefix
     * @param cacheName
     * @param keyPrefix
     * @return
     */
    List<Object> getElementsByKeyPrefix(String cacheName, String keyPrefix);

    /**
     * 删除缓存数据  by key
     * @param cacheName
     * @param key
     */
    void delElementByKey(String cacheName, String key);
}
