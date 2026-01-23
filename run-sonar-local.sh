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
echo "Running Maven with SonarQube token..."
mvn clean verify -Psonar -Dsonar.token=$SONAR_TOKEN "$@"
