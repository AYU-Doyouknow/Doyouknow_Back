package org.ayu.doyouknowback.domain.news.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.news.domain.News;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class NewsDetailResponseDTO {
    private Long id;
    private String newsTitle;// 타이틀
    private String newsWriter; //작성자
    private String newsDate; //게시일
    private String newsBody;

    public static NewsDetailResponseDTO fromEntity(News news){
        //Entity를 받아와서 DTO로 변경해줌
        return NewsDetailResponseDTO.builder()
                .id(news.getId())
                .newsTitle(news.getNewsTitle())
                .newsWriter(news.getNewsWriter())
                .newsDate(news.getNewsDate ())//만약 날짜를 문자열로 변경해야한다면 형변환 필요
                .newsBody(news.getNewsBody())
                .build();
    }
}