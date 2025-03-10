package org.ayu.doyouknowback.lost.form;

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
    private String lostDormitory;
    private String lostLink;
    private String lostDate;
    private int lostViews;
    private String lostBody;
}
