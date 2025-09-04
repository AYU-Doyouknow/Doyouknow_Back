//package org.ayu.doyouknowback.weather.domain;
//
//import jakarta.persistence.Embedded;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.ayu.doyouknowback.weather.domain.form.WeatherRequestDTO;
//import org.springframework.data.annotation.Id;
//
//@Entity
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//@Getter
//public class Weather {
//
//    @Id
//    private Long id = 1L;
//
//    private String temperature;   // 현재 온도
//    private String condition;     // 날씨 상태
//    private String summary;       // 요약 정보
//    private String feelsLike;     // 체감 온도
//    private String humidity;      // 습도
//    private String windSpeed;     // 풍속
//
//    @Embedded
//    private AdditionalInfo additionalInfo; // 미세먼지, 자외선 등 추가 정보
//
//    public static Weather toSaveEntity(WeatherRequestDTO dto) {
//        return Weather.builder()
//                .temperature(dto.getTemperature())
//                .condition(dto.getCondition())
//                .summary(dto.getSummary())
//                .feelsLike(dto.getFeelsLike())
//                .humidity(dto.getHumidity())
//                .windSpeed(dto.getWindSpeed())
//                .additionalInfo(AdditionalInfo.builder()
//                        .pm10(dto.getAdditionalInfo().getPm10())
//                        .pm25(dto.getAdditionalInfo().getPm25())
//                        .uv(dto.getAdditionalInfo().getUv())
//                        .sunset(dto.getAdditionalInfo().getSunset())
//                        .build())
//                .build();
//    }
//
//}
