package org.ayu.doyouknowback.lost.lost.domain;
/*
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
    private String lostWriter;
    private String lostDate;

    @Column(columnDefinition = "TEXT") // JPA에서 TEXT 타입으로 처리 (65,535자)
    private String lostBody;

    public static Lost toSaveEntity(LostRequestDTO lostRequestDTO){
        return Lost.builder()
                .id(lostRequestDTO.getId())
                .lostTitle(lostRequestDTO.getLostTitle())
                .lostWriter(lostRequestDTO.getLostWriter())
                .lostDate(lostRequestDTO.getLostDate())
                .lostBody(lostRequestDTO.getLostBody())
                .build();
    }
}
*/