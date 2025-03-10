package org.ayu.doyouknowback.lost.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.service.LostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lost")
public class LostController {

    private final LostService lostService;

    //데이터 저장
    @PostMapping("/addLost")
    public ResponseEntity<String> createLost(@RequestBody List<LostRequestDTO> lostRequestDTOList){
        lostService.createLost(lostRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lost Successfully Created");
    }
}
