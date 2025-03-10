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
public class LostDetailResponseDTO {
    private Long id;
    private String lostTitle;
    private String lostDormitory;
    private String lostLink;
    private String lostDate;
    private int lostViews;
    private String lostBody;

    public static LostDetailResponseDTO fromEntity(Lost lost){
        return LostDetailResponseDTO.builder()
                .id(lost.getId())
                .lostTitle(lost.getLostTitle())
                .lostDormitory(lost.getLostDormitory())
                .lostLink(lost.getLostLink())
                .lostDate(lost.getLostDate())
                .lostViews(lost.getLostViews())
                .lostBody(lost.getLostBody())
                .build();
    }
}
