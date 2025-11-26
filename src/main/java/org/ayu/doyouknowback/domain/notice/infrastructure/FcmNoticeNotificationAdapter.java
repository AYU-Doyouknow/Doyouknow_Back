package org.ayu.doyouknowback.domain.notice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.ayu.doyouknowback.domain.notice.application.port.NoticeNotificationPort;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;
import org.ayu.doyouknowback.global.util.NotificationMessageUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmNoticeNotificationAdapter implements NoticeNotificationPort {

    private final FcmService fcmService;

    @Override
    public void notifyNewNotices(List<NoticeRequestDTO> newNotices) {
        String[] message = NotificationMessageUtils.buildNoticeNotificationMessage(newNotices);
        if (message != null) {
            fcmService.sendNotificationToAllExpo(message[0], message[1]);
        }
    }
}
