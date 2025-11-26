package org.ayu.doyouknowback.global.util;

import org.ayu.doyouknowback.domain.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.notice.form.request.NoticeRequestDTO;

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
            return new String[]{"이거아냥?", "[공지사항] " + title};
        } else {
            // 가장 ID가 큰 항목 찾기
            NoticeRequestDTO latest = ItemFilterUtils.findLatestNoticeItem(newItems);
            String title = latest.getNoticeTitle();
            return new String[]{"이거아냥?", "[공지사항] " + title + " 외 " + (count - 1) + "개"};
        }
    }

    /**
     * 학교소식 알림 메시지를 생성
     * - 1개: "이거아냥? [학교소식] {제목}"
     * - 여러 개: "이거아냥? [학교소식] {최신 제목} 외 {개수}개"
     * 
     * @param newItems 신규 학교소식 리스트
     * @return 알림 메시지 배열 [title, body], 항목이 없으면 null
     * 
     * 사용 예시:
     * String[] message = NotificationMessageUtils.buildNewsNotificationMessage(newNewsItems);
     * if (message != null) {
     *     fcmService.sendNotificationToAllExpo(message[0], message[1]);
     * }
     */
    public static String[] buildNewsNotificationMessage(List<NewsRequestDTO> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return null;
        }

        int count = newItems.size();
        
        if (count == 1) {
            String title = newItems.get(0).getNewsTitle();
            return new String[]{"이거아냥?", "[학교소식] " + title};
        } else {
            // 가장 ID가 큰 항목 찾기
            NewsRequestDTO latest = ItemFilterUtils.findLatestNewsItem(newItems);
            String title = latest.getNewsTitle();
            return new String[]{"이거아냥?", "[학교소식] " + title + " 외 " + (count - 1) + "개"};
        }
    }

    /**
     * 분실물 알림 메시지를 생성
     * - 1개: "[분실습득] {제목} 게시글이 등록되었습니다."
     * - 여러 개: "[분실습득] {최신 제목} 외 {개수}개 게시글이 등록되었습니다."
     *
     * @param newItems 신규 분실물 리스트
     * @return 알림 메시지 배열 [title, body], 항목이 없으면 null
     *
     * 사용 예시:
     * String[] message = NotificationMessageUtils.buildLostNotificationMessage(newLostItems);
     * if (message != null) {
     *     fcmService.sendNotificationToAllExpo(message[0], message[1]);
     * }
     */
    public static String[] buildLostNotificationMessage(List<LostRequestDTO> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            return null;
        }

        int count = newItems.size();

        if (count == 1) {
            String title = newItems.get(0).getLostTitle();
            return new String[]{"[분실습득]", title + " 게시글이 등록되었습니다."};
        } else {
            // 가장 ID가 큰 항목 찾기
            LostRequestDTO latest = ItemFilterUtils.findLatestLostItem(newItems);
            String title = latest.getLostTitle();
            return new String[]{"[분실습득]", title + " 외 " + (count - 1) + "개 게시글이 등록되었습니다."};
        }
    }
}
