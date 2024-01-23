package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dao.UrlPairFirestoreDao;
import ca.dtadmi.tinylink.dao.UrlPairMongoRepository;
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
    private UrlPairFirestoreDao urlPairFirestoreDao;
    @Mock
    private UrlPairMongoRepository urlPairMongoRepository;
    @InjectMocks
    private UrlPairService urlPairService;
    private static final String CACHE_NAME = "urlCache";

    @BeforeEach
    public void setUp() {
        urlPairService = new UrlPairService(cachingService,urlPairFirestoreDao, urlPairMongoRepository);
    }

    @Test
    void test_findAll_should_return_a_list_of_UrlPair_objects_when_the_cache_is_empty() {
        // Arrange
        List<UrlPair> expected = List.of(
                new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "2021-01-01"));
        when(cachingService.getSingleCacheList(CACHE_NAME, "all")).thenReturn(Optional.empty());
        //when(urlPairFirestoreDao.findAll()).thenReturn(expected);
        when(urlPairMongoRepository.findAll()).thenReturn(expected);

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
        //verify(urlPairFirestoreDao, never()).findAll();
        verify(urlPairMongoRepository, never()).findAll();
        verify(cachingService, never()).cacheSingleList(CACHE_NAME, "all", expected);
    }

    @Test
    void test_findAll_should_return_an_empty_list_when_there_are_no_UrlPair_objects_in_the_database() {
        // Arrange
        List<UrlPair> expected = new ArrayList<>();
        when(cachingService.getSingleCacheList(CACHE_NAME, "all")).thenReturn(Optional.empty());
        //when(urlPairFirestoreDao.findAll()).thenReturn(new ArrayList<>());
        when(urlPairMongoRepository.findAll()).thenReturn(new ArrayList<>());

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
        //when(urlPairFirestoreDao.findById(id)).thenReturn(Optional.of(expectedUrlPair));
        when(urlPairMongoRepository.findById(id)).thenReturn(Optional.of(expectedUrlPair));

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
        //when(urlPairFirestoreDao.findAll()).thenReturn(Collections.singletonList(expectedUrlPair));
        when(urlPairMongoRepository.findAll()).thenReturn(Collections.singletonList(expectedUrlPair));

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
        //when(urlPairFirestoreDao.findAll()).thenReturn(Collections.singletonList(expectedUrlPair));
        when(urlPairMongoRepository.findAll()).thenReturn(Collections.singletonList(expectedUrlPair));

        // Act
        UrlPair actualUrlPair = urlPairService.findByShortUrl(shortUrl);

        // Assert
        assertEquals(expectedUrlPair, actualUrlPair);
    }

    @Test
    void test_findUrlPairById_NotFound() {
        // Arrange
        String id = "123";
        //when(urlPairFirestoreDao.findById(id)).thenReturn(Optional.empty());
        when(urlPairMongoRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        UrlPair actualUrlPair = urlPairService.find(id);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_findUrlPairByLongUrl_NotFound() {
        // Arrange
        String longUrl = "https://www.example.com";
        //when(urlPairFirestoreDao.findAll()).thenReturn(Collections.emptyList());
        when(urlPairMongoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        UrlPair actualUrlPair = urlPairService.findByLongUrl(longUrl);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_findUrlPairByShortUrl_NotFound() {
        // Arrange
        String shortUrl = "abc";
        //when(urlPairFirestoreDao.findAll()).thenReturn(Collections.emptyList());
        when(urlPairMongoRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        UrlPair actualUrlPair = urlPairService.findByShortUrl(shortUrl);

        // Assert
        assertNull(actualUrlPair);
    }

    @Test
    void test_createUrlPair() {
        // Arrange
        UrlPair urlPair = new UrlPair("123", "https://www.example.com", "abc", "2022-01-01");
        //when(urlPairFirestoreDao.save(urlPair)).thenReturn(urlPair);
        when(urlPairMongoRepository.save(urlPair)).thenReturn(urlPair);
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.create(urlPair);

        // Assert
        //verify(urlPairFirestoreDao, times(1)).save(urlPair);
        verify(urlPairMongoRepository, times(1)).save(urlPair);
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }

    @Test
    void test_removeUrlPairById() {
        // Arrange
        String id = "123";
        //doNothing().when(urlPairFirestoreDao).deleteAllById(List.of(id));
        doNothing().when(urlPairMongoRepository).deleteAllById(List.of(id));
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.remove(id);

        // Assert
        //verify(urlPairFirestoreDao, times(1)).deleteAllById(List.of(id));
        verify(urlPairMongoRepository, times(1)).deleteAllById(List.of(id));
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }

    @Test
    void test_removeAllUrlPairs() {
        // Arrange
        //doNothing().when(urlPairFirestoreDao).deleteAll();
        doNothing().when(urlPairMongoRepository).deleteAll();
        doNothing().when(cachingService).evictSingleCacheValue(anyString(), anyString());

        // Act
        urlPairService.removeAll();

        // Assert
        //verify(urlPairFirestoreDao, times(1)).deleteAll();
        verify(urlPairMongoRepository, times(1)).deleteAll();
        verify(cachingService, times(1)).evictSingleCacheValue(anyString(), anyString());
    }
}
