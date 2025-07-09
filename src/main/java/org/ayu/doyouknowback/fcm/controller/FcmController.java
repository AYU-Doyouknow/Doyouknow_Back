package org.ayu.doyouknowback.fcm.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.fcm.form.FcmTokenRequestDTO;
import org.ayu.doyouknowback.fcm.service.FcmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/saveToken")
    public ResponseEntity<String> saveToken(@RequestBody FcmTokenRequestDTO fcmTokenRequestDTO){
        fcmService.saveToken(fcmTokenRequestDTO);
        return ResponseEntity.ok("토큰 저장 완료");
    }
}
