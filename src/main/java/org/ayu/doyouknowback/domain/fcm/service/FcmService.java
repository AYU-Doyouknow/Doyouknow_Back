package org.ayu.doyouknowback.domain.fcm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.domain.Fcm;
import org.ayu.doyouknowback.domain.fcm.form.FcmTokenRequestDTO;
import org.ayu.doyouknowback.domain.fcm.repository.FcmRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

    private final FcmRepository fcmRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Expo Push 알림 메서드 (URL 없이)
    public void sendNotificationToAllExpo(String title, String body) {
        sendNotificationToAllExpoWithUrl(title, body, null);
    }

    // Expo Push 알림 메서드 (URL 포함)
    @Transactional
    public void sendNotificationToAllExpoWithUrl(String title, String body, String url) {
        List<Fcm> tokens = fcmRepository.findAll();
        int totalCount = tokens.size();

        if (totalCount == 0) {
            System.out.println("FCM token list is empty. Push notification not sent.");
            return;
        }

        List<Map<String, Object>> messages = new ArrayList<>();
        for (Fcm fcm : tokens) {
            Map<String, Object> message = new HashMap<>();
            message.put("to", fcm.getToken());
            message.put("title", title);
            message.put("body", body);
            message.put("sound", "default");

            // URL이 있으면 data 필드 추가
            if (url != null && !url.isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("url", url);
                message.put("data", data);
            }

            messages.add(message);
        }

        int successCount = 0;
        int failCount = 0;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));

            for (int i = 0; i < messages.size(); i += 100) {
                int end = Math.min(i + 100, messages.size());
                List<Map<String, Object>> batch = messages.subList(i, end);

                HttpEntity<String> request = new HttpEntity<>(
                        objectMapper.writeValueAsString(batch),
                        headers
                );

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "https://exp.host/--/api/v2/push/send",
                        request,
                        String.class
                );

                Map<String, Object> resultMap = objectMapper.readValue(response.getBody(), Map.class);
                List<Map<String, Object>> responseData = (List<Map<String, Object>>) resultMap.get("data");

                for (Map<String, Object> messageResponse : responseData) {
                    String status = (String) messageResponse.get("status");
                    if ("ok".equalsIgnoreCase(status)) {
                        successCount++;
                    } else {
                        Map<String, Object> details = (Map<String, Object>) messageResponse.get("details");
                        if (details != null && details.containsKey("expoPushToken")) {
                            String failedToken = (String) details.get("expoPushToken");
                            failCount++;
                            fcmRepository.deleteByToken(failedToken);
                        }
                    }
                }
            }

            System.out.println("Push notification sent to " + totalCount + " users (success: " + successCount + ", fail: " + failCount + ")");

        } catch (Exception e) {
            System.out.println("Failed to send Expo push notification");
            e.printStackTrace();
        }
    }

    @Transactional
    public void saveToken(FcmTokenRequestDTO fcmTokenRequestDTO){
        Optional<Fcm> optionalFcm = fcmRepository.findByToken(fcmTokenRequestDTO.getToken());

        if(optionalFcm.isPresent()){
            return;
        }

        Fcm fcm = Fcm.toSaveEntity(fcmTokenRequestDTO);
        fcmRepository.save(fcm);
    }

}