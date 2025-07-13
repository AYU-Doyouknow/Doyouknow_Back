package org.ayu.doyouknowback.notice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class NoticeSchedulerConfiguration {

    // 매일 9시~18시 사이 매 30분마다 실행 (cron 설명: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0/30 9-18 * * MON-FRI", zone = "Asia/Seoul")
    public void runNoticePythonScript() {
        // 로컬 환경 개발용 주소
        //String pythonScriptPath = "C:\\Doyouknow\\Doyouknow_Crawling\\notice.py";

        // 서버 환경용 주소
        String pythonScriptPath = "/home/ubuntu/crawling/notice.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("notice.py 실행 성공 (" + pythonScriptPath + ")");
            } else {
                System.err.println("notice.py 실행 실패, 종료 코드: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
