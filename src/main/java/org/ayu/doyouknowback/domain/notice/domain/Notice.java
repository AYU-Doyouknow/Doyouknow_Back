package org.ayu.doyouknowback.domain.notice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.notice.form.NoticeRequestDTO;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String noticeTitle; // 제목
    private String noticeWriter; // 작성자
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    @Column(columnDefinition = "LONGTEXT")
    private String noticeDownloadLink; //다운로드 링크
    @Column(columnDefinition = "LONGTEXT")
    private String noticeDownloadTitle; //다운로드 제목
    @Column(columnDefinition = "LONGTEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String noticeBody; // 게시글 내용

    public static Notice toSaveEntity(NoticeRequestDTO noticeRequestDTO){
        // DTO -> Entity
        return Notice.builder()
                .id(noticeRequestDTO.getId())
                .noticeTitle(noticeRequestDTO.getNoticeTitle())
                .noticeWriter(noticeRequestDTO.getNoticeWriter())
                .noticeDate(noticeRequestDTO.getNoticeDate())
                .noticeCategory(noticeRequestDTO.getNoticeCategory())
                .noticeBody(noticeRequestDTO.getNoticeBody())
                .noticeDownloadLink(noticeRequestDTO.getNoticeDownloadLink())
                .noticeDownloadTitle(noticeRequestDTO.getNoticeDownloadTitle())
                .build();
    }
}
