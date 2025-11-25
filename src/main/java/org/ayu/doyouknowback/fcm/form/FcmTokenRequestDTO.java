package org.ayu.doyouknowback.fcm.form;

import lombok.*;
import org.ayu.doyouknowback.fcm.domain.Fcm;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmTokenRequestDTO {
    private String token;
    private String platform;
}
