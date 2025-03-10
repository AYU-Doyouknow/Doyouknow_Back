package org.ayu.doyouknowback.lost.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
