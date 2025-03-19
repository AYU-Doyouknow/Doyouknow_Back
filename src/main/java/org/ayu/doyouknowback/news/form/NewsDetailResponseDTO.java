package org.ayu.doyouknowback.news.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class NewsDetailResponseDTO {
    private Long id;
    private String newsTitle;// 타이틀
    private String newsDormitory; //작성자
    private String newsLink; // *첨부파일? 링크?*
    private String newsDate; //게시일
    private int newsViews; // 조회수
    private int newsBody;

}
