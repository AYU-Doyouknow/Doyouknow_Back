package org.ayu.doyouknowback.lost.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Lost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lostTitle;
    private String lostDormitory;
    private String lostLink;
    private String lostDate;
    private int lostViews;

    @Column(columnDefinition = "TEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String lostBody;

    public static Lost toSaveEntity(LostRequestDTO lostRequestDTO){
        return Lost.builder()
                //id값은 strategy에서 자동 생성 방식이므로 id는 세팅하지 않음
                .lostTitle(lostRequestDTO.getLostTitle())
                .lostDormitory(lostRequestDTO.getLostDormitory())
                .lostLink(lostRequestDTO.getLostLink())
                .lostDate(lostRequestDTO.getLostDate())
                .lostViews(lostRequestDTO.getLostViews())
                .lostBody(lostRequestDTO.getLostBody())
                .build();
    }
}
