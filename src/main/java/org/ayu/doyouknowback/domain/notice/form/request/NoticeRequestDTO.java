package org.ayu.doyouknowback.domain.notice.form.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDTO {
    private Long id;
    private String noticeTitle; // 제목
    private String noticeWriter; // 작성자
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private String noticeBody; // 게시글 내용
    private String noticeDownloadLink; //다운로드 링크
    private String noticeDownloadTitle; //다운로드 제목

}
