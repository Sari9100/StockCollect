package com.example.stockcollect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.stockcollect.DTO.StockPriceDTO;
import com.example.stockcollect.service.SchedulerService;
import com.example.stockcollect.service.StockPriceService;

@Component
public class TestRunner implements CommandLineRunner {
    @Autowired
    private SchedulerService schedulerService;

    @Override
    public void run(String... args) {
        System.out.println("🚀 수동 실행: collectAndSaveAllClosingPrices()");
        schedulerService.collectAndSaveAllClosingPrices();
    }
}