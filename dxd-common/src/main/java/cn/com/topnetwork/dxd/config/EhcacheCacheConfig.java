package cn.com.topnetwork.dxd.config;

import cn.com.topnetwork.dxd.constant.properties.JwtProperties;
import cn.com.topnetwork.dxd.util.CacheUtil;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Searchable;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * <p>
 *     ehcache Cache配置
 * </p>
 * @author tianbaoyan
 * @date 2019-11-08
 */
@Configuration
@DependsOn(value = "jwtProperties")
public class EhcacheCacheConfig extends CachingConfigurerSupport {

    @Bean(destroyMethod="shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        //指定Cache的Searchable对象。
        Searchable searchable = new Searchable();
        //存放TOKEN_WITH_EXPIRED的缓存空间
        CacheConfiguration tokenExpiredCfg = new CacheConfiguration();
        tokenExpiredCfg.searchable(searchable);

        tokenExpiredCfg.setName(CacheUtil.CACHENAME_LOGIN_USER_TOKEN_EXPIRED);
        tokenExpiredCfg.setTimeToIdleSeconds(JwtProperties.transitionSecond);
        tokenExpiredCfg.setTimeToLiveSeconds(JwtProperties.transitionSecond);
        tokenExpiredCfg.setMaxEntriesLocalHeap(10000);
        tokenExpiredCfg.setMemoryStoreEvictionPolicy("LRU");

        //存放TOKEN_WITH_USE 的缓存空间(当前在用token)
        CacheConfiguration tokenCurrUseCfg = new CacheConfiguration();
        tokenCurrUseCfg.searchable(searchable);

        tokenCurrUseCfg.setName(CacheUtil.CACHENAME_LOGIN_USER_TOKEN_CURRENT);
        tokenCurrUseCfg.setTimeToIdleSeconds(JwtProperties.expireCacheSecond);
        tokenCurrUseCfg.setTimeToLiveSeconds(JwtProperties.expireCacheSecond);
        tokenCurrUseCfg.setMaxEntriesLocalHeap(10000);
        tokenCurrUseCfg.setMemoryStoreEvictionPolicy("LRU");

        //存放当前在线登陆用户的缓存空间(online)
        CacheConfiguration userOnlineCfg = new CacheConfiguration();
        userOnlineCfg.searchable(searchable);

        userOnlineCfg.setName(CacheUtil.CACHENAME_LONGIN_USER_ONLINE);
       // userOnlineCfg.setTimeToIdleSeconds(JwtProperties.expireCacheSecond);
       // userOnlineCfg.setTimeToLiveSeconds(JwtProperties.expireCacheSecond);
        userOnlineCfg.setMaxEntriesLocalHeap(10000);
        userOnlineCfg.setMemoryStoreEvictionPolicy("LRU");

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        //可以创建多个cacheConfiguration，都添加到Config中
        config.addCache(tokenExpiredCfg);
        config.addCache(tokenCurrUseCfg);
        config.addCache(userOnlineCfg);
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheResolver cacheResolver() { return null; }

    @Override
    public CacheErrorHandler errorHandler() {
        return null;
    }

}
