/*package org.ayu.doyouknowback.news.scheduler;

import lombok.RequiredArgsConstructor;
import org.ayu.doyouknowback.news.domain.News;
import org.ayu.doyouknowback.news.form.NewsRequestDTO;
import org.ayu.doyouknowback.news.repository.NewsRepository;
import org.ayu.doyouknowback.news.service.NewsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsSchedulerConfiguration {

    private final NewsRepository newsRepository;
    private final NewsService newsService;

    @Scheduled(cron = "0 0 9,13,17 * * *")
    public void runNewsPythonScript() {
        String pythonScriptPath = "/home/ubuntu/크롤링폴더/News.py";

        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath);

        try {
            Process process = processBuilder.start();

            // 🔽 Python 출력 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> crawledTitles = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                crawledTitles.add(line.trim());
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.err.println("✖ News.py 실행 실패 - 종료 코드: " + exitCode);
                return;
            }

            System.out.println("✔ News.py 실행 성공");

            // 🔽 DB에서 최신 뉴스 제목 5개 조회
            List<String> dbTitles = newsRepository.findTop5ByOrderByIdDesc()
                    .stream()
                    .map(News::getNewsTitle)
                    .toList();

            // 🔽 중복 검사
            boolean hasNew = crawledTitles.stream()
                    .anyMatch(title -> !dbTitles.contains(title));

            if (!hasNew) {
                System.out.println("뉴스 중복됨 → 저장 생략");
                return;
            }

            System.out.println("새로운 뉴스 감지 → 저장 실행");

            // ✅ 저장용 DTO 리스트 생성
            List<NewsRequestDTO> requestList = crawledTitles.stream()
                    .map(title -> NewsRequestDTO.builder()
                            .id(null)
                            .newsTitle(title)
                            .newsWriter("자동수집")
                            .newsDate(LocalDate.now().toString()) // ex: "2025-06-08"
                            .build())
                    .toList();

            // ✅ 실제 저장
            newsService.save(requestList);

        } catch (IOException | InterruptedException e) {
            System.err.println(" Python 실행 또는 결과 읽기 실패");
            e.printStackTrace();
        }
    }
}
*/