import subprocess
import sys
from apscheduler.schedulers.blocking import BlockingScheduler
from apscheduler.triggers.cron import CronTrigger

scheduler = BlockingScheduler(timezone="Asia/Seoul")


def run_notice_crawler():
    print("[Scheduler] notice.py 실행 시작")
    result = subprocess.run([sys.executable, "/app/notice.py"], capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print(f"[Scheduler] notice.py 실패: {result.stderr}")


def run_news_crawler():
    print("[Scheduler] news.py 실행 시작")
    result = subprocess.run([sys.executable, "/app/news.py"], capture_output=True, text=True)
    print(result.stdout)
    if result.returncode != 0:
        print(f"[Scheduler] news.py 실패: {result.stderr}")


# 공지사항: 평일 9~18시 매 30분 (기존 Spring @Scheduled와 동일)
scheduler.add_job(
    run_notice_crawler,
    CronTrigger(minute="0,30", hour="9-18", day_of_week="mon-fri"),
    id="notice_crawler",
)

# 뉴스: 평일 9시, 13시, 17시 (기존 Spring @Scheduled와 동일)
scheduler.add_job(
    run_news_crawler,
    CronTrigger(hour="9,13,17", day_of_week="mon-fri"),
    id="news_crawler",
)

print("[Scheduler] 크롤러 스케줄러 시작")
print("  - notice: 평일 9~18시 매 30분")
print("  - news: 평일 9시, 13시, 17시")
scheduler.start()
