package com.example.stockcollect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.stockcollect.service.StockPriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 주식 가격 정보를 제공하는 REST API 컨트롤러
 */
@RestController
@RequestMapping("/price")
public class StockPriceController {
    private final StockPriceService stockPriceService;

    /**
     * StockPriceController 생성자
     * @param stockPriceService 주식 가격 서비스
     */
    public StockPriceController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    /**
     * 특정 주식 티커에 대한 오늘의 종가를 반환
     * @param ticker 주식 티커
     * @return 오늘의 종가와 HTTP 상태 코드
     */
    @GetMapping("/{ticker}")
    public ResponseEntity<Double> getClosePrice(@PathVariable String ticker) {
        Double closingPrice = stockPriceService.getTodayClosingPrice(ticker);
        return ResponseEntity.ok(closingPrice); // HTTP 200 상태 코드와 데이터 반환
    }
}
