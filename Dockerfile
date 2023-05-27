# Docker를 올릴 때 jdk11 버전을 이용
FROM openjdk:11

EXPOSE ${8080}

ADD ./build/libs/*.jar app.jar

# 도커파일이 도커엔진을 통해서 컨테이너로 올라갈 때, 
# 도커 컨테이너의 시스템 진입점이 어디인지를 알려준다.
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "/app.jar"]