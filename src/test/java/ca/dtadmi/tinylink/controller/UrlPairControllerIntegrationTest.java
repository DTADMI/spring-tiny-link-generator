package ca.dtadmi.tinylink.controller;

import ca.dtadmi.tinylink.config.AppConfig;
import ca.dtadmi.tinylink.dto.PaginatedUrlPairsResultDto;
import ca.dtadmi.tinylink.model.UrlPair;
import ca.dtadmi.tinylink.service.CounterService;
import ca.dtadmi.tinylink.service.CryptoService;
import ca.dtadmi.tinylink.service.UrlPairService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ServletWebServerFactoryAutoConfiguration.class},
        webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = {AppConfig.class})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class UrlPairControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CounterService counterService;
    @MockBean
    private CryptoService cryptoService;
    @MockBean
    private UrlPairService urlPairService;

    @Value("${server.base.url}")
    private String serverBaseUrl;

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
        assertThat(context.getBean(UrlPairController.class)).isNotNull();
        assertThat(context.getBean(UrlPairService.class)).isNotNull();
    }

    @Test
    @DisplayName("GET request to /api/tinylink/urlPairs returns a list of most recently added UrlPairs")
    void test_getMostRecentlyAddedUrlPairs() throws Exception {
        int maxNumberEntriesPerPage = 3;
        // Mock dependencies
        List<UrlPair> results = List.of(
                new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024"),
                new UrlPair("2", "https://www.google.com", "https://tinylink.com/def", "Thu Jan 18 19:50:00 EST 2024")
        );
        PaginatedUrlPairsResultDto paginatedResults = new PaginatedUrlPairsResultDto(results, new PaginatedUrlPairsResultDto.Metadata( 1, maxNumberEntriesPerPage, 1, 2));
        when(urlPairService.findAll()).thenReturn(results);

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tinylink/urlPairs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(paginatedResults)))
                .andReturn();
    }

    @Test
    @DisplayName("GET request to /api/tinylink/urlPairs with an invalid page parameter returns an error")
    void test_getMostRecentlyAddedUrlPairs_invalidPageParameter() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/tinylink/urlPairs?page=invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/shortUrl with a valid UrlPair creates a shortUrl and returns it")
    void test_fromLongToShortUrl_validUrlPair() throws Exception {
        // Mock dependencies
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", null, null);
        when(urlPairService.findByLongUrl(urlPair.getLongUrl())).thenReturn(null);
        when(counterService.getCountFromZookeeper()).thenReturn(7);
        when(cryptoService.base62EncodeLong(7)).thenReturn("abc");
        urlPair.setShortUrl(serverBaseUrl + "abc");
        doNothing().when(urlPairService).create(any(UrlPair.class));

        ObjectMapper mapper = new ObjectMapper();

        // Verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(urlPair)))
                .andExpect(status().isOk())
                .andExpect(content().string(urlPair.getShortUrl()))
                .andReturn();
    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/shortUrl with an existing longUrl returns the existing shortUrl")
    void test_fromLongToShortUrl_existingLongUrl() throws Exception {
        // Mock dependencies
        UrlPair urlPair = new UrlPair("1", "https://www.example.com", "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findByLongUrl(urlPair.getLongUrl())).thenReturn(urlPair);

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(urlPair)))
                .andExpect(status().isOk())
                .andExpect(content().string(urlPair.getShortUrl()))
                .andReturn();

    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/shortUrl with an invalid UrlPair returns an error")
    void test_fromLongToShortUrl_invalidUrlPair() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/shortUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/longUrl with an invalid UrlPair returns an error")
    void test_fromShortToLongUrl_invalidUrlPair() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/longUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/longUrl with an existing shortUrl returns the corresponding longUrl")
    void test_postRequestWithExistingShortUrl_returnsCorrespondingLongUrl() throws Exception {
        // Mock dependencies
        String shortUrl = serverBaseUrl + "abc";
        String longUrl = "https://www.example.com";
        UrlPair urlPair = new UrlPair("1", longUrl, shortUrl, "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findAll()).thenReturn(List.of(urlPair));

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/longUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(urlPair)))
                .andExpect(status().isOk())
                .andExpect(content().string(urlPair.getLongUrl()))
                .andReturn();
    }

    @Test
    @DisplayName("POST request to /api/tinylink/urlPairs/longUrl with shortUrl not generated by the system throws bad request exception")
    void test_postRequestWithShortUrlNotFromSystem_throwsBadRequestException() throws Exception {
        // Mock dependencies
        String shortUrl = "https://tinylink.com/abc";
        String longUrl = "https://www.example.com";
        UrlPair urlPair = new UrlPair("1", longUrl, shortUrl, "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findAll()).thenReturn(Collections.emptyList());

        ObjectMapper mapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/tinylink/urlPairs/longUrl")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(urlPair)))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @DisplayName("DELETE request to /api/tinylink/urlPairs deletes all UrlPairs")
    void test_deleteRequest_deletesAllUrlPairs() throws Exception {
        doNothing().when(urlPairService).removeAll();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tinylink/urlPairs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    @DisplayName("DELETE request to /api/tinylink/urlPairs/{id} deletes the UrlPair with the corresponding id")
    void test_deleteRequestWithId_deletesUrlPairWithCorrespondingId() throws Exception {
        // Mock dependencies
        String id = "1";
        doNothing().when(urlPairService).remove(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tinylink/urlPairs/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
    }

    @Test
    @DisplayName("DELETE request to /api/tinylink/urlPairs/byLongUrl with an existing longUrl deletes the corresponding UrlPair")
    void test_deleteUrlPairByLongUrl_existingLongUrl() throws Exception {
        // Mock dependencies
        String longUrl = "https://www.example.com";
        UrlPair urlPair = new UrlPair("1", longUrl, "https://tinylink.com/abc", "Thu Jan 18 18:44:00 EST 2024");
        when(urlPairService.findByLongUrl(longUrl)).thenReturn(urlPair);
        doNothing().when(urlPairService).remove(urlPair.getId());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tinylink/urlPairs/byLongUrl?longUrl=" + longUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andReturn();
        verify(urlPairService, times(1)).remove(urlPair.getId());
    }

    @Test
    @DisplayName("DELETE request to /api/tinylink/urlPairs/byLongUrl with a non-existing longUrl returns an error")
    void test_deleteUrlPairByLongUrl_nonExistingLongUrl() throws Exception {
        // Mock dependencies
        String longUrl = "https://www.example.com";
        when(urlPairService.findByLongUrl(longUrl)).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/tinylink/urlPairs/byLongUrl?longUrl=" + longUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        verify(urlPairService, times(0)).remove(anyString());
    }
}
