package org.ayu.doyouknowback.domain.notice.form;

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
    private String noticeUrl;

//    @Builder
//    public NoticeRequestDTO(String noticeTitle, String noticeDormitory, String noticeLink,
//                            String noticeDate, String noticeCategory, int noticeViews, String noticeBody){
//
//        this.noticeTitle = noticeTitle;
//        this.noticeDormitory = noticeDormitory;
//        this.noticeLink = noticeLink;
//        this.noticeDate = noticeDate;
//        this.noticeCategory = noticeCategory;
//        this.noticeViews = noticeViews;
//        this.noticeBody = noticeBody;
//
//    }

//    // Entity 값 가져오기?
//    public static Notice toEntity(){
//        return Notice.builder()
//                .noticeTitle(noticeTitle)
//                .noticeDormitory(noticeDormitory)
//                .noticeLink(noticeLink)
//                .noticeDate(noticeDate)
//                .noticeCategory(noticeCategory)
//                .noticeViews(noticeViews)
//                .noticeBody(noticeBody)
//                .build();
//    }

}
