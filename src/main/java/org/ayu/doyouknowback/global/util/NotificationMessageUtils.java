package org.ayu.doyouknowback.global.util;

import org.ayu.doyouknowback.domain.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;

import java.util.List;

/**
 * 푸시 알림 메시지 생성 유틸리티 클래스
 * 신규 항목 개수에 따라 적절한 알림 메시지를 생성
 */
public class NotificationMessageUtils {

    /**
     * 공지사항 알림 메시지를 생성
     * - 1개: "이거아냥? [공지사항] {제목}"
     * - 여러 개: "이거아냥? [공지사항] {최신 제목} 외 {개수}개"
     *
     * @param newItems 신규 공지사항 리스트
     * @return 알림 메시지 배열 [title, body], 항목이 없으면 null
     *
     * 사용 예시:
     * String[] message = NotificationMessageUtils.buildNoticeNotificationMessage(newNoticeItems);
     * if (message != null) {
     *     fcmService.sendNotificationToAllExpo(message[0], message[1]);
     * }
     */
    public static String[] buildNoticeNotificationMessage(List<NoticeRequestDTO> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return null;
        }

        int count = newItems.size();

        if (count == 1) {
            String title = newItems.get(0).getNoticeTitle();
            return new String[]{"이거아냥?", "[공지사항] " + title, "https://doyouknowayu.netlify.app/notice"};
        } else {
            // 가장 ID가 큰 항목 찾기
            NoticeRequestDTO latest = ItemFilterUtils.findLatestNoticeItem(newItems);
            String title = latest.getNoticeTitle();
            return new String[]{"이거아냥?", "[공지사항] " + title + " 외 " + (count - 1) + "개", "https://doyouknowayu.netlify.app/notice/detail/" + newItems.get(0).getId()};
        }
    }
}
