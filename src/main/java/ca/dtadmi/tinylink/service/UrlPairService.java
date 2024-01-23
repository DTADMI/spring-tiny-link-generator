package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dao.UrlPairFirestoreDao;
import ca.dtadmi.tinylink.dao.UrlPairMongoRepository;
import ca.dtadmi.tinylink.model.UrlPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CachingService cachingService;
    private final UrlPairFirestoreDao urlPairFirestoreDao;
    private final UrlPairMongoRepository urlPairMongoRepository;
    private static final String CACHE_NAME = "urlCache";

    public UrlPairService(CachingService cachingService, UrlPairFirestoreDao urlPairFirestoreDao, UrlPairMongoRepository urlPairMongoRepository) {
        this.cachingService = cachingService;
        this.urlPairFirestoreDao = urlPairFirestoreDao;
        this.urlPairMongoRepository = urlPairMongoRepository;
    }

    public List<UrlPair> findAll() {
        List<UrlPair> cachedUrlPairs = this.cachingService.getSingleCacheList(CACHE_NAME, "all").orElse(null);
        if(cachedUrlPairs != null) {
            logger.debug("Found {} cached url pairs", cachedUrlPairs.size());
            return cachedUrlPairs;
        }
        //List<UrlPair> urlPairs = urlPairFirestoreDao.findAll().stream().filter(Objects::nonNull).collect(Collectors.toList()); //using Collectors.toList() to avoid SerializationException
        List<UrlPair> urlPairs = urlPairMongoRepository.findAll().stream().filter(Objects::nonNull).collect(Collectors.toList()); //using Collectors.toList() to avoid SerializationException
        if(!urlPairs.isEmpty()) {
            this.cachingService.cacheSingleList(CACHE_NAME, "all", urlPairs);
            logger.debug("Cached {} url pairs", urlPairs.size());
        }

        return urlPairs;
    }

    @Cacheable(key = "#id", unless = "#result == null")
    public UrlPair find(String id) {
        //return urlPairFirestoreDao.findById(id).orElse(null);
        return urlPairMongoRepository.findById(id).orElse(null);
    }

    public UrlPair findByLongUrl(String longUrl) {
        return findAll().stream().filter(urlPair -> urlPair.getLongUrl().equals(longUrl)).findFirst().orElse(null);
    }

    public UrlPair findByShortUrl(String shortUrl) {
        return findAll().stream().filter(urlPair -> urlPair.getShortUrl().equals(shortUrl)).findFirst().orElse(null);
    }

    public void create(UrlPair urlPair) {
        //urlPairFirestoreDao.save(urlPair);
        urlPairMongoRepository.save(urlPair);
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }

    public void removeAll() {
        //urlPairFirestoreDao.deleteAll();
        urlPairMongoRepository.deleteAll();
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }

    @CacheEvict(key = "#id")
    public void remove(String id) {
        //urlPairFirestoreDao.deleteAllById(List.of(id));
        urlPairMongoRepository.deleteAllById(List.of(id));
        this.cachingService.evictSingleCacheValue(CACHE_NAME, "all");
    }
}
