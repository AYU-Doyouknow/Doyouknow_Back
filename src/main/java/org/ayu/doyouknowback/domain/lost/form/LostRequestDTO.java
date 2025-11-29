package org.ayu.doyouknowback.domain.lost.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LostRequestDTO {
    private Long id;
    private String lostTitle;
    private String lostWriter;
    private String lostDate;
    private String lostBody;
}
