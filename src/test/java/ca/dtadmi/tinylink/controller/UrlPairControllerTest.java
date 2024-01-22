package ca.dtadmi.tinylink.controller;

import ca.dtadmi.tinylink.dto.PaginatedUrlPairsResultDto;
import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import ca.dtadmi.tinylink.model.UrlPair;
import ca.dtadmi.tinylink.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlPairControllerTest {
    @Mock
    private CounterService counterService;
    @Mock
    private CryptoService cryptoService;
    @Mock
    private UrlPairService urlPairService;
    @InjectMocks
    private UrlPairController urlPairController;

    @BeforeEach
    public void setUp() {
        urlPairController = new UrlPairController(urlPairService, counterService, cryptoService);
    }

    @Test
    @DisplayName("Should get most recently added url pairs when making a GET request to /api/tinylink/urlPairs")
    void test_getMostRecentlyAddedUrlPairs() {
        // Mock dependencies
        List<UrlPair> results = List.of(
            new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024"),
            new UrlPair("2", "https://www.google.com", "https://tinylink.com/def", "Thu Jan 18 19:50:00 EST 2024")
        );
        when(urlPairService.findAll()).thenReturn(results);

        try(MockedStatic<MarshallService> mocked = mockStatic(MarshallService.class)) {
            int maxNumberEntriesPerPage = 3;
            PaginatedUrlPairsResultDto paginatedResults = new PaginatedUrlPairsResultDto(results, new PaginatedUrlPairsResultDto.Metadata( 1, maxNumberEntriesPerPage, 1, 2));
            mocked.when(() -> MarshallService.mostRecentResults(anyList(), anyInt())).thenReturn(results);
            mocked.when(() -> MarshallService.paginateResults(anyInt(), anyInt(), anyList())).thenReturn(paginatedResults);

            // Invoke the controller method
            ResponseEntity<PaginatedUrlPairsResultDto> response = urlPairController.getMostRecentlyAddedUrlPairs("1");

            // Verify the response
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(results, response.getBody().getData());
        }
    }

    @Test
    @DisplayName("Should create a new short url and return it when making a POST request to /api/tinylink/urlPairs/shortUrl")
    void test_fromLongToShortUrl_validUrlPair() {
        // Mock dependencies
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", null, null);
        when(urlPairService.findByLongUrl(urlPair.getLongUrl())).thenReturn(null);
        when(counterService.getCountFromZookeeper()).thenReturn(7);
        when(cryptoService.base62EncodeLong(7)).thenReturn("abc");
        urlPair.setShortUrl("https://tinylink.com/abc");
        doNothing().when(urlPairService).create(urlPair);

        // Invoke the controller method
        ResponseEntity<String> response = urlPairController.fromLongToShortUrl(urlPair);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(urlPair.getShortUrl(), response.getBody());
    }

    @Test
    @DisplayName("Should return the corresponding existing short url when making a POST request to /api/tinylink/urlPairs/shortUrl with an existing long url")
    void test_fromLongToShortUrl_existingLongUrl() {
        // Mock dependencies
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findByLongUrl(urlPair.getLongUrl())).thenReturn(urlPair);

        // Invoke the controller method
        ResponseEntity<String> response = urlPairController.fromLongToShortUrl(urlPair);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(urlPair.getShortUrl(), response.getBody());
    }

    @Test
    @DisplayName("Should throw an exception when making a GET request to /api/tinylink/urlPairs with an invalid page parameter")
    void test_getMostRecentlyAddedUrlPairs_invalidPageParameter() {
        // Verify the thrown exception when the controller method is invoked with invalid page parameter
        assertThrows(ApiRuntimeException.class, () -> urlPairController.getMostRecentlyAddedUrlPairs("invalid"));
    }

    @Test
    @DisplayName("Should throw an exception when making a POST request to /api/tinylink/urlPairs/shortUrl with an invalid UrlPair")
    void test_fromLongToShortUrl_invalidUrlPair() {
        // Verify the thrown exception when the controller method is invoked with invalid UrlPair
        assertThrows(ApiRuntimeException.class, () -> urlPairController.fromLongToShortUrl(null));
    }

    @Test
    @DisplayName("Should throw an exception when making a POST request to /api/tinylink/urlPairs/longUrl with an invalid UrlPair")
    void test_fromShortToLongUrl_invalidUrlPair() {
        // Verify the thrown exception when the controller method is invoked with invalid UrlPair
        assertThrows(ApiRuntimeException.class, () -> urlPairController.fromShortToLongUrl(null));
    }

    @Test
    @DisplayName("Should return the corresponding long url when making a POST request to /api/tinylink/urlPairs/longUrl with an existing short url")
    void test_postRequestWithExistingShortUrl_returnsCorrespondingLongUrl() {
        // Mock dependencies
        String shortUrl = "https://tinylink.com/abc";
        String longUrl = "https://www.example.com";
        UrlPair urlPair = new UrlPair("1", longUrl, shortUrl, "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findAll()).thenReturn(List.of(urlPair));

        // Create request body
        UrlPair requestBody = new UrlPair();
        requestBody.setShortUrl(shortUrl);

        // Invoke the controller method
        ResponseEntity<String> response = urlPairController.fromShortToLongUrl(requestBody);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(longUrl, response.getBody());
    }

    @Test
    @DisplayName("Should delete all url pairs when making a DELETE request to /api/tinylink/urlPairs")
    void test_deleteRequest_deletesAllUrlPairs() {
        doNothing().when(urlPairService).removeAll();

        // Invoke the controller method
        ResponseEntity<UrlPair> response = urlPairController.deleteUrlPairs();

        // Verify the response
        verify(urlPairService, times(1)).removeAll();
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete url pair with corresponding id when making a DELETE request to /api/tinylink/urlPairs/{id}")
    void test_deleteRequestWithId_deletesUrlPairWithCorrespondingId() {
        // Mock dependencies
        String id = "1";
        doNothing().when(urlPairService).remove(id);

        // Invoke the controller method
        ResponseEntity<UrlPair> response = urlPairController.deleteUrlPairById(id);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(urlPairService, times(1)).remove(id);
    }

    @Test
    @DisplayName("Should delete url pair with corresponding id when making a DELETE request to /api/tinylink/urlPairs/byLongUrl")
    void test_deleteUrlPairByLongUrl_existingLongUrl() {
        // Mock dependencies
        String longUrl = "https://www.example.com";
        UrlPair urlPair = new UrlPair("1", longUrl, "https://tinylink.com/abc", "2022-01-01");
        when(urlPairService.findByLongUrl(longUrl)).thenReturn(urlPair);
        doNothing().when(urlPairService).remove(urlPair.getId());

        // Invoke the controller method
        ResponseEntity<UrlPair> response = urlPairController.deleteUrlPairByLongUrl(longUrl);

        // Verify the response
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(urlPairService, times(1)).remove(urlPair.getId());
    }

    @Test
    @DisplayName("Should throw an exception when making a DELETE request to /api/tinylink/urlPairs/{id} with an invalid id")
    void test_deleteUrlPairById_invalidId() {
        // Mock dependencies
        String id = "";

        // Verify the response
        assertThrows(ApiRuntimeException.class, () -> urlPairController.deleteUrlPairById(id));
        verify(urlPairService, never()).remove(id);
    }

    @Test
    @DisplayName("Should throw an exception when making a DELETE request to /api/tinylink/urlPairs/byLongUrl with an invalid long url")
    void test_deleteUrlPairByLongUrl_nonExistingLongUrl() {
        // Mock dependencies
        String longUrl = "https://www.example.com";
        when(urlPairService.findByLongUrl(longUrl)).thenReturn(null);

        // Invoke the controller method
        ResponseEntity<UrlPair> response = urlPairController.deleteUrlPairByLongUrl(longUrl);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(urlPairService, never()).remove(anyString());
    }

}
