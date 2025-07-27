package com.example.stockcollect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NotionService {

    @Value("${notion.api.token}")
    private String notionToken;

    @Value("${notion.api.version}")
    private String notionVersion;

    @Value("${notion.database.ticker_id}")
    private String tickerDbId;

    @Value("${notion.database.price_id}")
    private String priceDbId;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<String> getWatchTickers() throws Exception {
        String notionUrl = "https://api.notion.com/v1/databases/" + tickerDbId + "/query";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(notionToken);
        headers.set("Notion-Version", notionVersion);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = """
            {
              "filter": {
                "property": "감시 여부",
                "checkbox": {
                  "equals": true
                }
              }
            }
            """;

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(notionUrl, HttpMethod.POST, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());

        List<String> result = new ArrayList<>();
        Set<String> tickerSet = new HashSet<>(); // 중복 제거용
        
        for (JsonNode node : root.get("results")) {
            JsonNode tickerNode = node.get("properties").get("티커").get("rich_text");
            if (tickerNode != null && tickerNode.size() > 0) {
                String raw = tickerNode.get(0).get("plain_text").asText();
                String ticker = raw.trim().toUpperCase();  // 개행 제거 + 대문자 변환
                tickerSet.add(ticker);
            }
        }

        result.addAll(tickerSet);
        Collections.sort(result);  // 알파벳 정렬 (선택)

        return result;
    }


    public void saveClosingPrice(String ticker, Double price, LocalDate date){
      try{
      System.out.println("Save Cloding Price");

        String notionUrl = "https://api.notion.com/v1/pages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(notionToken);
        headers.set("Notion-Version", notionVersion);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("""
        {
          "parent": {
            "database_id": "%s"
          },
          "properties": {
            "티커": {
              "rich_text": [
                {
                  "text": {
                    "content": "%s"
                  }
                }
              ]
            },
            "날짜": {
              "date": {
                "start": "%s"
              }
            },
            "종가": {
              "number": %.2f
            }
          }
        }
        """, priceDbId, ticker, date.toString(), price);



        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(notionUrl, HttpMethod.POST, entity, String.class);

        String message = String.format("✅ 저장 완료: %s (%s → %.2f)", ticker, date.toString(), price);
        System.out.println(message);
      }catch (Exception e) {
        System.err.printf("❌ 저장 실패: %s (%s) → %s\n", ticker, date, e.getMessage());
    }
  
  }
}

