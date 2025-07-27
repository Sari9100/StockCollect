package com.example.stockcollect.controller;

import com.example.stockcollect.service.NotionService;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class NotionTestController {

    private static final Logger logger = LoggerFactory.getLogger(NotionTestController.class); // 로깅 설정
    private final NotionService notionService;

    public NotionTestController(NotionService notionService) {
        this.notionService = notionService;
    }

    /**
     * 주식 종가를 저장하는 엔드포인트
     * @param ticker 주식 티커 (필수)
     * @param price 종가 (필수)
     * @param date 날짜 (선택, 없으면 오늘 날짜 사용)
     * @return 저장 결과 메시지
     */
    @PostMapping("/save-price")
    public String savePrice(
            @RequestParam String ticker,
            @RequestParam Double price,
            @RequestParam(required = false) String date) {

        // 입력값 검증
        if (price == null || price <= 0) {
            return "❌ 가격은 0보다 커야 합니다.";
        }

        logger.info("📌 saveClosingPrice 호출됨 → ticker: {}, price: {}, date: {}", ticker, price, date);

        // 날짜 처리: 입력값이 없으면 오늘 날짜 사용
        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();

        // 서비스 호출
        notionService.saveClosingPrice(ticker.toUpperCase(), price, targetDate);

        return "✅ 저장 완료";
    }
}