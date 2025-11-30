package org.ayu.doyouknowback.domain.fcm.form;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequestDTO {
    private String token;
    private String platform;
}
