package org.ayu.doyouknowback.global.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class MonitoringAspect {

    @Around("@annotation(monitored)")
    public Object measure(ProceedingJoinPoint joinPoint, Monitored monitored) throws Throwable {

        String category = monitored.value();
        String methodName = joinPoint.getSignature().toShortString();

        long startTime = System.nanoTime();
        Object result = null;
        boolean success = true;

        try{
            result = joinPoint.proceed();
            return result;
        }catch (Throwable e){
            success = false;
            throw e;
        }finally {
            long elapsedNanos = System.nanoTime() - startTime;
            double elapsedMs = elapsedNanos / 1_000_000.0;  // 나노초 → 밀리초 (소수점 포함)

            if(success){
                log.info("SUCCESS [{}] {} - {}ms", category, methodName, elapsedMs);
            }else{
                log.error("FAIL [{}] {} - {}ms", category, methodName, elapsedMs);
            }
        }
    }
}