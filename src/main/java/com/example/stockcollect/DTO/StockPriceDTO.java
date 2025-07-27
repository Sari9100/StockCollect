package com.example.stockcollect.DTO;

import lombok.Data;
import java.time.LocalDate;

// 주식 가격 정보를 담는 DTO 클래스
@Data
public class StockPriceDTO {
    // 주식 티커 (예: AAPL, TSLA 등)
    private String ticker;

    // 날짜 정보
    private LocalDate date;

    // 시가 (주식 거래 시작 가격)
    private Double open;

    // 고가 (주식 거래 중 최고 가격)
    private Double high;

    // 저가 (주식 거래 중 최저 가격)
    private Double low;

    // 종가 (주식 거래 종료 가격)
    private Double close;

    // 거래량 (주식 거래된 수량)
    private Long volume;

    // 최적화: 모든 필드에 기본값을 설정하거나 null 체크를 추가할 필요가 없으므로 Lombok의 @Data를 사용하여
    // getter, setter, toString, equals, hashCode를 자동 생성합니다.
}
