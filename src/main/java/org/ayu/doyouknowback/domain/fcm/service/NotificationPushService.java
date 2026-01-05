package org.ayu.doyouknowback.domain.fcm.service;

import java.util.concurrent.CompletableFuture;

// 푸시 알림 전송 서비스
public interface NotificationPushService {

    CompletableFuture<Void> sendNotificationAsync(String title, String body, String url);
}
