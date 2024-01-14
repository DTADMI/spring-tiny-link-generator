package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dao.UrlPairDao;
import ca.dtadmi.tinylink.model.UrlPair;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "urlCache")
public class UrlPairService {

    private final UrlPairDao urlPairDao;
    private final CachingService cachingService;

    public UrlPairService(UrlPairDao urlPairDao, CachingService cachingService) {
        this.urlPairDao = urlPairDao;
        this.cachingService = cachingService;
    }

    public List<UrlPair> findAll() {
        List<UrlPair> cachedUrlPairs = this.cachingService.getSingleCacheList("urlCache", "all").orElse(null);
        if(cachedUrlPairs != null) {
            return cachedUrlPairs;
        }
        List<UrlPair> urlPairs = urlPairDao.getAll().stream().filter(Objects::nonNull).toList();
        if(!urlPairs.isEmpty()) {
            this.cachingService.cacheSingleList("urlCache", "all", urlPairs);
        }

        return urlPairs;
    }

    @Cacheable(key = "#longUrl", unless = "#result == null")
    public UrlPair find(String longUrl) {
        return urlPairDao.get(longUrl).orElse(null);
    }

    public void create(UrlPair urlPair) {
        urlPairDao.save(urlPair);
    }
}
