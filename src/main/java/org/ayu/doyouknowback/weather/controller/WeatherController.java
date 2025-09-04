package org.ayu.doyouknowback.weather.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.weather.domain.form.WeatherRequestDTO;
import org.ayu.doyouknowback.weather.domain.form.WeatherResponseDTO;
import org.ayu.doyouknowback.weather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/addWeather") // 게시글 데이터 요청 저장
    public ResponseEntity<String> createWeather(@RequestBody WeatherRequestDTO weatherRequestDTO) {
        try {
            weatherService.saveWeather(weatherRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("weather Successfully Created");
        } catch (IllegalArgumentException e) { // IllegalArgumentException : 호출자가 인수로 부적절한 값을 넘길 때 던지는 예외
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getWeather")
    public ResponseEntity<WeatherResponseDTO> getWeatherDetail() {
        WeatherResponseDTO weatherResponseDTO = weatherService.getWeather();

        if (weatherResponseDTO == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.status(HttpStatus.OK).body(weatherResponseDTO);
    }
}
