package org.ayu.doyouknowback.global.loadtest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Profile("loadtest")
@ConditionalOnProperty(name = "loadtest.seed.enabled", havingValue = "true")
@RequiredArgsConstructor
public class NoticeMockDataSeeder implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    private static final int TOTAL = 100_000;
    private static final int BATCH = 1_000;

    @Override
    public void run(String... args) {

        Integer cnt = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM notice", Integer.class);
        log.info("현재 notice row count = {}", cnt);

        // 이미 충분하면 스킵
        if (cnt != null && cnt >= TOTAL) {
            log.info("이미 {}건 이상 존재. seeding 스킵", TOTAL);
            return;
        }

        Random r = new Random(42);
        long start = System.currentTimeMillis();

        // 재현성을 위해 매번 초기화하고 싶다면 TRUNCATE 유지
        // 운영/공용 DB에서는 절대 켜지 않도록(loadtest profile + enabled) 해둔 상태임
        jdbcTemplate.execute("TRUNCATE TABLE notice");

        String sql = """
            INSERT INTO notice
            (id, notice_title, notice_writer, notice_date, notice_category,
             notice_download_link, notice_download_title, notice_body, notice_url)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        List<String> categories = List.of("학사", "장학", "등록", "일반", "행사");
        List<String> writers = List.of("관리자", "교무처", "학생처", "장학팀", "총무팀");

        for (int from = 1; from <= TOTAL; from += BATCH) {
            int to = Math.min(from + BATCH - 1, TOTAL);

            List<Object[]> params = new ArrayList<>(to - from + 1);

            for (int i = from; i <= to; i++) {
                String category = categories.get(r.nextInt(categories.size()));
                String writer = writers.get(r.nextInt(writers.size()));
                String date = LocalDate.now().minusDays(r.nextInt(365 * 3)).toString();

                String keyword = pickKeyword(r);
                String title = "[%s] %s 관련 공지 #%d".formatted(category, keyword, i);

                String body = generateBody(keyword, 600 + r.nextInt(600));
                String downloadLink = "https://example.com/download/" + i;
                String downloadTitle = "첨부파일_" + i + ".pdf";
                String url = "https://example.com/notice/" + i;

                params.add(new Object[]{
                        (long) i,
                        title,
                        writer,
                        date,
                        category,
                        downloadLink,
                        downloadTitle,
                        body,
                        url
                });
            }

            jdbcTemplate.batchUpdate(sql, params);
            log.info("Inserted {} ~ {}", from, to);
        }

        long end = System.currentTimeMillis();
        log.info("✅ 10만건 삽입 완료. 총 소요시간 = {} ms", (end - start));
    }

    private static String pickKeyword(Random r) {
        int p = r.nextInt(100);
        if (p < 20) return "장학";
        if (p < 40) return "등록";
        if (p < 50) return "휴강";
        if (p < 60) return "수강신청";
        return "공지";
    }

    private static String generateBody(String keyword, int len) {
        String base = "이 공지는 %s 관련 안내입니다. ".formatted(keyword);
        StringBuilder sb = new StringBuilder(len + 100);
        while (sb.length() < len) {
            sb.append(base);
            sb.append("세부 내용은 아래를 참고하세요. ");
            sb.append(UUID.randomUUID()).append(" ");
        }
        return sb.substring(0, len);
    }
}
