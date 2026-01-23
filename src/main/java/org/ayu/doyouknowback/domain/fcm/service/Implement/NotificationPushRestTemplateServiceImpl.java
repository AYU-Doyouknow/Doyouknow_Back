package org.ayu.doyouknowback.domain.fcm.service.Implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.domain.Notification;
import org.ayu.doyouknowback.domain.fcm.repository.NotificationRepository;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("restTemplatePushService")
@RequiredArgsConstructor
public class NotificationPushRestTemplateServiceImpl implements NotificationPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private static final int BATCH_SIZE = 100;

    private final NotificationRepository fcmRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @Async("pushNotificationExecutor")
    @Transactional
    public CompletableFuture<Void> sendNotificationAsync(String title, String body, String url) {
        long startTime = System.currentTimeMillis();
        log.info("비동기 푸시 알림 시작 (RestTemplate) - Thread: {}", Thread.currentThread().getName());

        // 1. 모든 토큰 조회
        List<Notification> allTokens = fcmRepository.findAll();
        if (allTokens.isEmpty()) {
            log.warn("토큰이 없습니다.");
            return CompletableFuture.completedFuture(null);
        }

        // 2. 메시지 생성
        List<Map<String, Object>> messages = createMessages(allTokens, title, body, url);
        if (messages.isEmpty()) {
            log.warn("유효한 토큰이 없습니다.");
            return CompletableFuture.completedFuture(null);
        }

        // 3. 푸시 전송
        sendMessages(messages);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("비동기 푸시 알림 완료 (RestTemplate) - Thread: {}, 실행 시간: {}ms", Thread.currentThread().getName(), executionTime);

        return CompletableFuture.completedFuture(null);
    }

    private List<Map<String, Object>> createMessages(List<Notification> tokens, String title, String body, String url) {
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Notification fcm : tokens) {
            if (fcm.isValid()) {
                messages.add(fcm.createPushMessage(title, body, url));
            }
        }
        return messages;
    }

    private void sendMessages(List<Map<String, Object>> messages) {
        int totalCount = messages.size();
        int successCount = 0;

        HttpHeaders headers = createHeaders();

        try {
            for (int i = 0; i < messages.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, messages.size());
                List<Map<String, Object>> batch = messages.subList(i, end);

                sendBatch(batch, headers);
            }

            log.info("Expo Push 전송 완료 - Total: {}, Success: {}, Fail: {}", totalCount, successCount, totalCount - successCount);

        } catch (Exception e) {
            log.error("Expo Push 전송 실패", e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.ALL));
        return headers;
    }

    private void sendBatch(List<Map<String, Object>> batch, HttpHeaders headers) throws Exception {
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(batch), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, String.class);

        parseResponse(response.getBody());
    }

    private void parseResponse(String responseBody) {
        try {
            Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);
            List<Map<String, Object>> responseData = (List<Map<String, Object>>) resultMap.get("data");

            int successCount = 0;
            int failureCount = 0;

            if (responseData != null) {
                for (Map<String, Object> messageResponse : responseData) {
                    String status = (String) messageResponse.get("status");

                    if ("ok".equalsIgnoreCase(status)) {
                        successCount++;
                    } else {
                        failureCount++;
                        log.warn("메시지 실패 - status: {}, details: {}", status, messageResponse.get("details"));
                    }
                }
            }

            log.info("파싱 결과 - 성공: {}, 실패: {}", successCount, failureCount);

        } catch (JsonProcessingException e) {
            log.error("응답 파싱 실패: {}", e.getMessage());
        }
    }
}
