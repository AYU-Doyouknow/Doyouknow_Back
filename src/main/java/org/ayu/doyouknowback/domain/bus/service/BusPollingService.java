package org.ayu.doyouknowback.domain.bus.service;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.domain.bus.direction.BusDirection;
import org.ayu.doyouknowback.domain.bus.service.BusArrivalCache;
import org.ayu.doyouknowback.domain.bus.form.BusRequestDTO;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class BusPollingService {
    private final BusService busService;
    private final BusArrivalCache cache;

    @Scheduled(fixedDelayString = "${bus.Renewal.time:120000}")
    public void renewal() {
        //한국 시간 기준 오전 06시부터 22시 30분까지 갱신을 허용함
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
        if(now.isBefore(LocalTime.of(6, 0))||now.isAfter(LocalTime.of(22, 30))){
            return;
        }
        for(BusDirection dir : BusDirection.values()) {
            BusResponseDTO dto = busService.fetchFromPublicApi(dir);
            // 실패하면 캐시를 덮어쓰지 않고 기존 값 유지(운영 안정성)
            if (dto != null && dto.isSuccess()) {
                cache.put(dir, dto);
            }
        }
    }
}