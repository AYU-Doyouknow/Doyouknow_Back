package org.ayu.doyouknowback.global.cache;

import org.ayu.doyouknowback.domain.bus.enums.BusEnums;
import org.ayu.doyouknowback.domain.bus.form.BusResponseDTO;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class BusCache {
    /*
      캐시에 저장될 데이터 단위.
      @param data            버스 도착 정보 결과 DTO
      @param updatedAtMillis 데이터가 마지막으로 갱신된 시간
     */
    public record Entry(BusResponseDTO data, long updatedAtMillis) {}
    /*
       버스 방향(Key)별로 도착 정보(Value)를 매핑하여 저장하는 저장소.
       ConcurrentHashMap을 사용하여 여러 스레드가 동시에 사용되어도 안정성 보장이 가능함.!
     */
    private final ConcurrentHashMap<BusEnums, Entry> cache = new ConcurrentHashMap<>();
    /*
    새로운 버스 데이터를 캐시에 저장하거나 기존 데이터를 업데이트
    풀링을 통해 주기적으로 호출
     */
    public void put(BusEnums dir, BusResponseDTO dto) {
        cache.put(dir, new Entry(dto, System.currentTimeMillis()));
    }
    /*
    저장된 캐시데이터를 조회함
    유저가 API를 요청하였을 때 호출되는 부분
    */
    public Entry get(BusEnums dir) {
        return cache.get(dir);
    }
}
