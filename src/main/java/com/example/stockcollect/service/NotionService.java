package com.example.stockcollect.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class NotionService {

  @Value("${notion.token}")
  private String notionToken;

  @Value("${notion.version}")
  private String notionVersion;

  @Value("${notion.tickerDbId}")
  private String tickerDbId;

  @Value("${notion.priceDbId}")
  private String priceDbId;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public NotionService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // 공통적으로 사용하는 HTTP 헤더 생성 메서드
  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(notionToken);
    headers.set("Notion-Version", notionVersion);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  // 감시 티커 목록 조회
  public List<String> getWatchTickers() throws Exception {
    String notionUrl = "https://api.notion.com/v1/databases/" + tickerDbId + "/query";

    HttpHeaders headers = createHeaders();
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
    Set<String> tickerSet = new HashSet<>();

    for (JsonNode node : root.get("results")) {
      JsonNode properties = node.get("properties");
      if (properties == null) {
        System.err.println("⚠️ properties 없음 - 항목 건너뜀");
        continue;
      }

      JsonNode tickerProperty = properties.get("Ticker");
      if (tickerProperty == null) {
        System.err.println("⚠️ '티커' 속성 없음 - 항목 건너뜀");
        continue;
      }

      JsonNode richText = tickerProperty.get("rich_text");
      if (richText == null || !richText.isArray() || richText.size() == 0) {
        System.err.println("⚠️ 'rich_text' 비어 있음 - 항목 건너뜀");
        continue;
      }

      JsonNode plainTextNode = richText.get(0).get("plain_text");
      if (plainTextNode == null) {
        System.err.println("⚠️ 'plain_text' 없음 - 항목 건너뜀");
        continue;
      }

      String ticker = plainTextNode.asText().trim().toUpperCase();
      tickerSet.add(ticker);
    }

    List<String> result = new ArrayList<>(tickerSet);
    Collections.sort(result);
    return result;
  }

  // 티커와 페이지 ID 매핑 조회
  public Map<String, String> getTickerMap() throws Exception {
    String notionUrl = "https://api.notion.com/v1/databases/" + tickerDbId + "/query";

    HttpHeaders headers = createHeaders();
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
    Map<String, String> tickerMap = new HashMap<>();

    for (JsonNode node : root.get("results")) {
      String pageID = node.get("id").asText();

      JsonNode propertiesNode = node.get("properties");
      JsonNode tickerProperty = propertiesNode.has("Ticker") ? propertiesNode.get("Ticker") : propertiesNode.get("티커");

      if (tickerProperty == null) {
        System.err.println("❌ 'Ticker' 또는 '티커' 속성이 존재하지 않음: " + propertiesNode.fieldNames().toString());
        continue;
      }

      JsonNode textArray = tickerProperty.has("rich_text") ? tickerProperty.get("rich_text") : tickerProperty.get("title");

      if (textArray != null && textArray.size() > 0) {
        String ticker = textArray.get(0).get("plain_text").asText().trim().toUpperCase();
        tickerMap.put(ticker, pageID);
      } else {
        System.err.println("⚠️ 'plain_text' 없음: " + tickerProperty);
      }
    }

    return tickerMap;
  }

  // 종가 저장
  public void saveClosingPrice(String ticker, Double price, LocalDate date) {
    try {
      System.out.println("📌 Save Closing Price");
      String title = ticker + " - " + date;

      HttpHeaders headers = createHeaders();

      // Step 1: 동일한 title이 이미 존재하는지 확인
      String queryUrl = "https://api.notion.com/v1/databases/" + priceDbId + "/query";
      String queryBody = String.format("""
        {
          "filter": {
          "property": "title",
          "title": {
            "equals": "%s"
          }
          }
        }
      """, title);

      HttpEntity<String> queryEntity = new HttpEntity<>(queryBody, headers);
      ResponseEntity<String> queryResponse = restTemplate.exchange(queryUrl, HttpMethod.POST, queryEntity, String.class);
      JsonNode queryResult = objectMapper.readTree(queryResponse.getBody());

      if (queryResult.get("results").size() > 0) {
        System.out.printf("⏭ 이미 저장된 데이터: %s → 저장 생략\n", title);
        return; // 저장 생략
      }

      // Step 2: 저장 진행
      String createUrl = "https://api.notion.com/v1/pages";

      String ticketPageID = getTickerMap().get(ticker);
      if (ticketPageID == null) {
        System.err.printf("❌ 티커 페이지를 찾을 수 없습니다: %s\n", ticker);
        return;
      }

      String createBody = String.format("""
        {
          "parent": {
          "database_id": "%s"
          },
          "properties": {
          "title": {
            "title": [
            {
              "text": {
              "content": "%s"
              }
            }
            ]
          },
          "Ticker": {
            "relation": [
            { "id": "%s" }
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
      """, priceDbId, title, ticketPageID, date.toString(), price);

      HttpEntity<String> createEntity = new HttpEntity<>(createBody, headers);
      restTemplate.exchange(createUrl, HttpMethod.POST, createEntity, String.class);

      System.out.printf("✅ 저장 완료: %s (%s → %.2f)\n", ticker, date, price);
    } catch (Exception e) {
      System.err.printf("❌ 저장 실패: %s (%s) → %s\n", ticker, date, e.getMessage());
    }
  }
}
