package org.ayu.doyouknowback.domain.notice.service;

import lombok.extern.slf4j.Slf4j;
import org.ayu.doyouknowback.domain.fcm.service.NotificationPushService;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.NoticeRepository;
import org.ayu.doyouknowback.domain.notice.repository.projection.NoticeSummaryView;
import org.ayu.doyouknowback.global.monitoring.Monitored;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
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

    // 검색 쿼리(DB)만 측정
    @Monitored("DB_SEARCH")
    public Page<Notice> searchByTitleOrBody(String keyword, Pageable pageable) {
        Page<Notice> page = noticeRepository
                .findByNoticeTitleContainingOrNoticeBodyContaining(keyword, keyword, pageable);

        log.info("[DB_SEARCH] keyword='{}', returned={} / total={}",
                keyword, page.getNumberOfElements(), page.getTotalElements());

        return page;
    }

    // 검색 쿼리(DB)만 측정
    @Monitored("DB_FULLTEXT_SEARCH")
    public Page<Notice> fullTextSearchByTitleOrBody(String keyword, Pageable pageable) {

        /**
         * (선택) 1글자 검색 정책
         *          ngram_token_size=2면 keyword 길이가 1일 때 매칭이 거의 안 나올 수 있음.
         *          정책1) 1글자면 예외/빈 결과
         *          정책2) 1글자면 LIKE로 fallback(성능은 포기)
         *          여기선 정책을 명확히 하기 위해 최소 길이 제한 예시를 둠.
         * **/
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        String q = keyword.trim();

        Page<Notice> page = noticeRepository.searchByFullText(q, pageable);

        log.info("[DB_FULLTEXT_SEARCH] keyword='{}', returned={} / total={}",
                keyword, page.getNumberOfElements(), page.getTotalElements());

        return page;
    }

    // Projection 적용 FullText Search
    @Monitored("DB_FULLTEXT_SEARCH")
    public Page<NoticeSummaryView> fullTextSearchSummaryByTitleOrBody(String keyword, Pageable pageable) {

        /**
         *  1글자 검색 정책
         *          ngram_token_size=2면 keyword 길이가 1일 때 매칭이 거의 안 나올 수 있음.
         *          정책1) 1글자면 예외/빈 결과
         *          정책2) 1글자면 LIKE로 fallback(성능은 포기)
         *          여기선 정책을 명확히 하기 위해 최소 길이 제한 예시를 둠.
         * **/
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        String q = keyword.trim();

        Page<NoticeSummaryView> page = noticeRepository.searchByFullTextSummary(q, pageable);

        log.info("[DB_FULLTEXT_SEARCH] keyword='{}', returned={} / total={}",
                keyword, page.getNumberOfElements(), page.getTotalElements());

        return page;
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

