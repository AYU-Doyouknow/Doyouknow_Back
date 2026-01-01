package org.ayu.doyouknowback.domain.bus.form;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BusResponseDTO {
    private String stationId;
    private String stationName;
    private String routeId;
    private String staOrder;

    private String plateNo;
    private String locationNo;
    private String predictTime;
    private String formattedTimeSec;

    private boolean success;
    private String message;
}




