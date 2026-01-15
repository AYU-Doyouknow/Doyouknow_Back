package org.ayu.doyouknowback.domain.bus.form;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class BusResponseDTO {
    //private String stationId;
    private String stationName;
    //private String routeId;
    //private String staOrder;정류장 순번

    //private String plateNo;차량 번호판 가장 최근 번호판 조회
    private String locationNo;//남은 정거장 수 1순위
    private String predictTime;// 남은 시간(분 단위)
    //private String locationNo2; 남은 정거장 수 2순위
    //private String predictTime2; 남은 시간 2순위(분 단위)
    private String formattedTimeSec;// 남은 시간(초 단위)

    private boolean success;
    private String message;

    private String lastUpdatedKst; // "yyyy-MM-dd HH:mm:ss"
}




