package org.ayu.doyouknowback.weather.domain.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {

    private String temperature;   // 현재 온도
    private String condition;     // 날씨 상태
    private String summary;       // 요약 정보
    private String feelsLike;     // 체감 온도
    private String humidity;      // 습도
    private String windSpeed;     // 풍속

//    private AdditionalInfoDTO additionalInfo; // 추가 정보 (미세먼지, 자외선, 일몰 시간 등)
//
//    @Getter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class AdditionalInfoDTO {
//        private String pm10;    // 미세먼지
//        private String pm25;    // 초미세먼지
//        private String uv;      // 자외선
//        private String sunset;  // 일몰 시간
//    }
}