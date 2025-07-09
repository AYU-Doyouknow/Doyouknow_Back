package org.ayu.doyouknowback.lost.controller;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.lost.form.LostDetailResponseDTO;
import org.ayu.doyouknowback.lost.form.LostRequestDTO;
import org.ayu.doyouknowback.lost.form.LostResponseDTO;
import org.ayu.doyouknowback.lost.service.LostService;
import org.springframework.data.domain.Page;
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
    @GetMapping("/all") // http://localhost:8080/lost/all?page=0&size=10
    public ResponseEntity<Page<LostResponseDTO>> getAllLost(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort){
        Page<LostResponseDTO> lostResponseDTOList = lostService.getAll(page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(lostResponseDTOList);
    }

    //데이터 상세 조회
    @GetMapping("/detail/{LostId}")
    public ResponseEntity<LostDetailResponseDTO> getDetailLost(@PathVariable Long LostId){
        LostDetailResponseDTO lostDetailResponseDTO = lostService.getfindById(LostId);
        return ResponseEntity.status(HttpStatus.OK).body(lostDetailResponseDTO);
    }

    //검색 조회
    @GetMapping("/search")
    public ResponseEntity<Page<LostResponseDTO>> getSearchLost(
            @RequestParam String value,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "id,desc") String sort) {
        Page<LostResponseDTO> lostResponseDTOList = lostService.getSearch(value, page, size, sort);
        return ResponseEntity.status(HttpStatus.OK).body(lostResponseDTOList);
    }

    //데이터 저장
    @PostMapping("/addLost")
    public ResponseEntity<String> createLost(@RequestBody List<LostRequestDTO> lostRequestDTOList){
        lostService.createLost(lostRequestDTOList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Lost Successfully Created");
    }

}
