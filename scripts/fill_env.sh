#!/usr/bin/env bash
set -euo pipefail

cat <<'WARN'
This script will prompt you to enter secrets interactively (input will be hidden where possible).
It will write them to `./.env` in the repository root. DO NOT paste secrets into chat.
WARN

read -p "POSTGRES_USER (default 'sonar'): " POSTGRES_USER
POSTGRES_USER=${POSTGRES_USER:-sonar}

read -s -p "POSTGRES_PASSWORD: " POSTGRES_PASSWORD; echo
read -s -p "SONAR_TOKEN: " SONAR_TOKEN; echo
read -s -p "SONAR_HOST_URL (e.g. http://localhost:9000): " SONAR_HOST_URL; echo
read -s -p "GITHUB_TOKEN (optional): " GITHUB_TOKEN; echo
read -s -p "MAIL_USERNAME (optional): " MAIL_USERNAME; echo
read -s -p "EMAIL_PASSWORD (optional): " EMAIL_PASSWORD; echo
read -s -p "SENDGRID_API_KEY (optional): " SENDGRID_API_KEY; echo
read -p "EMAIL_TO (optional, comma separated): " EMAIL_TO
read -p "EMAIL_FROM (optional): " EMAIL_FROM

cat > .env <<EOF
# Local environment (DO NOT commit)
POSTGRES_USER=${POSTGRES_USER}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
SONAR_TOKEN=${SONAR_TOKEN}
SONAR_HOST_URL=${SONAR_HOST_URL}
GITHUB_TOKEN=${GITHUB_TOKEN}
MAIL_USERNAME=${MAIL_USERNAME}
EMAIL_PASSWORD=${EMAIL_PASSWORD}
SENDGRID_API_KEY=${SENDGRID_API_KEY}
EMAIL_TO=${EMAIL_TO}
EMAIL_FROM=${EMAIL_FROM}
EOF

chmod 600 .env || true

echo "Wrote .env (permissions set to 600 if supported)."
echo "Reminder: .env is in .gitignore; do not commit it."
