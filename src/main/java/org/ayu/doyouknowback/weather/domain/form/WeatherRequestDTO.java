package org.ayu.doyouknowback.weather.domain.form;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRequestDTO {

    private String temperature;
    private String condition;
    private String summary;
    private String feelsLike;
    private String humidity;
    private String windSpeed;

//    private AdditionalInfoDTO additionalInfo;
//
//    @Getter
//    @Setter
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Builder
//    public static class AdditionalInfoDTO {
//        private String pm10;   // 미세먼지
//        private String pm25;   // 초미세먼지
//        private String uv;     // 자외선
//        private String sunset; // 일몰
//    }
}

