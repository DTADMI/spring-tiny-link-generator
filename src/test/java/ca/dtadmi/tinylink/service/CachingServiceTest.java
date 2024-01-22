package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.model.UrlPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachingServiceTest {

    @Mock
    CacheManager cacheManager;
    @InjectMocks
    private CachingService cachingService;

    @BeforeEach
    public void setUp() {
        cachingService = new CachingService(cacheManager);
    }

    @Test
    void test_getSingleCacheValue_cacheExists() {
        Cache cache = mock(Cache.class);
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01");
        when(cacheManager.getCache("cacheName")).thenReturn(cache);
        when(cache.get("cacheKey", UrlPair.class)).thenReturn(urlPair);

        Object result = cachingService.getSingleCacheValue("cacheName", "cacheKey");

        assertEquals(urlPair, result);
    }

    @Test
    void test_getSingleCacheList_cacheExists() {
        Cache cache = mock(Cache.class);
        List<UrlPair> urlPairs = Arrays.asList(
                new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01"),
                new UrlPair("2", "https://www.example2.com", "https://tinylink.com/def", "2021-01-02")
        );
        when(cacheManager.getCache("cacheName")).thenReturn(cache);
        when(cache.get("cacheKey", List.class)).thenReturn(urlPairs);

        Optional<List<UrlPair>> result = cachingService.getSingleCacheList("cacheName", "cacheKey");

        assertEquals(urlPairs, result.orElse(null));
    }

    @Test
    void test_cacheSingleValue_cacheExists() {
        Cache cache = mock(Cache.class);
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01");
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        cachingService.cacheSingleValue("cacheName", "cacheKey", urlPair);

        verify(cache).put("cacheKey", urlPair);
    }

    @Test
    void test_getSingleCacheValue_cacheDoesNotExist() {
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        Object result = cachingService.getSingleCacheValue("cacheName", "cacheKey");

        assertNull(result);
    }

    @Test
    void test_getSingleCacheList_cacheDoesNotExist() {
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        Optional<List<UrlPair>> result = cachingService.getSingleCacheList("cacheName", "cacheKey");

        assertFalse(result.isPresent());
    }

    @Test
    void test_cacheSingleValue_cacheDoesNotExist() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        cachingService.cacheSingleValue("cacheName", "cacheKey", "value");

        verify(cache, never()).put(any(), any());
    }

    @Test
    void test_cache_single_list_cache_exists() {
        Cache cache = mock(Cache.class);
        List<UrlPair> list = List.of(new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01"));
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        cachingService.cacheSingleList("cacheName", "cacheKey", list);

        verify(cache).put("cacheKey", list);
    }

    @Test
    void test_evict_single_cache_value_cache_exists() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        cachingService.evictSingleCacheValue("cacheName", "cacheKey");

        verify(cache).evict("cacheKey");
    }

    @Test
    void test_evict_all_cache_values_cache_exists() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(cache);

        cachingService.evictAllCacheValues("cacheName");

        verify(cache).clear();
    }

    @Test
    void test_evict_all_caches() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(cache);
        when(cacheManager.getCacheNames()).thenReturn(List.of("cacheName"));

        cachingService.evictAllCaches();

        verify(cache, times(1)).clear();
    }

    @Test
    void test_evict_all_caches_at_intervals() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(cache);
        when(cacheManager.getCacheNames()).thenReturn(List.of("cacheName"));

        cachingService.evictAllCachesAtIntervals();

        verify(cache, times(1)).clear();
    }

    @Test
    void test_does_not_cache_if_cache_does_not_exist_when_caching_single_list() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cacheName")).thenReturn(null);

        List<UrlPair> list = new ArrayList<>();
        cachingService.cacheSingleList("cacheName", "cacheKey", list);

        verify(cache, never()).put(anyString(), any());
    }
}
