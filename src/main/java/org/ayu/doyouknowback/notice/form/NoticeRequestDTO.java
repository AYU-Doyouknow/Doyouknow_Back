package org.ayu.doyouknowback.notice.form;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeRequestDTO {

    private String noticeTitle; // 제목
    private String noticeDormitory; // 작성자
    private String noticeLink; // 링크?
    private String noticeDate; // 게시글 생성 날짜
    private String noticeCategory; // 게시글 카테고리
    private int noticeViews; // 게시글 조회수
    private String noticeBody; // 게시글 내용

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
