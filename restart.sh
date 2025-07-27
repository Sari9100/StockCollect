#!/bin/bash

# 스크립트 이름: redeploy.sh
# 목적: stock-collector Docker 이미지 재빌드 및 컨테이너 재시작

# 변수
IMAGE_NAME="stock-collector"
CONTAINER_NAME="stock-collector"
ENV_FILE=".env"

echo "🧼 이전 컨테이너 중지 및 제거..."
docker stop $CONTAINER_NAME 2>/dev/null
docker rm $CONTAINER_NAME 2>/dev/null

echo "🔨 프로젝트 빌드 (JAR 생성)..."
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "❌ Gradle 빌드 실패. 중단합니다."
  exit 1
fi

echo "🐳 Docker 이미지 빌드..."
docker build -t $IMAGE_NAME .

echo "🚀 Docker 컨테이너 실행..."
docker run -d \
  --name $CONTAINER_NAME \
  --env-file $ENV_FILE \
  $IMAGE_NAME

echo "✅ 완료: http://localhost:8080 (기본 포트 기준)"