package org.ayu.doyouknowback.domain.bus.service;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.bus.enums.BusEnums;
import org.ayu.doyouknowback.domain.bus.form.BusRequestDTO;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.global.cache.BusCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 버스 도착 정보를 관리하는 서비스 클래스
 * 1. 유저의 요청에는 서버 메모리(Cache)에 저장된 최신 데이터를 즉시 반환
 * 2. 스케줄러 등을 통해 주기적으로 외부 공공데이터 API를 호출하여 캐시를 갱신하는 구조
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusService {

    // 인메모리 캐시 저장소 (ConcurrentHashMap 등을 활용한 로컬 캐시) 추후 요청이 많아지거나 2대로 서버를 나눌 시 redis 사용
    private final BusCache cache;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 캐시 갱신 시간을 표시하기 위한 한국 표준시간
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter KST_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${bus.key}")
    private String serviceKey;

    /*
      캐시에 저장된 버스 도착 정보를 조회합니다.
      외부 API를 직접 호출하지 않으므로 매우 빠른 응답 속도를 보장.
      @param request 유저가 보낸 방향 정보 (ANYtoAYU 등)
      @return 캐시된 버스 정보 또는 데이터 없음 에러 메시지
     */
    public BusResponseDTO getArrival(BusRequestDTO request) {
        BusEnums dir;
        try {
            // 요청받은 문자열을 Enum 상수로 변환 (ANYtoAYU / AYUtoANY)
            dir = BusEnums.valueOf(request.direction());
        } catch (Exception e) {
            return BusResponseDTO.builder()
                    .success(false)
                    .message("direction 값이 올바르지 않습니다. (ANYtoAYU / AYUtoANY)")
                    .build();
        }

        // 1. 캐시에서 해당 방향의 데이터(Entry)를 가져옴
        BusCache.Entry entry = cache.get(dir);

        // 2. 캐시 데이터가 없으면(서버 가동 직후 등) 실패 응답 반환
        if (entry == null) {
            return BusResponseDTO.builder()
                    .stationName(dir.stationName())
                    .success(false)
                    .message("아직 데이터가 갱신되지 않았습니다. 잠시 후 다시 시도해주세요.")
                    .lastUpdatedKst(null)
                    .build();
        }

        // 3. 캐시 갱신 시간을 유저가 보기 편한 한국 시간 포맷으로 변환
        String updatedKst = "최근 갱신 시간 : " + Instant.ofEpochMilli(entry.updatedAtMillis())
                .atZone(KST)
                .format(KST_FMT);

        // 4. 캐시된 DTO에 시간 정보를 추가하여 최종 반환
        return entry.data().toBuilder()
                .lastUpdatedKst(updatedKst)
                .build();
    }

    /**
     * 리스트 형태의 다중 요청 처리 메서드
     */
    public List<BusResponseDTO> getArrivals(List<BusRequestDTO> requests) {
        List<BusResponseDTO> results = new ArrayList<>();
        for (BusRequestDTO req : requests) {
            results.add(getArrival(req));
        }
        return results;
    }

    /**
     * [배치/폴링용 로직]
     * 외부 경기버스 공공데이터 API를 실제로 호출하여 최신 데이터를 가져옵니다.
     * 동기 방식을 사용.
     * * @param dir 조회할 버스 노선 및 정류장 정보
     * @return API로부터 받아온 가공된 버스 정보 DTO
     */
    public BusResponseDTO fetchFromPublicApi(BusEnums dir) {
        HttpURLConnection conn = null;
        try {
            // 1. API 요청 URL 생성
            String urlString = buildUrl(dir.stationId(), dir.routeId(), dir.staOrder());
            conn = (HttpURLConnection) new URL(urlString).openConnection();

            // 2. 연결 설정 (Timeout 3초 설정으로 무한 대기 방지)
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            // 3. 응답 코드 확인
            BufferedReader rd = (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300)
                    ? new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))
                    : new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) sb.append(line);
            rd.close();

            // 4. JSON 파싱 시작 (Jackson Tree Model 사용)
            JsonNode root = objectMapper.readTree(sb.toString());
            JsonNode item = root.path("response").path("msgBody").path("busArrivalItem");

            // 5. 데이터가 없는 경우 예외 처리 (해당 노선에 운행 중인 버스가 없을 때 등)
            if (item.isMissingNode() || item.isNull() || item.size() == 0) {
                return BusResponseDTO.builder()
                        .stationName(dir.stationName())
                        .success(false)
                        .message("도착 정보 없음")
                        .build();
            }

            // 6. 배열 응답인 경우 첫 번째 항목(가장 빨리 오는 차)을 선택
            JsonNode target = item.isArray() ? item.get(0) : item;

            // 필요한 데이터 추출
            String locationNo = target.path("locationNo1").asText("");     // 몇 정거장 전
            String predictTime = target.path("predictTime1").asText("");   // 남은 시간 (분)
            String formattedTimeSec = target.path("predictTimeSec1").asText(""); // 남은 시간 (초)
            String updatedKst = Instant.now().atZone(KST).format(KST_FMT);
            // 7. 성공 응답 생성
            log.info("[BUS-API] 성공 | 정거장: {} | 위치: {}전 | 남은시간: {}분({}초) | 갱신: {}",
                    dir.stationName(), locationNo, predictTime, formattedTimeSec, updatedKst);
            return BusResponseDTO.builder()
                    .stationName(dir.stationName())
                    .locationNo(locationNo)
                    .predictTime(predictTime)
                    .formattedTimeSec(formattedTimeSec)
                    .success(true)
                    .message("OK")
                    .build();

        } catch (Exception e) {
            // 통신 장애, 파싱 에러 발생 시 처리
            return BusResponseDTO.builder()
                    .stationName(dir.stationName())
                    .success(false)
                    .message("API 호출/파싱 실패: " + e.getMessage())
                    .build();
        } finally {
            if (conn != null) conn.disconnect(); // 자원 해제
        }
    }

    /*
      공공데이터 API 호출을 위한 쿼리 파라미터 조합 메서드
     */
    private String buildUrl(String stationId, String routeId, String staOrder) throws Exception {
        // 서비스 키가 이미 인코딩되어 있는지 확인 후 처리
        String encodedKey = serviceKey.contains("%")
                ? serviceKey
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