package ca.dtadmi.tinylink.service;

import ca.dtadmi.tinylink.model.UrlPair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarshallService {

    private MarshallService() {
    }

    public static UrlPair getUrlPairFromFirestore(Map<?, ?> serializedData){
        if(serializedData == null){
            return null;
        }
        UrlPair urlPair = new UrlPair();
        urlPair.setLongUrl((String) serializedData.get("longUrl"));
        urlPair.setShortUrl((String) serializedData.get("shortUrl"));

        return urlPair;
    }

}
