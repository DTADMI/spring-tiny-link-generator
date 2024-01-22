package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dto.PaginatedUrlPairsResultDto;
import ca.dtadmi.tinylink.model.UrlPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MarshallServiceTest {

    @Test
    void test_getUrlPairFromFirestore_returnsUrlPairWithCorrectData() {
        // Arrange
        Map<String, Object> serializedData = new HashMap<>();
        serializedData.put("id", "123");
        serializedData.put("longUrl", "https://www.example.com");
        serializedData.put("shortUrl", "https://tinylink.com/abc");
        serializedData.put("creationDate", "2022-01-01");

        // Act
        UrlPair result = MarshallService.getUrlPairFromFirestore(serializedData);

        // Assert
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("https://www.example.com", result.getLongUrl());
        assertEquals("https://tinylink.com/abc", result.getShortUrl());
        assertEquals("2022-01-01", result.getCreationDate());
    }

    @Test
    void test_most_recent_results_returns_sorted_list() {
        // Arrange
        List<UrlPair> urlPairs = new ArrayList<>();
        UrlPair urlPair1 = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        UrlPair urlPair2 = new UrlPair("2", "https://www.example2.com", "https://tinylink.com/def", "Thu Jan 18 19:04:50 EST 2024");
        urlPairs.add(urlPair1);
        urlPairs.add(urlPair2);

        // Act
        List<UrlPair> result = MarshallService.mostRecentResults(urlPairs, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(urlPair2, result.get(0));
        assertEquals(urlPair1, result.get(1));
    }

    @Test
    void test_paginate_results_returns_correct_data() {
        // Arrange
        List<UrlPair> urlPairs = new ArrayList<>();
        UrlPair urlPair1 = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        UrlPair urlPair2 = new UrlPair("2", "https://www.example2.com", "https://tinylink.com/def", "Thu Jan 18 19:04:50 EST 2024");
        urlPairs.add(urlPair1);
        urlPairs.add(urlPair2);

        // Act
        PaginatedUrlPairsResultDto result = MarshallService.paginateResults(1, 10, urlPairs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals(urlPair1, result.getData().get(0));
        assertEquals(urlPair2, result.getData().get(1));
        assertEquals(1, result.getMetadata().getPage());
        assertEquals(10, result.getMetadata().getPerPage());
        assertEquals(1, result.getMetadata().getPageCount());
        assertEquals(2, result.getMetadata().getTotalCount());
    }

    @Test
    void test_paginate_results_returns_correct_metadata() {
        // Arrange
        List<UrlPair> urlPairs = new ArrayList<>();
        UrlPair urlPair1 = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        UrlPair urlPair2 = new UrlPair("2", "https://www.example2.com", "https://tinylink.com/def", "Thu Jan 18 19:04:50 EST 2024");
        urlPairs.add(urlPair1);
        urlPairs.add(urlPair2);

        // Act
        PaginatedUrlPairsResultDto result = MarshallService.paginateResults(1, 10, urlPairs);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMetadata().getPage());
        assertEquals(10, result.getMetadata().getPerPage());
        assertEquals(1, result.getMetadata().getPageCount());
        assertEquals(2, result.getMetadata().getTotalCount());
    }
}
