package org.ayu.doyouknowback.fcm.form;

import lombok.Getter;
import lombok.Setter;
import org.ayu.doyouknowback.fcm.domain.Fcm;

@Getter
@Setter
public class FcmTokenRequestDTO {
    private String token;
    private String platform;
}
