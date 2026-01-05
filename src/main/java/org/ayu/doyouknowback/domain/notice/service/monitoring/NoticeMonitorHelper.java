package org.ayu.doyouknowback.domain.notice.service.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.FcmService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeMonitorHelper {

    private final NoticeRepository noticeRepository;
    private final FcmService fcmService;

    @Monitored("DB_READ")
    public List<Notice> findTop5Notice() {
        List<Notice> latestNotice = noticeRepository.findTop5ByOrderByIdDesc();

        log.info("========DB에서 불러온 최근 5개의 공지사항========");
        for (Notice notice : latestNotice) {
            log.info("id : {}, title : {}", notice.getId(), notice.getNoticeTitle());
        }

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
            fcmService.sendNotificationAsync(
                    "이거아냥?",
                    singleNotice.createNotificationTitle(),
                    singleNotice.createDetailUrl());
        } else {
            Notice latestNotice = newNoticeList.get(0);
            fcmService.sendNotificationAsync(
                    "이거아냥?",
                    latestNotice.createMultipleNoticesNotificationBody(count),
                    Notice.getNoticeListUrl());
        }

        log.info("푸시 알림 비동기 큐잉 완료 (백그라운드 실행 중)");
    }
}
