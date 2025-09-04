import requests
from bs4 import BeautifulSoup
import json  # JSON 출력용

def crawl_anyang_weather():
    try:
        url = "https://search.naver.com/search.naver"
        params = {
            "where": "nexearch",
            "sm": "top_hty",
            "fbm": "0",
            "ie": "utf8",
            "query": "안양 날씨"
        }
        headers = {
            "User-Agent": (
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"
            )
        }

        resp = requests.get(url, params=params, headers=headers, timeout=5)
        resp.raise_for_status()
        soup = BeautifulSoup(resp.text, "html.parser")

        # 현재 온도
        temp_tag = soup.select_one(".temperature_text strong")
        temperature = temp_tag.get_text(strip=True) if temp_tag else "N/A"

        # 날씨 상태
        condition_tag = soup.select_one(".weather_graphic .blind")
        condition = condition_tag.get_text(strip=True) if condition_tag else "N/A"

        # 요약
        summary_tag = soup.select_one(".summary")
        summary = summary_tag.get_text(strip=True) if summary_tag else "N/A"

        # 체감온도, 습도, 풍속
        feels_like, humidity, wind_speed = "N/A", "N/A", "N/A"
        summary_list = soup.select(".summary_list .sort")
        if len(summary_list) >= 3:
            feels_like = summary_list[0].select_one(".desc").get_text(strip=True)
            humidity = summary_list[1].select_one(".desc").get_text(strip=True)
            wind_speed = summary_list[2].select_one(".desc").get_text(strip=True)

        # 미세먼지/자외선 등
        additional_info = {}
        for li in soup.select(".today_chart_list .item_today"):
            t = li.select_one(".title")
            v = li.select_one(".txt")
            if t and v:
                key = t.get_text(strip=True)
                value = v.get_text(strip=True)
                # 필요 없는 항목 제거
                if key not in ["날씨를 공유해보세요!", "오전미세", "오전초미세", "오후미세", "오후초미세"]:
                    additional_info[key] = value

        # 결과 dict (영문 key)
        weather_data = {
            "temperature": temperature,
            "condition": condition,
            "summary": summary,
            "feelsLike": feels_like,
            "humidity": humidity,
            "windSpeed": wind_speed,
            # "additional_info": additional_info
        }

        return weather_data

    except Exception as e:
        print(f"[ERROR] 크롤링 중 오류: {e}")
        return {}

if __name__ == "__main__":
    # 크롤링 실행
    weather = crawl_anyang_weather()

    if weather:
        # 터미널에 보기 편하게 JSON 출력
        print("===== 크롤링 결과 =====")
        print(json.dumps(weather, ensure_ascii=False, indent=4))  # 들여쓰기 적용

        # 서버 전송용 리스트
        weather_list = weather

        api_url = "http://localhost:8080/weather/addWeather"
        try:
            response = requests.post(api_url, json=weather_list, timeout=5)
            if response.status_code == 201:
                print("Weather successfully added.")
            else:
                print(f"Failed to add weather. Status code: {response.status_code}")
                print(response.text)
        except requests.RequestException as e:
            print(f"[ERROR] 서버 요청 실패: {e}")
    else:
        print("크롤링 결과가 없어 서버로 전송하지 않습니다.")
