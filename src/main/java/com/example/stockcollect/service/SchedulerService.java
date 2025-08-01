package com.example.stockcollect.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.stockcollect.DTO.StockPriceDTO;

@Service
public class SchedulerService {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    private final NotionService notionService;
    private final StockPriceService stockPriceService;

    public SchedulerService(NotionService notionService, StockPriceService stockPriceService) {
        this.notionService = notionService;
        this.stockPriceService = stockPriceService;
    }

    // 스케줄러: 월요일부터 토요일까지 매일 오전 7시 10분에 실행
    @Scheduled(cron = "0 20 7 * * MON-SAT", zone = "Asia/Seoul")
    public void collectAndSaveAllClosingPrices() {
        logger.info("📅 스케줄러 실행: 종가 저장 시작");

        try {
            // 종목 티커 정보를 가져옴
            Map<String, String> tickerMap = notionService.getTickerMap();
            logger.info("📈 {}개의 종목에 대해 종가를 조회합니다.", tickerMap.size());

            // 종목별로 종가를 조회하고 저장
            for (Map.Entry<String, String> entry : tickerMap.entrySet()) {
                String ticker = entry.getKey();
                StockPriceDTO dto = stockPriceService.getTodayStockPrice(ticker);

                if (dto != null && dto.getClose() != null) {
                    notionService.saveClosingPrice(dto.getTicker(), dto.getClose(), dto.getDate());
                    logger.info("✅ 종가 저장 성공: {} - {}", ticker, dto.getClose());
                } else {
                    logger.warn("❌ 종가 조회 실패: {}", ticker);
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 오류 로그 출력
            logger.error("❌ 스케줄러 오류: {}", e.getMessage(), e);
        }
    }
}