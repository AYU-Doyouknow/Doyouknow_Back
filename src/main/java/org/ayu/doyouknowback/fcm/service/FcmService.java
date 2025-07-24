package org.ayu.doyouknowback.fcm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.fcm.domain.Fcm;
import org.ayu.doyouknowback.fcm.form.FcmTokenRequestDTO;
import org.ayu.doyouknowback.fcm.repository.FcmRepository;
import org.springframework.stereotype.Service;
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

//    public void sendNotificationToAllUser(String title, String body){
//        List<Fcm> allToken = fcmRepository.findAll();
//
//        for(Fcm fcm : allToken){
//            sendNotification(fcm.getToken(), title, body);
//        }
//    }
//
//    public void sendNotification(String token, String title, String body) {
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(body)
//                .build();
//
//        Message message = Message.builder()
//                .setToken(token)
//                .setNotification(notification)
//                .build();
//
//        try {
//            String response = FirebaseMessaging.getInstance().send(message);
//            System.out.println("Successfully sent message: " + response);
//        } catch (FirebaseMessagingException e){
//            // 없는 토큰 삭제
//            if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
//                fcmRepository.deleteByToken(token);
//            } else{
//                e.printStackTrace();
//            }
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    // Expo Push 알림 메서드
    public void sendNotificationToAllExpo(String title, String body) {
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

            messages.add(message);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.ALL));

            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(messages),
                    headers
            );

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://exp.host/--/api/v2/push/send",
                    request,
                    String.class
            );

            Map<String, Object> resultMap = objectMapper.readValue(response.getBody(), Map.class);
            List<Map<String, Object>> responseData = (List<Map<String, Object>>) resultMap.get("data");

            int successCount = 0;
            for (Map<String, Object> messageResponse : responseData) {
                String status = (String) messageResponse.get("status");
                if ("ok".equalsIgnoreCase(status)) {
                    successCount++;
                } else {
                    Map<String, Object> details = (Map<String, Object>) messageResponse.get("details");
                    if (details != null && details.containsKey("expoPushToken")) {
                        String failedToken = (String) details.get("expoPushToken");
                        System.out.println("🗑️ Deleting invalid token: " + failedToken);
                        fcmRepository.deleteByToken(failedToken);
                    }
                }
            }

            System.out.println("Push notification sent to " + totalCount + " users (success: " + successCount + ", fail: " + (totalCount - successCount) + ")");

        } catch (Exception e) {
            System.out.println("Failed to send Expo push notification");
            e.printStackTrace();
        }
    }




    public void saveToken(FcmTokenRequestDTO fcmTokenRequestDTO){
        Optional<Fcm> optionalFcm = fcmRepository.findByToken(fcmTokenRequestDTO.getToken());

        if(optionalFcm.isPresent()){
            return;
        }

        Fcm fcm = Fcm.toSaveEntity(fcmTokenRequestDTO);
        fcmRepository.save(fcm);
    }


}
