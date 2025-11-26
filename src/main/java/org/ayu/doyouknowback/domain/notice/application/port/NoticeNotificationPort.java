package org.ayu.doyouknowback.domain.notice.application.port;

import org.ayu.doyouknowback.domain.notice.form.request.NoticeRequestDTO;

import java.util.List;

/**
 * 공지 알림 발송을 위한 포트(추상화)
 * 구현체는 FCM, 이메일 등 어떤 수단이든 가능하다.
 */
public interface NoticeNotificationPort {

    /**
     * 새로 등록된 공지 목록에 대해 알림을 발송한다.
     */
    void notifyNewNotices(List<NoticeRequestDTO> newNotices);
}
