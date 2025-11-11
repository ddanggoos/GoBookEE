#!/bin/bash

# GoBookEE 로컬 배포 스크립트 (Docker Desktop용)
# 이 스크립트는 로컬 개발 환경에서 실행됩니다

set -e

echo "🚀 GoBookEE 로컬 배포 시작..."

# 1. 프로젝트 빌드
echo "📦 프로젝트 빌드 중..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패!"
    exit 1
fi

echo "✅ 빌드 완료!"

# 2. 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker stop gobookee-app 2>/dev/null || true
docker rm gobookee-app 2>/dev/null || true

# 3. Docker 이미지 빌드
echo "🐳 Docker 이미지 빌드 중..."
docker build -t gobookee:local .

# 4. 컨테이너 실행
echo "🚀 컨테이너 실행 중..."
docker run -d \
  --name gobookee-app \
  -p 8080:8080 \
  -v gobookee-uploads:/usr/local/tomcat/webapps/ROOT/resources/upload \
  -v gobookee-logs:/usr/local/tomcat/logs \
  --restart unless-stopped \
  gobookee:local

# 5. 헬스 체크
echo "🔍 헬스 체크 중..."
for i in {1..30}; do
  if curl -f http://localhost:8080/ >/dev/null 2>&1; then
    echo "✅ 배포 성공! 애플리케이션이 정상적으로 실행 중입니다."
    break
  fi
  echo "   대기 중... ($i/30)"
  sleep 10
done

# 6. 정리
echo "🧹 정리 중..."
docker image prune -f

echo "🎉 GoBookEE 로컬 배포 완료!"
echo "📋 접속 정보:"
echo "  - 애플리케이션: http://localhost:8080"
echo "  - 컨테이너 상태: docker ps | grep gobookee-app"
echo "  - 로그 확인: docker logs gobookee-app"
echo "  - 컨테이너 중지: docker stop gobookee-app"
