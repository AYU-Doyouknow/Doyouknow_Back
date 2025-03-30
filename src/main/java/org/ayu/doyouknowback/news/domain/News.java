package org.ayu.doyouknowback.news.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class News {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //글 번호

    private String newsTitle;// 타이틀
    private String newsWriter; //작성자
    private String newsDate; //게시일
    private String newsDownloadLink; //다운로드 링크
    private String newsDownloadTitle; //다운로드 제목
    @Column(columnDefinition = "TEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String newsBody; // news 글 내용

    public static News toSaveEntity(NewsRequestDTO newsRequestDTO) {
        return News.builder()
                .id(newsRequestDTO.getId())
                .newsTitle(newsRequestDTO.getNewsTitle())
                .newsWriter(newsRequestDTO.getNewsWriter())
                .newsDate(newsRequestDTO.getNewsDate())
                .newsBody(newsRequestDTO.getNewsBody())
                .build();
    }
}