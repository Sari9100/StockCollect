package com.example.stockcollect.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockcollect.service.NotionService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class NotionController {
    private final NotionService notionService;

    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    @GetMapping("/tickers")
    public List<String> getWatchTickers() throws Exception {
        return notionService.getWatchTickers();
    }

    
}
