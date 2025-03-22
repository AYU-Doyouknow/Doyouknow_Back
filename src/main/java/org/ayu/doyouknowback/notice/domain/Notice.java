package org.ayu.doyouknowback.notice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.notice.form.NoticeRequestDTO;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String noticeTitle; // 제목
    private String noticeDormitory; // 작성자
    private String noticeLink; // 링크?
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private int noticeViews; // 게시글 조회수

    @Column(columnDefinition = "TEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String noticeBody; // 게시글 내용

    public static Notice toSaveEntity(NoticeRequestDTO noticeRequestDTO){
        // DTO -> Entity
        return Notice.builder()
                .noticeTitle(noticeRequestDTO.getNoticeTitle())
                .noticeDormitory(noticeRequestDTO.getNoticeDormitory())
                .noticeLink(noticeRequestDTO.getNoticeLink())
                .noticeDate(noticeRequestDTO.getNoticeDate())
                .noticeCategory(noticeRequestDTO.getNoticeCategory())
                .noticeViews(noticeRequestDTO.getNoticeViews())
                .noticeBody(noticeRequestDTO.getNoticeBody())
                .build();
    }
}
