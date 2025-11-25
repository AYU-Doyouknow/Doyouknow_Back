package org.ayu.doyouknowback.domain.fcm.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FcmSendMessageReqeustDTO {
    private String title;
    private String body;
}
