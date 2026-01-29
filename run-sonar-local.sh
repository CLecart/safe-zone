# Usage: source this script to load .env variables and run Maven with SonarQube token
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo ".env file not found!"
  return 1
fi

if [ -z "$SONAR_TOKEN" ]; then
  echo "SONAR_TOKEN is not set in .env!"
  return 1
fi

# Run Maven with Sonar token
echo "Running Maven with Sonar token..."
SONAR_HOST_URL=${SONAR_HOST_URL:-https://sonarcloud.io}
echo "Using SONAR_HOST_URL=$SONAR_HOST_URL"
# If targeting SonarCloud, prefer Automatic Analysis via SonarCloud GitHub App.
# Require an explicit override to avoid accidental local scans against SonarCloud.
if [[ "$SONAR_HOST_URL" == *"sonarcloud.io"* ]] && [ "${FORCE_LOCAL_SONAR:-}" != "1" ]; then
  echo "Detected SonarCloud host; Automatic Analysis may be enabled for project CLecart_safe-zone."
  echo "To run a local scan against SonarCloud, set FORCE_LOCAL_SONAR=1 and re-run this script."
  exit 1
fi
mvn clean verify -Psonar -Dsonar.host.url="$SONAR_HOST_URL" -Dsonar.login="$SONAR_TOKEN" "$@"
