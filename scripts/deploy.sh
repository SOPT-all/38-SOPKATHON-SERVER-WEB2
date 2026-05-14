#!/bin/bash
# EC2에서 Spring Boot 서비스를 재시작하는 배포 스크립트

set -euo pipefail

SERVICE_NAME="sopkathon"

sudo systemctl restart "${SERVICE_NAME}"
sudo systemctl is-active --quiet "${SERVICE_NAME}"
sudo systemctl status "${SERVICE_NAME}" --no-pager
