package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.model.UrlPair;
import java.util.Collections;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CachingService {
    CacheManager cacheManager;

    public CachingService(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    public Object getSingleCacheValue(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            return cache.get(cacheKey, UrlPair.class);
        }

        return null;
    }

    public Optional<List<UrlPair>> getSingleCacheList(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            return Optional.ofNullable(cache.get(cacheKey, List.class));
        }

        return Optional.empty();
    }

    public void cacheSingleValue(String cacheName, String cacheKey, Object value) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            cache.put(cacheKey, value);
        }
    }

    public void cacheSingleList(String cacheName, String cacheKey, List<UrlPair> list) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            cache.put(cacheKey, list);
        }
    }

    public void evictSingleCacheValue(String cacheName, String cacheKey) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            cache.evict(cacheKey);
        }
    }

    public void evictAllCacheValues(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache!=null){
            cache.clear();
        }
    }
    public void evictAllCaches() {
        cacheManager.getCacheNames()
                .forEach(this::evictAllCacheValues);
    }
    @Scheduled(fixedRate = 60000)
    public void evictAllCachesAtIntervals() {
        evictAllCaches();
    }
}
