package org.ayu.doyouknowback.domain.fcm.service.Implement;

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
public class NotificationPushServiceImpl implements NotificationPushService {

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
        log.info("비동기 푸시 알림 시작 - Thread: {}", Thread.currentThread().getName());

        // 1. 토큰 조회
        List<Notification> allTokens = fcmRepository.findAll();
        if (allTokens.isEmpty()) {
            log.warn("토큰 리스트가 비어 있습니다.");
            return CompletableFuture.completedFuture(null);
        }

        // 2. 메시지 생성
        List<Map<String, Object>> messages = createMessages(allTokens, title, body, url);
        if (messages.isEmpty()) {
            log.warn("유효한 토큰이 없습니다.");
            return CompletableFuture.completedFuture(null);
        }

        // 3. 푸시 전송
        List<String> failedTokens = sendMessages(messages);

        // 4. 실패 토큰 삭제
        if (!failedTokens.isEmpty()) {
            deleteFailedTokens(failedTokens);
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("비동기 푸시 알림 완료 - Thread: {}, 실행 시간: {}ms", Thread.currentThread().getName(), executionTime);

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

    private List<String> sendMessages(List<Map<String, Object>> messages) {
        int totalCount = messages.size();
        int successCount = 0;
        List<String> failedTokens = new ArrayList<>();

        try {
            HttpHeaders headers = createHeaders();

            for (int i = 0; i < messages.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, messages.size());
                List<Map<String, Object>> batch = messages.subList(i, end);

                BatchResult batchResult = sendBatch(batch, headers);
                successCount += batchResult.successCount;
                failedTokens.addAll(batchResult.failedTokens);
            }

            log.info("Expo Push 전송 완료 - Total: {}, Success: {}, Fail: {}",
                    totalCount, successCount, totalCount - successCount);

        } catch (Exception e) {
            log.error("Expo Push 전송 실패", e);
        }

        return failedTokens;
    }

    private void deleteFailedTokens(List<String> failedTokens) {
        for (String token : failedTokens) {
            fcmRepository.deleteByToken(token);
            log.info("만료 토큰 삭제: {}", token);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.ALL));
        return headers;
    }

    private BatchResult sendBatch(List<Map<String, Object>> batch, HttpHeaders headers) throws Exception {
        HttpEntity<String> request = new HttpEntity<>(
                objectMapper.writeValueAsString(batch),
                headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                EXPO_PUSH_URL,
                request,
                String.class);

        return parseResponse(response.getBody());
    }

    private BatchResult parseResponse(String responseBody) throws Exception {
        Map<String, Object> resultMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> responseData = (List<Map<String, Object>>) resultMap.get("data");

        int successCount = 0;
        List<String> failedTokens = new ArrayList<>();

        for (Map<String, Object> messageResponse : responseData) {
            String status = (String) messageResponse.get("status");

            if ("ok".equalsIgnoreCase(status)) {
                successCount++;
            } else {
                Map<String, Object> details = (Map<String, Object>) messageResponse.get("details");
                if (details != null && details.containsKey("expoPushToken")) {
                    failedTokens.add((String) details.get("expoPushToken"));
                }
            }
        }

        return new BatchResult(successCount, failedTokens);
    }

    private static class BatchResult {
        int successCount;
        List<String> failedTokens;

        BatchResult(int successCount, List<String> failedTokens) {
            this.successCount = successCount;
            this.failedTokens = failedTokens;
        }
    }
}
