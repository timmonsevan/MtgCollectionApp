#!/bin/bash


IMAGE="timmonsevan/timmons-ceg3120:latest"

if [ -z "$1" ]; then
  echo "Usage: $0 <container-name>"
  exit 1
fi

CONTAINER_NAME="$1"

echo "Stopping and removing existing container (if any)..."
docker stop $CONTAINER_NAME 2>/dev/null || true
docker rm $CONTAINER_NAME 2>/dev/null || true

echo "Pulling the latest image from DockerHub..."
docker pull $IMAGE

echo "Restarting docker..."
systemctl restart docker

echo "Running a new container..."
docker run -d -p 5000:5000 $IMAGE

echo "Deployment complete."

