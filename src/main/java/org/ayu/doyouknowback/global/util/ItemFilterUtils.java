package org.ayu.doyouknowback.global.util;

import org.ayu.doyouknowback.domain.lost.domain.Lost;
import org.ayu.doyouknowback.domain.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.domain.news.domain.News;
import org.ayu.doyouknowback.domain.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * 신규 항목 필터링 및 최신 항목 선택 유틸리티 클래스
 * 크롤링된 데이터에서 DB에 없는 신규 항목만 추출하는 로직을 재사용
 */
public class ItemFilterUtils {

    /**
     * Notice 엔티티 리스트에서 ID만 추출
     * 
     * @param items Notice 엔티티 리스트
     * @return ID 리스트
     * 
     * 사용 예시:
     * List<Long> ids = ItemFilterUtils.extractIdsFromNotice(noticeList);
     */
    public static List<Long> extractIdsFromNotice(List<Notice> items) {
        List<Long> ids = new ArrayList<>();
        for (Notice item : items) {
            ids.add(item.getId());
        }
        return ids;
    }

    /**
     * NoticeRequestDTO 리스트에서 기존 ID에 없는 신규 항목만 필터링
     * 
     * @param allItems 전체 NoticeRequestDTO 리스트
     * @param existingIds DB에 이미 존재하는 ID 리스트
     * @return 신규 NoticeRequestDTO 리스트
     * 
     * 사용 예시:
     * List<NoticeRequestDTO> newItems = ItemFilterUtils.filterNewNoticeItems(allItems, dbIds);
     */
    public static List<NoticeRequestDTO> filterNewNoticeItems(List<NoticeRequestDTO> allItems, List<Long> existingIds) {
        List<NoticeRequestDTO> newItems = new ArrayList<>();
        for (NoticeRequestDTO item : allItems) {
            if (!existingIds.contains(item.getId())) {
                newItems.add(item);
            }
        }
        return newItems;
    }


    /**
     * NoticeRequestDTO 리스트에서 ID가 가장 큰 (최신) 항목을 찾는다.
     * 
     * @param items NoticeRequestDTO 리스트
     * @return ID가 가장 큰 NoticeRequestDTO, 리스트가 비어있으면 null
     * 
     * 사용 예시:
     * NoticeRequestDTO latest = ItemFilterUtils.findLatestNoticeItem(newNoticeItems);
     */
    public static NoticeRequestDTO findLatestNoticeItem(List<NoticeRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        NoticeRequestDTO latest = items.get(0);
        for (NoticeRequestDTO item : items) {
            if (item.getId() > latest.getId()) {
                latest = item;
            }
        }
        return latest;
    }
}
