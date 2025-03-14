package org.ayu.doyouknowback.notice.form;

import lombok.*;
import org.ayu.doyouknowback.notice.domain.Notice;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeResponseDTO {

    private Long id;
    private String noticeTitle; // 제목
    private String noticeDormitory; // 작성자
    private String noticeLink; // 링크?
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private int noticeViews; // 게시글 조회수

    public static NoticeResponseDTO toDTO(Notice notice){
        // Entity -> Dto
        return NoticeResponseDTO.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeDormitory(notice.getNoticeDormitory())
                .noticeLink(notice.getNoticeLink())
                .noticeDate(notice.getNoticeDate())
                .noticeCategory(notice.getNoticeCategory())
                .noticeViews(notice.getNoticeViews())
                .build();
    }

}
