package ca.dtadmi.tinylink.service;

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

    public static List<UrlPair> paginateResults(String page, String limit, List<UrlPair> urlPairs) {
        if(urlPairs.isEmpty()) {
            return urlPairs;
        }
        List<UrlPair> resultsData = new ArrayList<>(urlPairs);
        int numberTasks = resultsData.size();

        logger.debug("Getting sliced urlPairs with {} elements starting at page {}", limit, page);
        // To optimize the display, 4 links are too much,
        // thus limit cannot exceed 3, and page cannot exceed the number of pages
        // Both cannot be less than 1 since pages start at 1, and we cannot take 0 elements
        int perPage = Math.min(Integer.parseInt(limit), 3);
        int pageCount = Math.ceilDivExact(numberTasks, perPage);
        int limitInt = Math.max(1, Math.min(perPage, numberTasks));
        int pageInt = Math.max(1, Math.min(Integer.parseInt(page), pageCount));
        int startIndex = (pageInt - 1) * limitInt;
        int endIndex = (pageInt) * limitInt;
        logger.debug("Getting sliced urlPairs from {} to {} excluded", startIndex, endIndex);

        return resultsData.subList(startIndex, endIndex);
    }

}
