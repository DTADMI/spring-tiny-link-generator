package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.dto.PaginatedUrlPairsResultDto;
import ca.dtadmi.tinylink.model.UrlPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarshallService {
    private static final Logger logger = LoggerFactory.getLogger(MarshallService.class);

    private MarshallService() {
    }

    public static UrlPair getUrlPairFromFirestore(Map<?, ?> serializedData){
        if(serializedData == null){
            return null;
        }
        UrlPair urlPair = new UrlPair();
        urlPair.setId((String) serializedData.get("id"));
        urlPair.setLongUrl((String) serializedData.get("longUrl"));
        urlPair.setShortUrl((String) serializedData.get("shortUrl"));
        urlPair.setCreationDate((String) serializedData.get("creationDate"));

        return urlPair;
    }

    public static List<UrlPair> mostRecentResults(List<UrlPair> urlPairs, int maxNumberHistoryResults) {
        if(urlPairs.isEmpty()) {
            return urlPairs;
        }
        List<UrlPair> resultsData = new ArrayList<>(urlPairs);
        logger.debug("Getting creation date sorted urlPairs {} most recent elements", maxNumberHistoryResults);
        return resultsData.stream()
                .sorted(Comparator.comparing((UrlPair urlPair) -> new Date(urlPair.getCreationDate()).getTime()).reversed())
                .limit(maxNumberHistoryResults).toList();
    }

    public static PaginatedUrlPairsResultDto paginateResults(String page, int perPage, List<UrlPair> urlPairs) {
        if(urlPairs.isEmpty()) {
            return new PaginatedUrlPairsResultDto(urlPairs, new PaginatedUrlPairsResultDto.Metadata(1, 0, 0, 0));
        }
        List<UrlPair> resultsData = new ArrayList<>(urlPairs);
        int numberUrlPairs = resultsData.size();

        logger.info("Getting sliced urlPairs with {} elements starting at page {}", perPage, page);
        // To optimize the display, 4 links are too much,
        // thus limit cannot exceed 3, and page cannot exceed the number of pages
        // Both cannot be less than 1 since pages start at 1, and we cannot take 0 elements
        int pageCount = Math.ceilDivExact(numberUrlPairs, perPage);
        int limitInt = Math.max(1, perPage);
        int pageInt = Math.max(1, Math.min(Integer.parseInt(page), pageCount));
        int startIndex = Math.max(0, (pageInt - 1) * limitInt);
        int endIndex = Math.min(numberUrlPairs, (pageInt) * limitInt);
        logger.info("Getting sliced urlPairs from {} to {} excluded", startIndex, endIndex);
        PaginatedUrlPairsResultDto result = new PaginatedUrlPairsResultDto();
        List<UrlPair> data = resultsData.subList(startIndex, endIndex);

        result.setData(data);
        result.setMetadata(new PaginatedUrlPairsResultDto.Metadata(pageInt, perPage, pageCount, numberUrlPairs));

        return result;
    }

}
