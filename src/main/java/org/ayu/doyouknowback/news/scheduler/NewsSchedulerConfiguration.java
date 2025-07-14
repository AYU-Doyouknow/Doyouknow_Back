package org.ayu.doyouknowback.news.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component // 스프링 빈으로 관리, 스케줄러 기능 사용
@RequiredArgsConstructor
public class NewsSchedulerConfiguration {

    // 매일 9시~18시 사이 매 4시간마다 실행 (cron 설명: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 9,13,17 * * MON-FRI", zone = "Asia/Seoul")
    public void runNewsPythonScript() {
        // 로컬 환경 개발용 주소
        //String pythonScriptPath = "C:\\Doyouknow\\Doyouknow_Crawling\\News.py";

        // 서버 환경용 주소
        String pythonScriptPath = "/home/ubuntu/crawling/news.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python3", pythonScriptPath);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("News.py 실행 성공 (" + pythonScriptPath + ")");
            } else {
                System.err.println("News.py 실행 실패, 종료 코드: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}