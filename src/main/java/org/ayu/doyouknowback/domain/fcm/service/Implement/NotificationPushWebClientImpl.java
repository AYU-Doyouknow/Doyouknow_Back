package org.ayu.doyouknowback.domain.fcm.service.Implement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.domain.Notification;
import org.ayu.doyouknowback.domain.fcm.repository.NotificationRepository;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("webClientPushService")
@RequiredArgsConstructor
public class NotificationPushWebClientImpl implements NotificationPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private static final int BATCH_SIZE = 100;
    private static final int TIMEOUT_SECONDS = 10;

    private final NotificationRepository notificationRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Override
    @Async("pushNotificationExecutor")
    @Transactional
    public CompletableFuture<Void> sendNotificationAsync(String title, String body, String url) {

        long startTime = System.currentTimeMillis();

        log.info("비동기 푸시 알림 시작 (WebClient) - Thread: {}", Thread.currentThread().getName());

        // 1. 모든 토큰 조회
        List<Notification> allTokens = notificationRepository.findAll();
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

        // 3. 푸시 전송 (병렬 처리)
        sendMessagesParallel(messages);

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.info("비동기 푸시 알림 완료 (WebClient) - Thread: {}, 실행 시간: {}ms", Thread.currentThread().getName(), executionTime);

        return CompletableFuture.completedFuture(null);
    }

    private List<Map<String, Object>> createMessages(List<Notification> tokens, String title, String body, String url) {
        List<Map<String, Object>> messages = new ArrayList<>();
        for (Notification notification : tokens) {
            if (notification.isValid()) {
                messages.add(notification.createPushMessage(title, body, url));
            }
        }
        return messages;
    }

    private void sendMessagesParallel(List<Map<String, Object>> messages) {
        int totalCount = messages.size();
        int batchCount = (int) Math.ceil((double) messages.size() / BATCH_SIZE);

        // 동시 실행 수 설정
        int concurrency = 5;

        log.info("푸시 전송 설정 - 총 {}개, 배치 {}개, 동시 실행 수: {}", totalCount, batchCount, concurrency);

        List<Mono<Void>> batchRequests = new ArrayList<>();

        // 배치별로 Mono 생성
        for (int i = 0; i < messages.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, messages.size());
            List<Map<String, Object>> batch = messages.subList(i, end);

            int batchNumber = (i / BATCH_SIZE) + 1;
            Mono<Void> batchMono = sendBatchAsync(batch, batchNumber);
            batchRequests.add(batchMono);
        }

        long batchStartTime = System.currentTimeMillis();

        // 동시 실행 수 제한하여 병렬 실행
        Flux.fromIterable(batchRequests)
                .flatMap(mono -> mono, concurrency)
                .then()
                .block();

        long batchEndTime = System.currentTimeMillis();
        long batchExecutionTime = batchEndTime - batchStartTime;

        List<String> failedTokens = new ArrayList<>();

        log.info("Expo Push 전송 완료 (WebClient)");
        log.info("   - 배치 실행 시간: {}ms", batchExecutionTime);
        log.info("   - 평균 배치 시간: {}ms", batchExecutionTime / batchCount);
    }

    private Mono<Void> sendBatchAsync(List<Map<String, Object>> batch, int batchNumber) {
        long startTime = System.currentTimeMillis();

        return webClient.post()
                .uri(EXPO_PUSH_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(batch)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .doOnNext(response -> {
                    long endTime = System.currentTimeMillis();
                    // log.info("  배치 #{} 완료 - {}ms", batchNumber, endTime - startTime);
                    parseResponse(response);
                })
                .then()
                .onErrorResume(error -> {
//                    long endTime = System.currentTimeMillis();
//                    boolean isRateLimit = error.getMessage().contains("429");

//                    if (isRateLimit) {
//                        log.error("  배치 #{} 실패 - {}ms", batchNumber, endTime - startTime);
//                    } else {
//                        log.error("  배치 #{} 실패: {} - {}ms", batchNumber, error.getMessage(), endTime - startTime);
//                    }

                    return Mono.empty();
                });
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
                    }
                }
            }

            // log.info("파싱 결과 - 성공: {}, 실패: {}", successCount, failureCount);

        } catch (JsonProcessingException e) {
            log.error("응답 파싱 실패: {}", e.getMessage());
        }
    }
}
