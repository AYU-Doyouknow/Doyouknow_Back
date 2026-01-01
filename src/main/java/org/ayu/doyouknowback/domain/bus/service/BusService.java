package org.ayu.doyouknowback.domain.bus.service;

import org.ayu.doyouknowback.domain.bus.direction.BusDirection;
import org.ayu.doyouknowback.domain.bus.form.BusRequestDTO;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${bus.key}")
    private String serviceKey;

    public BusResponseDTO getArrival(BusRequestDTO request) {
        BusDirection dir;
        try {
            dir = BusDirection.valueOf(request.direction());
        } catch (Exception e) {
            return BusResponseDTO.builder()
                    .success(false)
                    .message("direction 값이 올바르지 않습니다. (ANYtoAYU / AYUtoANY)")
                    .build();
        }

        return getArrivalByDirection(dir);
    }

    public List<BusResponseDTO> getArrivals(List<BusRequestDTO> requests) {
        List<BusResponseDTO> results = new ArrayList<>();
        for (BusRequestDTO req : requests) {
            results.add(getArrival(req));
        }
        return results;
    }

    private BusResponseDTO getArrivalByDirection(BusDirection dir) {
        try {
            String urlString = buildUrl(dir.stationId(), dir.routeId(), dir.staOrder());
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) sb.append(line);

            rd.close();
            conn.disconnect();

            JsonNode root = objectMapper.readTree(sb.toString());
            JsonNode item = root.path("response").path("msgBody").path("busArrivalItem");

            if (item.isMissingNode() || item.isNull() || item.size() == 0) {
                return BusResponseDTO.builder()
                        .stationId(dir.stationId())
                        .stationName(dir.stationName())
                        .routeId(dir.routeId())
                        .staOrder(dir.staOrder())
                        .success(false)
                        .message("도착 정보 없음")
                        .build();
            }

            JsonNode target = item.isArray() ? item.get(0) : item;

            String plateNo = target.path("plateNo1").asText("");
            String locationNo = target.path("locationNo1").asText("");
            String predictTime = target.path("predictTime1").asText("");
// 1. 초 단위 데이터 가져오기 (기본값 0)
            int totalSeconds = target.path("predictTimeSec1").asInt(0);
            String formattedTimeSec;

// 2. "M분 S초" 형식으로 변환 로직
            if (totalSeconds > 0) {
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;

                if (minutes > 0) {
                    formattedTimeSec = String.format("%d분 %d초", minutes, seconds);
                } else {
                    formattedTimeSec = String.format("%d초", seconds);
                }
            } else {
                // 초 정보가 없을 경우 기존 분 정보를 활용하거나 빈 값 처리
                formattedTimeSec = predictTime.isEmpty() ? "" : predictTime + "분";
            }
            return BusResponseDTO.builder()
                    .stationId(dir.stationId())
                    .stationName(dir.stationName())
                    .routeId(dir.routeId())
                    .staOrder(dir.staOrder())
                    .plateNo(plateNo)
                    .locationNo(locationNo)
                    .predictTime(predictTime)
                    .formattedTimeSec(formattedTimeSec)
                    .success(true)
                    .message("OK")
                    .build();

        } catch (Exception e) {
            return BusResponseDTO.builder()
                    .stationId(dir.stationId())
                    .stationName(dir.stationName())
                    .routeId(dir.routeId())
                    .staOrder(dir.staOrder())
                    .success(false)
                    .message("API 호출/파싱 실패: " + e.getMessage())
                    .build();
        }
    }

    private String buildUrl(String stationId, String routeId, String staOrder) throws Exception {
        String encodedKey = serviceKey.contains("%")
                ? serviceKey                //인코딩 키 사용
                : URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);

        StringBuilder urlBuilder = new StringBuilder(
                "https://apis.data.go.kr/6410000/busarrivalservice/v2/getBusArrivalItemv2"
        );
        urlBuilder.append("?serviceKey=").append(encodedKey);
        urlBuilder.append("&stationId=").append(URLEncoder.encode(stationId, StandardCharsets.UTF_8));
        urlBuilder.append("&routeId=").append(URLEncoder.encode(routeId, StandardCharsets.UTF_8));
        urlBuilder.append("&staOrder=").append(URLEncoder.encode(staOrder, StandardCharsets.UTF_8));
        urlBuilder.append("&format=json");
        return urlBuilder.toString();
    }
}
