package org.ayu.doyouknowback.notice.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.notice.domain.Notice;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeCategoryResponseDTO {

    private Long id;
    private String noticeTitle; // 제목
    private String noticeDormitory; // 작성자
    private String noticeLink; // 링크?
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private int noticeViews; // 게시글 조회수
    private String noticeBody; // 게시글 내용

    public static NoticeCategoryResponseDTO toDTO(Notice notice){
        // Entity -> Dto
        return NoticeCategoryResponseDTO.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeDormitory(notice.getNoticeDormitory())
                .noticeLink(notice.getNoticeLink())
                .noticeDate(notice.getNoticeDate())
                .noticeCategory(notice.getNoticeCategory())
                .noticeViews(notice.getNoticeViews())
                .noticeBody(notice.getNoticeBody())
                .build();
    }

}
