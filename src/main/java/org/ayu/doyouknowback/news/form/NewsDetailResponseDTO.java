package org.ayu.doyouknowback.news.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.news.domain.News;

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
    private String newsBody;
    public static NewsDetailResponseDTO fromEntity(News news){
        //Entity를 받아와서 DTO로 변경해줌
        return NewsDetailResponseDTO.builder()
                .id(news.getId())
                .newsTitle(news.getNewsTitle())
                .newsDormitory(news.getNewsDormitory())
                .newsLink(news.getNewsLink())
                .newsDate(news.getNewsDate ())//만약 날짜를 문자열로 변경해야한다면 형변환 필요
                .newsViews(news.getNewsViews ())
                .build();
    }
}
