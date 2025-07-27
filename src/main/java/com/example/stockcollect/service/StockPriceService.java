package com.example.stockcollect.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.stockcollect.DTO.StockPriceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class StockPriceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StockPriceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 공통 HTTP 요청 메서드
    private JsonNode fetchStockData(String url) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return objectMapper.readTree(response.getBody());
    }

    // 오늘의 주식 가격 조회
    public StockPriceDTO getTodayStockPrice(String ticker) {
        try {
            String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d", ticker);
            JsonNode root = fetchStockData(url);

            JsonNode result = root.path("chart").path("result").get(0);
            JsonNode indicators = result.path("indicators").path("quote").get(0);
            JsonNode timestampNode = result.path("timestamp");

            if (indicators == null || indicators.isMissingNode() || timestampNode.isEmpty()) return null;

            long timestamp = timestampNode.get(0).asLong();
            LocalDate date = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("America/New_York")).toLocalDate();

            return createStockPriceDTO(ticker, date, indicators, 0);

        } catch (Exception e) {
            System.err.printf("❌ 오늘의 종가 수집 실패 (%s): %s\n", ticker, e.getMessage());
            return null;
        }
    }

    // 최근 거래일의 주식 가격 조회
    public StockPriceDTO getLatestTradingDayStockPrice(String ticker) {
        try {
            String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=7d", ticker);
            JsonNode root = fetchStockData(url);

            JsonNode result = root.path("chart").path("result").get(0);
            JsonNode quote = result.path("indicators").path("quote").get(0);
            JsonNode timestamps = result.path("timestamp");

            if (quote == null || timestamps == null || timestamps.size() == 0) {
                System.err.printf("❌ 데이터 없음: %s\n", ticker);
                return null;
            }

            int lastIndex = findLastValidIndex(quote, timestamps);
            if (lastIndex == -1) {
                System.err.printf("❌ 유효한 종가 데이터 없음: %s\n", ticker);
                return null;
            }

            long timestamp = timestamps.get(lastIndex).asLong();
            LocalDate date = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("America/New_York")).toLocalDate();

            return createStockPriceDTO(ticker, date, quote, lastIndex);

        } catch (Exception e) {
            System.err.printf("❌ 최근 거래일 종가 수집 실패 (%s): %s\n", ticker, e.getMessage());
            return null;
        }
    }

    // 오늘의 종가 조회
    public Double getTodayClosingPrice(String ticker) {
        StockPriceDTO dto = getTodayStockPrice(ticker);
        return dto != null ? dto.getClose() : null;
    }

    // 유효한 마지막 데이터 인덱스 찾기
    private int findLastValidIndex(JsonNode quote, JsonNode timestamps) {
        for (int i = timestamps.size() - 1; i >= 0; i--) {
            JsonNode closeNode = quote.path("close").get(i);
            if (closeNode != null && !closeNode.isNull()) {
                return i;
            }
        }
        return -1;
    }

    // StockPriceDTO 객체 생성
    private StockPriceDTO createStockPriceDTO(String ticker, LocalDate date, JsonNode indicators, int index) {
        StockPriceDTO dto = new StockPriceDTO();
        dto.setTicker(ticker);
        dto.setDate(date);
        dto.setOpen(indicators.path("open").get(index).asDouble());
        dto.setHigh(indicators.path("high").get(index).asDouble());
        dto.setLow(indicators.path("low").get(index).asDouble());
        dto.setClose(indicators.path("close").get(index).asDouble());
        dto.setVolume(indicators.path("volume").get(index).asLong());
        return dto;
    }
}
