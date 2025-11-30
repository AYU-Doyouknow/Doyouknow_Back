package org.ayu.doyouknowback.domain.fcm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ayu.doyouknowback.domain.fcm.form.FcmTokenRequestDTO;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Fcm {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String token;
    private String platform;

    public static Fcm toSaveEntity(FcmTokenRequestDTO fcmTokenRequestDTO){
        return Fcm.builder()
                .token(fcmTokenRequestDTO.getToken())
                .platform(fcmTokenRequestDTO.getPlatform())
                .build();
    }
}
