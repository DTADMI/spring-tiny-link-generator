package ca.dtadmi.tinylink.controller;

import ca.dtadmi.tinylink.dto.PaginatedUrlPairsResultDto;
import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import ca.dtadmi.tinylink.model.UrlPair;
import ca.dtadmi.tinylink.service.CounterService;
import ca.dtadmi.tinylink.service.CryptoService;
import ca.dtadmi.tinylink.service.MarshallService;
import ca.dtadmi.tinylink.service.UrlPairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api/tinylink/urlPairs")
public class UrlPairController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UrlPairService urlPairService;
    private final CounterService counterService;
    private final CryptoService cryptoService;

    @Value("${server.base.url}")
    private String serverBaseUrl;

    @Value("${max.number.history.results}")
    private int maxNumberHistoryResults;

    @Value("${tiny.link.size}")
    private int tinyLinkSize;

    public UrlPairController(UrlPairService urlPairService, CounterService counterService, CryptoService cryptoService) {
        this.urlPairService = urlPairService;
        this.counterService = counterService;
        this.cryptoService = cryptoService;
    }

    @GetMapping("")
    public ResponseEntity<PaginatedUrlPairsResultDto> getMostRecentlyAddedUrlPairs(@RequestParam(defaultValue = "1") String page, @RequestParam(defaultValue = "1") String limit) throws ApiRuntimeException {
        List<UrlPair> results = urlPairService.findAll();
        List<UrlPair> mostRecentResults = MarshallService.mostRecentResults(results, maxNumberHistoryResults);
        PaginatedUrlPairsResultDto paginatedMostRecentResults = MarshallService.paginateResults(page, limit, mostRecentResults);
        return new ResponseEntity<>(paginatedMostRecentResults, HttpStatus.OK);
    }

    @PostMapping("/shortUrl")
    public ResponseEntity<String> fromLongToShortUrl(@RequestBody UrlPair urlPair) throws NoSuchElementException, ApiRuntimeException {
        if(urlPair == null || urlPair.getLongUrl() == null || urlPair.getLongUrl().isBlank()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Parameter longUrl is empty.", new Date());
        }
        String longUrl = urlPair.getLongUrl();
        UrlPair urlPairInDB = this.urlPairService.findByLongUrl(longUrl);
        if(urlPairInDB != null) {
            return new ResponseEntity<>(urlPairInDB.getShortUrl(), HttpStatus.OK);
        }
        int count = this.counterService.getCountFromZookeeper();
        String encryptedValue = this.cryptoService.base62EncodeLong(count);
        // Might be useless to substring since there are 62^tinyLinkSize possible results,
        // even at 1000 shorts urls generated per second, it would take more than 26Ml years to reach the end...
        String shortUrl = serverBaseUrl + encryptedValue.substring(0, Math.min(encryptedValue.length(), tinyLinkSize));
        urlPair.setShortUrl(shortUrl);
        this.urlPairService.create(urlPair);
        return new ResponseEntity<>(shortUrl, HttpStatus.OK);
    }

    @PostMapping("/longUrl")
    public ResponseEntity<String> fromShortToLongUrl(@RequestBody UrlPair urlPair) throws NoSuchElementException, ApiRuntimeException {
        if(urlPair == null || urlPair.getShortUrl() == null || urlPair.getShortUrl().isBlank()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Parameter shortUrl is empty.", new Date());
        }
        String shortUrl = urlPair.getShortUrl();
        UrlPair urlPairInDB = this.urlPairService.findAll().stream().filter(Objects::nonNull).filter(pair -> pair.getShortUrl().equals(shortUrl)).findFirst().orElse(null);
        if(urlPairInDB != null) {
            return new ResponseEntity<>(urlPairInDB.getShortUrl(), HttpStatus.OK);
        }
        this.logger.error("The provided short url wasn't generated by this system: {}", shortUrl);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("")
    public ResponseEntity<UrlPair> deleteUrlPairs() throws NoSuchElementException, ApiRuntimeException {
        urlPairService.removeAll();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<UrlPair> deleteUrlPairById(@PathVariable String id) throws NoSuchElementException, ApiRuntimeException {
        if(id.isBlank()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Parameter id is empty.", new Date());
        }
        urlPairService.remove(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/byLongUrl")
    public ResponseEntity<UrlPair> deleteUrlPairByLongUrl(@RequestParam(defaultValue = "") String longUrl) throws NoSuchElementException, ApiRuntimeException {
        if(longUrl.isBlank()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Parameter longUrl is empty.", new Date());
        }
        UrlPair urlPairToDelete = urlPairService.findByLongUrl(longUrl);
        if(urlPairToDelete!=null) {
            urlPairService.remove(urlPairToDelete.getId());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
