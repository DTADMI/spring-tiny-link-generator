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

}
