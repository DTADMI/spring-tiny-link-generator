package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dao.UrlPairDao;
import ca.dtadmi.tinylink.model.UrlPair;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "urlCache")
public class UrlPairService {

    private final UrlPairDao urlPairDao;
    private final CachingService cachingService;
    private static final String CACHE_NAME = "urlCache";

    public UrlPairService(UrlPairDao urlPairDao, CachingService cachingService) {
        this.urlPairDao = urlPairDao;
        this.cachingService = cachingService;
    }

    public List<UrlPair> findAll() {
        List<UrlPair> cachedUrlPairs = this.cachingService.getSingleCacheList(CACHE_NAME, "all").orElse(null);
        if(cachedUrlPairs != null) {
            return cachedUrlPairs;
        }
        List<UrlPair> urlPairs = urlPairDao.getAll().stream().filter(Objects::nonNull).collect(Collectors.toList()); //using Collectors.toList() to avoid SerializationException
        if(!urlPairs.isEmpty()) {
            this.cachingService.cacheSingleList(CACHE_NAME, "all", urlPairs);
        }

        return urlPairs;
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public UrlPair find(String id) {
        return urlPairDao.get(id).orElse(null);
    }

    public UrlPair findByLongUrl(String longUrl) {
        return findAll().stream().filter(urlPair -> urlPair.getLongUrl().equals(longUrl)).findFirst().orElse(null);
    }

    public UrlPair findByShortUrl(String shortUrl) {
        return findAll().stream().filter(urlPair -> urlPair.getShortUrl().equals(shortUrl)).findFirst().orElse(null);
    }

    public void create(UrlPair urlPair) {
        urlPairDao.save(urlPair);
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }

    public void removeAll() {
        urlPairDao.deleteAll();
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }

    @CacheEvict(key = "#id")
    public void remove(String id) {
        urlPairDao.delete(id);
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }
}
