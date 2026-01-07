package org.ayu.doyouknowback.domain.bus.controller;

import org.ayu.doyouknowback.domain.bus.form.BusRequestDTO;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;
import org.ayu.doyouknowback.domain.bus.service.BusService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
public class BusController {

    private final BusService busService;

    // 검증방법: GET /api/bus/arrival?direction=ANYtoAYU or AYU to ANY
    @GetMapping("/arrival")
    public BusResponseDTO getArrival(@RequestParam String direction) {
        return busService.getArrival(new BusRequestDTO(direction));
    }

    // 검증방법: POST /api/bus/arrivals
    // body 값: [{"direction":"ANYtoAYU"},{"direction":"AYUtoANY"}]
    @PostMapping("/arrivals")
    public List<BusResponseDTO> getArrivals(@RequestBody List<BusRequestDTO> requests) {
        return busService.getArrivals(requests);
    }
}
