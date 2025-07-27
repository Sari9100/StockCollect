package com.example.stockcollect.service;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StockPriceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StockPriceService(RestTemplate restTemplate)
    {
        this.restTemplate= restTemplate;
    }

    public Double GetTodayClosingPrice(String ticker) {
        try {
            String url = String.format("https://query1.finance.yahoo.com/v8/finance/chart/%s?interval=1d&range=1d", ticker);

            // 1. Header에 User-Agent 추가
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0"); // 브라우저로 가장

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. exchange 방식으로 요청
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            String json = response.getBody();

            // 3. JSON 파싱
            JsonNode root = objectMapper.readTree(json);
            JsonNode chart = root.get("chart");
            JsonNode result = chart.get("result").get(0);
            JsonNode quote = result.get("indicators").get("quote").get(0);
            JsonNode close = quote.get("close");

            if (close != null && close.get(0) != null) {
                return close.get(0).asDouble();
            }
        } catch (Exception e) {
            System.err.println("❌ 종가 조회 실패 (" + ticker + "): " + e.getMessage());
        }

        return null;
    }

}
