package com.example.stockcollect.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.stockcollect.service.NotionService;

@RestController
@RequestMapping("/api") // 공통 URL prefix를 추가하여 관리 용이
public class NotionController {
    private final NotionService notionService;

    // 생성자를 통해 의존성 주입
    public NotionController(NotionService notionService) {
        this.notionService = notionService;
    }

    /**
     * 사용자가 관심 있는 주식 티커 목록을 반환합니다.
     * @return 관심 주식 티커 목록
     * @throws Exception 서비스 호출 중 발생할 수 있는 예외
     */
    @GetMapping("/tickers")
    public List<String> getWatchTickers() throws Exception {
        // 서비스 계층에서 데이터를 가져옵니다.
        return notionService.getWatchTickers();
    }
}
