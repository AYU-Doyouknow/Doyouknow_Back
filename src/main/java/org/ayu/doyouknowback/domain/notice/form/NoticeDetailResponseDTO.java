package org.ayu.doyouknowback.domain.notice.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.notice.domain.Notice;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDetailResponseDTO {

    private Long id;
    private String noticeTitle; // 제목
    private String noticeWriter; // 작성자
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private String noticeBody; // 게시글 내용
    private String noticeDownloadLink; // 다운로드 링크
    private String noticeDownloadTitle; // 다운로드 제목
    private String noticeUrl; // 게시글 원본 링크

    public static NoticeDetailResponseDTO toDTO(Notice notice) {
        // Entity -> Dto
        return NoticeDetailResponseDTO.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeWriter(notice.getNoticeWriter())
                .noticeDate(notice.getNoticeDate())
                .noticeCategory(notice.getNoticeCategory())
                .noticeBody(notice.getNoticeBody())
                .noticeDownloadLink(notice.getNoticeDownloadLink())
                .noticeDownloadTitle(notice.getNoticeDownloadTitle())
                .noticeUrl(notice.getNoticeUrl())
                .build();
    }

}
