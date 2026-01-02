package org.ayu.doyouknowback.domain.notice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice {

    @Id
    private Long id;

    private String noticeTitle; // 제목
    private String noticeWriter; // 작성자
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    @Column(columnDefinition = "LONGTEXT")
    private String noticeDownloadLink; // 다운로드 링크
    @Column(columnDefinition = "LONGTEXT")
    private String noticeDownloadTitle; // 다운로드 제목
    @Column(columnDefinition = "LONGTEXT")
    private String noticeBody; // 게시글 내용
    @Column(columnDefinition = "LONGTEXT")
    private String noticeUrl; // 게시글 원본 링크

    // DTO로부터 Entity 생성
    public static Notice from(NoticeRequestDTO dto) {
        return new Notice(
                dto.getId(),
                dto.getNoticeTitle(),
                dto.getNoticeWriter(),
                dto.getNoticeDate(),
                dto.getNoticeCategory(),
                dto.getNoticeDownloadLink(),
                dto.getNoticeDownloadTitle(),
                dto.getNoticeBody(),
                dto.getNoticeUrl());
    }

    // DTO 리스트를 Entity 리스트로 변환
    public static List<Notice> fromList(List<NoticeRequestDTO> dtoList) {
        List<Notice> noticeList = new ArrayList<>();
        for (NoticeRequestDTO dto : dtoList) {
            noticeList.add(Notice.from(dto));
        }
        return noticeList;
    }

    // 이 공지가 기존 공지 목록에 없는 새로운 공지인지 비교
    public boolean isNewComparedTo(List<Notice> existingNotices) {
        for (Notice existing : existingNotices) {
            if (existing.id.equals(this.id)) {
                return false;
            }
        }
        return true;
    }

    // 기존 공지 목록과 비교하여 새로운 공지만 필터링
    public static List<Notice> filterNewNotices(List<Notice> candidates, List<Notice> existingNotices) {
        List<Notice> newNoticesList = new ArrayList<>();
        for (Notice candidate : candidates) {
            if (candidate.isNewComparedTo(existingNotices)) {
                newNoticesList.add(candidate);
            }
        }
        return newNoticesList;
    }

    // 단일 알림 제목 생성
    public String createNotificationTitle() {
        return "[공지사항] " + this.noticeTitle;
    }

    // 여러 공지 알림 메시지 생성
    public String createMultipleNoticesNotificationBody(int totalCount) {
        return this.createNotificationTitle() + " 외 " + (totalCount - 1) + "개";
    }

    // 상세 페이지 URL 생성
    public String createDetailUrl() {
        return "https://doyouknowayu.netlify.app/notice/detail/" + this.id;
    }

    // 공지사항 목록 페이지 URL
    public static String getNoticeListUrl() {
        return "https://doyouknowayu.netlify.app/notice";
    }
}
