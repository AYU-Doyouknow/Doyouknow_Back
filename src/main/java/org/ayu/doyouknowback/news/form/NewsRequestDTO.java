package org.ayu.doyouknowback.news.form;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class NewsRequestDTO { //데이터를 저장 혹은 업데이트 하기 위해서 사용되는 DTO
    private Long id;
    private String newsTitle;// 타이틀
    private String newsWriter; //작성자
    private String newsDate; //게시일
    private String newsBody;

//..이후 추가될 내용이 존재하면 추가

}