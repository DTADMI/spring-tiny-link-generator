package ca.dtadmi.tinylink.controller;

import ca.dtadmi.tinylink.service.CachingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class CachingController {
    CachingService cachingService;
    public CachingController(CachingService cachingService) {
        this.cachingService = cachingService;
    }

    @GetMapping("/clearAllCaches")
    public void clearAllCaches() {
        cachingService.evictAllCaches();
    }
}
