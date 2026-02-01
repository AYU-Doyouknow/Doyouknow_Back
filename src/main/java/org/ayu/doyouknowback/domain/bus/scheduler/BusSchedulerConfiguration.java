package org.ayu.doyouknowback.domain.bus.scheduler;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.domain.bus.enums.BusEnums;
import org.ayu.doyouknowback.global.cache.BusCache;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;
import org.ayu.doyouknowback.domain.bus.service.BusService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.ZoneId;


@Service
@RequiredArgsConstructor
public class BusSchedulerConfiguration {
    private final BusService busService;
    private final BusCache cache;

    @Scheduled(fixedDelayString = "${bus.Renewal.time:20000}")
    public void renewal() {
        //한국 시간 기준 오전 06시부터 22시 30분까지 갱신을 허용함
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
        if(now.isBefore(LocalTime.of(6, 0))||now.isAfter(LocalTime.of(22, 30))){
            return;
        }
        for(BusEnums dir : BusEnums.values()) {
            BusResponseDTO dto = busService.fetchFromPublicApi(dir);
            // 실패하면 캐시를 덮어쓰지 않고 기존 값 유지
            if (dto != null && dto.isSuccess()) {
                cache.put(dir, dto);
            }
        }
    }
}