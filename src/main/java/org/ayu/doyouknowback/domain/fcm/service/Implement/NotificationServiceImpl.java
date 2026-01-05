package org.ayu.doyouknowback.domain.fcm.service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.domain.Notification;
import org.ayu.doyouknowback.domain.fcm.domain.Token;
import org.ayu.doyouknowback.domain.fcm.form.NotificationTokenRequestDTO;
import org.ayu.doyouknowback.domain.fcm.repository.NotificationRepository;
import org.ayu.doyouknowback.domain.fcm.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository fcmRepository;

    @Override
    @Transactional
    public void saveToken(NotificationTokenRequestDTO dto) {
        String tokenValue = dto.getToken();
        Optional<Notification> existing = fcmRepository.findByToken(tokenValue);

        if (existing.isPresent()) {
            log.info("토큰 이미 존재: {}", dto.getToken());
            return;
        }

        Notification newNotification = Notification.builder()
                .token(Token.of(dto.getToken()))
                .platform(dto.getPlatform())
                .build();

        fcmRepository.save(newNotification);
        log.info("신규 토큰 저장: {} (platform: {})", dto.getToken(), dto.getPlatform());
    }
}
