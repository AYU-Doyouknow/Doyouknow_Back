package org.ayu.doyouknowback.domain.notice.service;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NoticeMonitorHelper {

    private final NoticeRepository noticeRepository;
    private final NotificationPushService notificationPushService;

    public NoticeMonitorHelper(
            NoticeRepository noticeRepository,
            @Qualifier("webClientPushService") NotificationPushService notificationPushService) {
        this.noticeRepository = noticeRepository;
        this.notificationPushService = notificationPushService;
    }

    @Monitored("DB_READ")
    public List<Notice> findTop5Notice() {
        List<Notice> latestNotice = noticeRepository.findTop5ByOrderByIdDesc();
        log.info("[DB_READ] DB에서 최근 5개 공지사항 조회 - {}개", latestNotice.size());
        return latestNotice;
    }

    @Monitored("DB_WRITE")
    public void saveNotice(List<Notice> newNoticeList) {
        noticeRepository.saveAll(newNoticeList);
        log.info("공지사항 {}개 저장 완료", newNoticeList.size());
    }

    @Monitored("PUSH_NOTIFICATION")
    public void sendNotification(List<Notice> newNoticeList, int count) {
        if (count == 1) {
            Notice singleNotice = newNoticeList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    singleNotice.createNotificationTitle(),
                    singleNotice.createDetailUrl());
        } else {
            Notice latestNotice = newNoticeList.get(0);
            notificationPushService.sendNotificationAsync(
                    "이거아냥?",
                    latestNotice.createMultipleNoticesNotificationBody(count),
                    Notice.getNoticeListUrl());
        }

        log.info("푸시 알림 비동기 실행");
    }
}

