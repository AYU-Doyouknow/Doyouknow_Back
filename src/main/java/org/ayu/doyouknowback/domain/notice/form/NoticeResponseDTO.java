package org.ayu.doyouknowback.domain.notice.form;

import lombok.*;
import org.ayu.doyouknowback.domain.notice.domain.Notice;
import org.ayu.doyouknowback.domain.notice.repository.projection.NoticeSummaryView;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDTO {

    private Long id;
    private String noticeTitle; // 제목
    private String noticeWriter; // 작성자
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리

    public static NoticeResponseDTO toDTO(Notice notice){
        // Entity -> Dto
        return NoticeResponseDTO.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeWriter(notice.getNoticeWriter())
                .noticeDate(notice.getNoticeDate())
                .noticeCategory(notice.getNoticeCategory())
                .build();
    }

    public static NoticeResponseDTO projection(NoticeSummaryView row) {
        // Projection -> Dto
        return NoticeResponseDTO.builder()
                .id(row.getId())
                .noticeTitle(row.getNoticeTitle())
                .noticeWriter(row.getNoticeWriter())
                .noticeDate(row.getNoticeDate())
                .noticeCategory(row.getNoticeCategory())
                .build();
    }

}
