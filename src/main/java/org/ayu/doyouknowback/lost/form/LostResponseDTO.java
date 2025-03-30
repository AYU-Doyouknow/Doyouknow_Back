package org.ayu.doyouknowback.lost.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.lost.domain.Lost;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LostResponseDTO {
    private Long id;
    private String lostTitle;
    private String lostAuthor;
    private String lostDate;


    public static LostResponseDTO fromEntity(Lost lost){
        return LostResponseDTO.builder()
                .id(lost.getId())
                .lostTitle(lost.getLostTitle())
                .lostAuthor(lost.getLostAuthor())
                .lostDate(lost.getLostDate())
                .build();
    }
}
