package com.example.stockcollect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockcollectApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockcollectApplication.class, args);
	}

}
