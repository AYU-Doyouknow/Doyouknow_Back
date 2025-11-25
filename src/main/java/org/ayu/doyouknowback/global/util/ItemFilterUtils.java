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

    // ==================== ID 추출 메서드 ====================

    /**
     * Lost 엔티티 리스트에서 ID만 추출
     * 
     * @param items Lost 엔티티 리스트
     * @return ID 리스트
     * 
     * 사용 예시:
     * List<Long> ids = ItemFilterUtils.extractIdsFromLost(lostList);
     */
    public static List<Long> extractIdsFromLost(List<Lost> items) {
        List<Long> ids = new ArrayList<>();
        for (Lost item : items) {
            ids.add(item.getId());
        }
        return ids;
    }

    /**
     * News 엔티티 리스트에서 ID만 추출
     * 
     * @param items News 엔티티 리스트
     * @return ID 리스트
     * 
     * 사용 예시:
     * List<Long> ids = ItemFilterUtils.extractIdsFromNews(newsList);
     */
    public static List<Long> extractIdsFromNews(List<News> items) {
        List<Long> ids = new ArrayList<>();
        for (News item : items) {
            ids.add(item.getId());
        }
        return ids;
    }

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

    // ==================== 신규 항목 필터링 메서드 ====================

    /**
     * LostRequestDTO 리스트에서 기존 ID에 없는 신규 항목만 필터링
     * 
     * @param allItems 전체 LostRequestDTO 리스트
     * @param existingIds DB에 이미 존재하는 ID 리스트
     * @return 신규 LostRequestDTO 리스트
     * 
     * 사용 예시:
     * List<LostRequestDTO> newItems = ItemFilterUtils.filterNewLostItems(allItems, dbIds);
     */
    public static List<LostRequestDTO> filterNewLostItems(List<LostRequestDTO> allItems, List<Long> existingIds) {
        List<LostRequestDTO> newItems = new ArrayList<>();
        for (LostRequestDTO item : allItems) {
            if (!existingIds.contains(item.getId())) {
                newItems.add(item);
            }
        }
        return newItems;
    }

    /**
     * NewsRequestDTO 리스트에서 기존 ID에 없는 신규 항목만 필터링
     * 
     * @param allItems 전체 NewsRequestDTO 리스트
     * @param existingIds DB에 이미 존재하는 ID 리스트
     * @return 신규 NewsRequestDTO 리스트
     * 
     * 사용 예시:
     * List<NewsRequestDTO> newItems = ItemFilterUtils.filterNewNewsItems(allItems, dbIds);
     */
    public static List<NewsRequestDTO> filterNewNewsItems(List<NewsRequestDTO> allItems, List<Long> existingIds) {
        List<NewsRequestDTO> newItems = new ArrayList<>();
        for (NewsRequestDTO item : allItems) {
            if (!existingIds.contains(item.getId())) {
                newItems.add(item);
            }
        }
        return newItems;
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

    // ==================== 최신 항목 선택 메서드 ====================

    /**
     * LostRequestDTO 리스트에서 ID가 가장 큰 (최신) 항목을 찾음
     * 
     * @param items LostRequestDTO 리스트
     * @return ID가 가장 큰 LostRequestDTO, 리스트가 비어있으면 null
     * 
     * 사용 예시:
     * LostRequestDTO latest = ItemFilterUtils.findLatestLostItem(newLostItems);
     */
    public static LostRequestDTO findLatestLostItem(List<LostRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        LostRequestDTO latest = items.get(0);
        for (LostRequestDTO item : items) {
            if (item.getId() > latest.getId()) {
                latest = item;
            }
        }
        return latest;
    }

    /**
     * NewsRequestDTO 리스트에서 ID가 가장 큰 (최신) 항목을 찾음
     * 
     * @param items NewsRequestDTO 리스트
     * @return ID가 가장 큰 NewsRequestDTO, 리스트가 비어있으면 null
     * 
     * 사용 예시:
     * NewsRequestDTO latest = ItemFilterUtils.findLatestNewsItem(newNewsItems);
     */
    public static NewsRequestDTO findLatestNewsItem(List<NewsRequestDTO> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        NewsRequestDTO latest = items.get(0);
        for (NewsRequestDTO item : items) {
            if (item.getId() > latest.getId()) {
                latest = item;
            }
        }
        return latest;
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
