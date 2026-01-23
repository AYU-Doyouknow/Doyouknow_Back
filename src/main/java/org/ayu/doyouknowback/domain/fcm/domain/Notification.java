package org.ayu.doyouknowback.domain.fcm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.fcm.form.NotificationTokenRequestDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Token token;

    private String platform;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // DTO -> Entity 변환
    public static Notification toSaveEntity(NotificationTokenRequestDTO fcmTokenRequestDTO) {
        return Notification.builder()
                .token(Token.of(fcmTokenRequestDTO.getToken()))
                .platform(fcmTokenRequestDTO.getPlatform())
                .build();
    }

    // 토큰 유효성 검증
    public boolean isValid() {
        return token != null && token.isValid();
    }

    // 푸시 메시지 생성
    public Map<String, Object> createPushMessage(String title, String body, String url) {
        Map<String, Object> message = new HashMap<>();
        message.put("to", token.getValue());
        message.put("title", title);
        message.put("body", body);
        message.put("sound", "default");

        if (url != null && !url.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("url", url);
            message.put("data", data);
        }

        return message;
    }
}
