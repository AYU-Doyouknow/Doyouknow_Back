package org.ayu.doyouknowback.weather.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.weather.domain.form.WeatherRequestDTO;
import org.ayu.doyouknowback.weather.domain.form.WeatherResponseDTO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WeatherService {

    // 메모리에 최신 데이터 1개만 저장
    private WeatherResponseDTO latestWeather;

    // POST: 저장
    public void saveWeather(WeatherRequestDTO dto) {
        this.latestWeather = WeatherResponseDTO.builder()
                .temperature(dto.getTemperature())
                .condition(dto.getCondition())
                .summary(dto.getSummary())
                .feelsLike(dto.getFeelsLike())
                .humidity(dto.getHumidity())
                .windSpeed(dto.getWindSpeed())
//                .additionalInfo(
//                        WeatherResponseDTO.AdditionalInfoDTO.builder()
//                                .pm10(dto.getAdditionalInfo().getPm10())
//                                .pm25(dto.getAdditionalInfo().getPm25())
//                                .uv(dto.getAdditionalInfo().getUv())
//                                .sunset(dto.getAdditionalInfo().getSunset())
//                                .build()
//                )
                .build();
    }

    // GET: 조회
    public WeatherResponseDTO getWeather() {
        return this.latestWeather;
    }

}
