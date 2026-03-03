import os
import json
import requests
from bs4 import BeautifulSoup
from kafka import KafkaProducer

BASE_URL = "https://www.anyang.ac.kr"
LIST_URL = f"{BASE_URL}/main/communication/notice.do"
KAFKA_BOOTSTRAP = os.environ.get("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092")

# 리스트 URL
url = f"{LIST_URL}?mode=list&articleLimit=10&article.offset=0"

html_text = requests.get(url)
html = BeautifulSoup(html_text.text, "html.parser")
html_notice = html.select(
    "#cms-content > div > div > div.bn-list-common01.type01.bn-common-cate > table > tbody > tr"
)

notice_list = []


def build_full_url(href: str) -> str:
    """상대 경로를 절대 URL로 변환"""
    if not href:
        return ""
    href = href.strip()
    if href.startswith("http"):
        return href
    if href.startswith("/"):
        return BASE_URL + href
    return LIST_URL + href


for i in range(len(html_notice) - 1, 0, -1):
    notice = html.select(
        f"#cms-content > div > div > div.bn-list-common01.type01.bn-common-cate > table > tbody > tr:nth-child({i})"
    )
    if not notice:
        continue

    notice = notice[0]

    title_element = notice.select_one("td.b-td-left.b-td-title > div > a")
    title = title_element.get("title") if title_element else "No title"
    title = title.replace(" 자세히 보기", "")

    link_add = title_element.get("href") if title_element else "#"
    notice_url = build_full_url(link_add)  # ← URL 100% 정확하게 생성

    # articleNo 추출
    articleId = None
    for part in link_add.split("&"):
        if "articleNo=" in part:
            articleId = part.split("=")[1]
            break
    if articleId is None:
        continue

    category = (
        notice.select_one("td.b-cate-box").text.strip()
        if notice.select_one("td.b-cate-box")
        else "No category"
    )
    writer = (
        notice.select_one("td:nth-child(4)").text.strip()
        if notice.select_one("td:nth-child(4)")
        else "No writer"
    )
    date = (
        notice.select_one("td:nth-child(5)").text.strip()
        if notice.select_one("td:nth-child(5)")
        else "No date"
    )

    try:
        html_text_2 = requests.get(notice_url)
        html_2 = BeautifulSoup(html_text_2.text, "html.parser")

        download_html = html_2.select_one(".b-file-box > ul")
        if download_html:
            download_items = download_html.find_all("li")
            download_link = ""
            download_title = ""
            for item in download_items:
                a_tag = item.find("a")
                if a_tag:
                    file_url = build_full_url(a_tag["href"])
                    download_link = file_url + ", " + download_link
                    download_title = a_tag.text.strip() + ", " + download_title
        else:
            download_link = ""
            download_title = ""

        body = html_2.select_one(".b-content-box")

    except requests.RequestException as e:
        print(f"Error fetching {notice_url}: {e}")
        body = None
        download_link = ""
        download_title = ""

    notice_list.append(
        {
            "id": articleId,
            "noticeTitle": str(title),
            "noticeWriter": str(writer),
            "noticeUrl": str(notice_url),
            "noticeDate": str(date),
            "noticeCategory": str(category),
            "noticeBody": str(body),
            "noticeDownloadLink": str(download_link),
            "noticeDownloadTitle": str(download_title),
        }
    )

# 내림차순 정렬 (id 기준)
notice_list.sort(key=lambda x: int(x["id"]), reverse=True)
notice_list = notice_list[:5]

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP,
    value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode("utf-8"),
)

for notice in notice_list:
    producer.send("notice.crawled", value=notice)
    print(f"[Kafka] notice produced: id={notice['id']}, title={notice['noticeTitle']}")

producer.flush()
producer.close()
print(f"총 {len(notice_list)}건 notice Kafka 전송 완료")
