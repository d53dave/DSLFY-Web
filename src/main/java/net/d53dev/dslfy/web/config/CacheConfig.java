package net.d53dev.dslfy.web.config;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by davidsere on 16/11/15.
 */
@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(cacheConfig(ConfigConstants.EH_CACHE_NAME, ConfigConstants.EH_CACHE_MAXSIZE));
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public org.springframework.cache.CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver();
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }

    private CacheConfiguration cacheConfig(String name, long maxEntries) {
        CacheConfiguration config = new CacheConfiguration();
        config.setName(name);
        config.setMaxEntriesLocalHeap(maxEntries);
        config.setMemoryStoreEvictionPolicy(ConfigConstants.EH_CACHE_POLICY);
        return config;
    }
}
