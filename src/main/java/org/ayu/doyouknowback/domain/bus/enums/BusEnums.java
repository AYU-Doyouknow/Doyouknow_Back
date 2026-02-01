package org.ayu.doyouknowback.domain.bus.enums;

public enum BusEnums {
    ANYtoAYU("208000069", "241253001", "10", "안양역"),
    AYUtoANY("208000299", "241253001", "25", "안양대학교");

    private final String stationId;
    private final String routeId;
    private final String staOrder;
    private final String stationName;

    BusEnums(String stationId, String routeId, String staOrder, String stationName) {
        this.stationId = stationId;
        this.routeId = routeId;
        this.staOrder = staOrder;
        this.stationName = stationName;
    }

    public String stationId() { return stationId; }
    public String routeId() { return routeId; }
    public String staOrder() { return staOrder; }
    public String stationName() { return stationName; }
}
