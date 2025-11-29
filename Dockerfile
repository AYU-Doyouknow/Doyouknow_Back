#FROM openjdk:17
#RUN apt update && apt install -y python3
#COPY build/libs/*.jar app.jar
#COPY keystore.p12 keystore.p12
#EXPOSE 443
#ENTRYPOINT ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk

# Python3 및 pip 설치
RUN apt update && apt install -y python3 python3-pip

# Python 패키지 설치
RUN pip3 install requests beautifulsoup4

# Python3 스크립트 복사
COPY crawling/notice.py notice.py
COPY crawling/news.py news.py

# JAR 및 기타 파일 복사
COPY build/libs/*.jar app.jar
COPY keystore.p12 keystore.p12

# HTTPS 포트 오픈
EXPOSE 443

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
