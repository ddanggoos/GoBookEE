# Multi-stage build for GoBookEE
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jre17-temurin-jammy

# 톰캣 설정
ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

# 포트 설정
EXPOSE 8080

# 필요한 유틸 설치 (unzip)
USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

# 기존 ROOT 제거 및 WAR 수동 풀기(압축해제)
RUN rm -rf $CATALINA_HOME/webapps/ROOT
COPY --from=builder /app/target/*.war /tmp/app.war
RUN mkdir -p $CATALINA_HOME/webapps/ROOT \
    && unzip -q /tmp/app.war -d $CATALINA_HOME/webapps/ROOT \
    && rm -f /tmp/app.war

# 업로드 디렉토리 생성 및 권한 설정
RUN mkdir -p $CATALINA_HOME/webapps/ROOT/resources/upload/book \
    && mkdir -p $CATALINA_HOME/webapps/ROOT/resources/upload/place \
    && mkdir -p $CATALINA_HOME/webapps/ROOT/resources/upload/study \
    && mkdir -p $CATALINA_HOME/webapps/ROOT/resources/upload/user \
    && chmod -R 755 $CATALINA_HOME/webapps/ROOT/resources/upload
USER 1000

# 톰캣 실행
CMD ["catalina.sh", "run"]