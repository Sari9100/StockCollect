package com.example.stockcollect.controller;

import com.example.stockcollect.service.NotionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class NotionTestController {

    private final NotionService notionService;

    public NotionTestController(NotionService notionService) {
        this.notionService = notionService;
    }

    @PostMapping("/save-price")
    public String savePrice(
            @RequestParam String ticker,
            @RequestParam Double price,
            @RequestParam(required = false) String date) {


        System.out.println("📌 saveClosingPrice 호출됨 → " + ticker + ", " + price + ", " + date);

        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        notionService.saveClosingPrice(ticker.toUpperCase(), price, targetDate);
        return "✅ 저장 완료";
    }
}