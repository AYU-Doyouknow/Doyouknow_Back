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
        Optional<Notification> optionalFcm = fcmRepository.findByToken(dto.getToken());

        if (optionalFcm.isPresent()) {
            log.info("토큰이 이미 존재합니다: {}", dto.getToken());
            return;
        }

        Notification fcm = Notification.builder()
                .token(Token.of(dto.getToken()))
                .platform(dto.getPlatform())
                .build();

        fcmRepository.save(fcm);
        log.info("새 토큰 저장 완료: {} (platform: {})", dto.getToken(), dto.getPlatform());
    }
}
