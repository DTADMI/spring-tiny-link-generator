package ca.dtadmi.tinylink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UtilsService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String extractPureUrl(String url) {
        //String pureUrl = url;
        if(url.startsWith("http") && url.contains("://")) {
            return url.split("://")[1];
            //pureUrl = url.substring(url.indexOf("://") + 3);
        }
        /*if(url.startsWith("http://")) {
            pureUrl = url.substring(7);
        } else if(url.startsWith("https://")) {
            pureUrl = url.substring(8);
        }*/
        return null;
    }

}
