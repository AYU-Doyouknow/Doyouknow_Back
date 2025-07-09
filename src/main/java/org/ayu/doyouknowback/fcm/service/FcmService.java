package org.ayu.doyouknowback.fcm.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.fcm.domain.Fcm;
import org.ayu.doyouknowback.fcm.form.FcmTokenRequestDTO;
import org.ayu.doyouknowback.fcm.repository.FcmRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;

    public void sendNotificationToAllUser(String title, String body){
        List<Fcm> allToken = fcmRepository.findAll();

        for(Fcm fcm : allToken){
            sendNotification(fcm.getToken(), title, body);
        }
    }

    public void sendNotification(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e){
            // 없는 토큰 삭제
            if(e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED){
                fcmRepository.deleteByToken(token);
            } else{
                e.printStackTrace();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveToken(FcmTokenRequestDTO fcmTokenRequestDTO){
        Optional<Fcm> optionalFcm = fcmRepository.findByToken(fcmTokenRequestDTO.getToken());

        if(optionalFcm.isPresent()){
            return;
        }

        Fcm fcm = Fcm.toSaveEntity(fcmTokenRequestDTO);
        fcmRepository.save(fcm);
    }
}
