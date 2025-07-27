package com.example.stockcollect.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockcollect.service.StockPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/price")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    public StockPriceController(StockPriceService stockPriceService)
    {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/{ticker}")
    public Double getClosePice(@PathVariable String ticker) {
        return stockPriceService.GetTodayClosingPrice(ticker);
    }
    
}
