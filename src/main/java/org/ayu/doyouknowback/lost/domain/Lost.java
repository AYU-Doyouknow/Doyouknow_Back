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
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lostTitle;
    private String lostAuthor;
    private String lostDate;

    @Column(columnDefinition = "TEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String lostBody;

    public static Lost toSaveEntity(LostRequestDTO lostRequestDTO){
        return Lost.builder()
                .id(lostRequestDTO.getId())
                .lostTitle(lostRequestDTO.getLostTitle())
                .lostAuthor(lostRequestDTO.getLostAuthor())
                .lostDate(lostRequestDTO.getLostDate())
                .lostBody(lostRequestDTO.getLostBody())
                .build();
    }
}
