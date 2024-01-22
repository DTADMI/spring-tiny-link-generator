package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dao.UrlPairDao;
import ca.dtadmi.tinylink.model.UrlPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlPairServiceTest {

    @Mock
    private CachingService cachingService;
    @Mock
    private UrlPairDao urlPairDao;
    @InjectMocks
    private UrlPairService urlPairService;
    private static final String CACHE_NAME = "urlCache";

    @BeforeEach
    public void setUp() {
        urlPairService = new UrlPairService(urlPairDao, cachingService);
    }

    @Test
    void test_findAll_should_return_a_list_of_UrlPair_objects_when_the_cache_is_empty() {
        // Arrange
        List<UrlPair> expected = List.of(
                new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01"));
        when(cachingService.getSingleCacheList(CACHE_NAME, "all")).thenReturn(Optional.empty());
        when(urlPairDao.getAll()).thenReturn(expected);

        // Act
        List<UrlPair> actual = urlPairService.findAll();

        // Assert
        assertEquals(expected, actual);
        verify(cachingService, times(1)).cacheSingleList(CACHE_NAME, "all", expected);
    }

    @Test
    void test_findAll_should_return_a_list_of_UrlPair_objects_from_the_cache_when_available() {
        // Arrange
        List<UrlPair> expected = List.of(
                new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01"));
        when(cachingService.getSingleCacheList(CACHE_NAME, "all")).thenReturn(Optional.of(expected));

        // Act
        List<UrlPair> actual = urlPairService.findAll();

        // Assert
        assertEquals(expected, actual);
        verify(urlPairDao, never()).getAll();
        verify(cachingService, never()).cacheSingleList(CACHE_NAME, "all", expected);
    }

    @Test
    void test_findAll_should_return_an_empty_list_when_there_are_no_UrlPair_objects_in_the_database() {
        // Arrange
        List<UrlPair> expected = new ArrayList<>();
        when(cachingService.getSingleCacheList(CACHE_NAME, "all")).thenReturn(Optional.empty());
        when(urlPairDao.getAll()).thenReturn(new ArrayList<>());

        // Act
        List<UrlPair> actual = urlPairService.findAll();

        // Assert
        assertEquals(expected, actual);
        verify(cachingService, never()).cacheSingleList(CACHE_NAME, "all", expected);
    }

    @Test
    void test_findUrlPairById() {
        // Arrange
        String id = "123";
        UrlPair expectedUrlPair = new UrlPair(id, "https://www.example.com", "abc", "2022-01-01");
        when(urlPairDao.get(id)).thenReturn(Optional.of(expectedUrlPair));

        // Act
        UrlPair actualUrlPair = urlPairService.find(id);

        // Assert
        assertEquals(expectedUrlPair, actualUrlPair);
    }

    @Test
    void test_findUrlPairByLongUrl() {
        // Arrange
        String longUrl = "https://www.example.com";
        UrlPair expectedUrlPair = new UrlPair("123", longUrl, "abc", "2022-01-01");
        when(urlPairDao.getAll()).thenReturn(Collections.singletonList(expectedUrlPair));

        // Act
        UrlPair actualUrlPair = urlPairService.findByLongUrl(longUrl);

        // Assert
        assertEquals(expectedUrlPair, actualUrlPair);
    }

    @Test
    void test_findUrlPairByShortUrl() {
        // Arrange
        String shortUrl = "abc";
        UrlPair expectedUrlPair = new UrlPair("123", "https://www.example.com", shortUrl, "2022-01-01");
        when(urlPairDao.getAll()).thenReturn(Collections.singletonList(expectedUrlPair));

        // Act
        UrlPair actualUrlPair = urlPairService.findByShortUrl(shortUrl);

        // Assert
        assertEquals(expectedUrlPair, actualUrlPair);
    }

    @Test
    void test_findUrlPairById_NotFound() {
        // Arrange
        String id = "123";
        when(urlPairDao.get(id)).thenReturn(Optional.empty());

        // Act
        UrlPair actualUrlPair = urlPairService.find(id);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_findUrlPairByLongUrl_NotFound() {
        // Arrange
        String longUrl = "https://www.example.com";
        when(urlPairDao.getAll()).thenReturn(Collections.emptyList());

        // Act
        UrlPair actualUrlPair = urlPairService.findByLongUrl(longUrl);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_findUrlPairByShortUrl_NotFound() {
        // Arrange
        String shortUrl = "abc";
        when(urlPairDao.getAll()).thenReturn(Collections.emptyList());

        // Act
        UrlPair actualUrlPair = urlPairService.findByShortUrl(shortUrl);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_createUrlPair() {
        // Arrange
        UrlPair urlPair = new UrlPair("123", "https://www.example.com", "abc", "2022-01-01");
        when(urlPairDao.save(urlPair)).thenReturn(urlPair);
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.create(urlPair);

        // Assert
        verify(urlPairDao, times(1)).save(urlPair);
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }

    @Test
    void test_removeUrlPairById() {
        // Arrange
        String id = "123";
        doNothing().when(urlPairDao).delete(id);
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.remove(id);

        // Assert
        verify(urlPairDao, times(1)).delete(id);
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }

    @Test
    void test_removeAllUrlPairs() {
        // Arrange
        doNothing().when(urlPairDao).deleteAll();
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.removeAll();

        // Assert
        verify(urlPairDao, times(1)).deleteAll();
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }
}
