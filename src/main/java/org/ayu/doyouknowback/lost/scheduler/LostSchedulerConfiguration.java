//package org.ayu.doyouknowback.lost.scheduler;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class LostSchedulerConfiguration {
//
//    // 매일 23시~01시 사이 매 5분마다 실행 (cron 설명: 초 분 시 일 월 요일)
//    @Scheduled(cron = "0 * 23-23,0-1 * * *")
//    public void runLostPythonScript() {
//        // 로컬 환경 개발용 주소
//        String pythonScriptPath = "C:\\Doyouknow\\Doyouknow_Crawling\\lost.py";
//        // 서버 환경용 주소
//        // String pythonScriptPath = "/home/ubuntu/크롤링폴더";
//        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);
//
//        try {
//            Process process = processBuilder.start();
//            int exitCode = process.waitFor();
//
//            if (exitCode == 0) {
//                System.out.println("lost.py 실행 성공");
//            } else {
//                System.err.println("lost.py 실행 실패, 종료 코드: " + exitCode);
//            }
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//}
