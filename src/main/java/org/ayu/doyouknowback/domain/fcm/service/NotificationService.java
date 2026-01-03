package org.ayu.doyouknowback.domain.fcm.service;

import org.ayu.doyouknowback.domain.fcm.form.NotificationTokenRequestDTO;

public interface NotificationService {

    void saveToken(NotificationTokenRequestDTO dto);
}
