package ca.dtadmi.tinylink.controller;

import ca.dtadmi.tinylink.exception.ApiRuntimeException;
import ca.dtadmi.tinylink.model.UrlPair;
import ca.dtadmi.tinylink.service.UrlPairService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Date;

@RestController
@RequestMapping("")
public class RedirectionController {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UrlPairService urlPairService;

    @Value("${server.base.url}")
    private String serverBaseUrl;
    public RedirectionController(UrlPairService urlPairService) {
        this.urlPairService = urlPairService;
    }

    @GetMapping("/{shortUrlCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrlCode) {
        if(shortUrlCode.isBlank()) {
            throw new ApiRuntimeException(HttpStatus.BAD_REQUEST, "Parameter shortUrlCode is empty.", new Date());
        }
        logger.debug("Calling redirect with {}", shortUrlCode);
        UrlPair urlPair = urlPairService.findByShortUrl(serverBaseUrl + shortUrlCode);
        String longUrl = urlPair.getLongUrl();
        logger.debug("Redirecting {} to {}", urlPair.getShortUrl(), urlPair.getLongUrl());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }
}
