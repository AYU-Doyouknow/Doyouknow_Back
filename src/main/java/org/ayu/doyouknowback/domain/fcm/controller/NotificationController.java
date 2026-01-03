package org.ayu.doyouknowback.domain.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.domain.fcm.form.NotificationSendMessageReqeustDTO;
import org.ayu.doyouknowback.domain.fcm.form.NotificationTokenRequestDTO;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.fcm.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationPushService notificationPushService;

    @PostMapping("/saveToken")
    public ResponseEntity<String> saveToken(@RequestBody NotificationTokenRequestDTO fcmTokenRequestDTO) {
        notificationService.saveToken(fcmTokenRequestDTO);
        return ResponseEntity.ok("토큰 저장 완료");
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody NotificationSendMessageReqeustDTO fcmSendMessageReqeustDTO) {
        notificationPushService.sendNotificationAsync(fcmSendMessageReqeustDTO.getTitle(),
                fcmSendMessageReqeustDTO.getBody(), null);
        return ResponseEntity.ok("메세지 전송 완료");
    }
}
