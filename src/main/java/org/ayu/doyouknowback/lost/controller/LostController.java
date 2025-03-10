package org.ayu.doyouknowback.lost.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.form.LostDetailResponseDTO;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.lost.service.LostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lost")
public class LostController {

    private final LostService lostService;

    //데이터 전체 조회
    @GetMapping("/all")
    public ResponseEntity<List<LostResponseDTO>> getAllLost(){
        List<LostResponseDTO> lostResponseDTOList = lostService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(lostResponseDTOList);
    }

    //데이터 상세 조회
    @GetMapping("/detail/{LostId}")
    public ResponseEntity<LostDetailResponseDTO> getDetailLost(@PathVariable Long LostId){
        LostDetailResponseDTO lostDetailResponseDTO = lostService.getfindById(LostId);
        return ResponseEntity.status(HttpStatus.OK).body(lostDetailResponseDTO);
    }

    //데이터 저장
    @PostMapping("/addLost")
    public ResponseEntity<String> createLost(@RequestBody List<LostRequestDTO> lostRequestDTOList){
        lostService.createLost(lostRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lost Successfully Created");
    }

}
