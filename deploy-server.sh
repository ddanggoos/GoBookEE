#!/bin/bash

# GoBookEE 서버 배포 스크립트
# 이 스크립트는 서버에서 실행됩니다

set -e

IMAGE_TAG=${1:-latest}
CONTAINER_NAME="gobookee-app"
IMAGE_NAME="gobookee"

echo "🚀 GoBookEE 배포 시작... (이미지: $IMAGE_NAME:$IMAGE_TAG)"

# 1. 기존 컨테이너 중지 및 제거
echo "🛑 기존 컨테이너 중지 중..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

# 2. 새 컨테이너 실행
echo "🐳 새 컨테이너 실행 중..."
docker run -d \
  --name $CONTAINER_NAME \
  -p 8080:8080 \
  -v gobookee-uploads:/usr/local/tomcat/webapps/ROOT/resources/upload \
  -v gobookee-logs:/usr/local/tomcat/logs \
  --restart unless-stopped \
  $IMAGE_NAME:$IMAGE_TAG

# 3. 헬스 체크
echo "🔍 헬스 체크 중..."
for i in {1..30}; do
  if curl -f http://localhost:8080/ >/dev/null 2>&1; then
    echo "✅ 배포 성공! 애플리케이션이 정상적으로 실행 중입니다."
    break
  fi
  echo "   대기 중... ($i/30)"
  sleep 10
done

# 4. 정리
echo "🧹 정리 중..."
docker image prune -f

echo "🎉 GoBookEE 배포 완료!"
echo "📋 접속 정보:"
echo "  - 애플리케이션: http://$(hostname -I | awk '{print $1}'):8080"
echo "  - 컨테이너 상태: docker ps | grep $CONTAINER_NAME"
echo "  - 로그 확인: docker logs $CONTAINER_NAME"
