import os
import requests
from bs4 import BeautifulSoup

BASE_URL = "https://www.anyang.ac.kr"
LIST_URL = f"{BASE_URL}/main/communication/school-news.do"
AUTH_TOKEN = os.environ.get("APP_AUTH_TOKEN")

# 리스트 URL (6개)
url = f"{LIST_URL}?mode=list&articleLimit=6"

html_text = requests.get(url)
html = BeautifulSoup(html_text.text, "html.parser")
html_news = html.select(
    "#cms-content > div > div > div.bn-list-common01.type01.bn-common > table > tbody > tr"
)

news_list = []

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

for i in range(len(html_news) - 1, 0, -1):
    news = html.select(
        f"#cms-content > div > div > div.bn-list-common01.type01.bn-common > table > tbody > tr:nth-child({i})"
    )

    if news:
        news = news[0]
        news_ID = news.select_one("td.b-num-box").text.strip()

        title_element = news.select_one("td.b-td-left.b-td-title > div > a")
        title = title_element.get("title") if title_element else "No title"
        title = title.replace(" 자세히 보기", "")

        link_add = title_element.get("href") if title_element else "#"
        news_url = build_full_url(link_add)

        writer = (
            news.select_one("td:nth-child(3)").text.strip()
            if news.select_one("td:nth-child(3)")
            else "No writer"
        )
        date = (
            news.select_one("td:nth-child(4)").text.strip()
            if news.select_one("td:nth-child(4)")
            else "No date"
        )

        try:
            html_text_2 = requests.get(news_url)
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
            print(f"Error fetching {news_url}: {e}")
            body = None
            download_link = ""
            download_title = ""

        news_list.append(
            {
                "id": news_ID,
                "newsTitle": str(title),
                "newsWriter": str(writer),
                "newsUrl": str(news_url),
                "newsDate": str(date),
                "newsBody": str(body),
                "newsDownloadLink": str(download_link),
                "newsDownloadTitle": str(download_title),
            }
        )

api_url = "https://doyouknow.shop/news/addNews"
headers = {
    "Authorization": AUTH_TOKEN,
    "Content-Type": "application/json",
}

response = requests.post(api_url, json=news_list, headers=headers)

if response.status_code == 201:
    print("News successfully added.")
else:
    print("Failed to add news.")
    print("Status:", response.status_code)
    print("Body:", response.text)
