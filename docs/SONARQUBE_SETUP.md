# SonarQube Setup Guide

## Quick Start

### 1. Start SonarQube with Docker

```bash
# Using the helper script
./scripts/sonarqube.sh start

# Or manually with Docker Compose
docker-compose -f docker/docker-compose.sonarqube.yml up -d
```

### 2. Access SonarQube

- URL: http://localhost:9000
- Default credentials: `admin` / `admin`
- **Important**: Change the default password on first login

### 3. Generate Analysis Token

```bash
./scripts/sonarqube.sh token
```

Or manually:

1. Go to User > My Account > Security
2. Generate a new token
3. Save it securely

### 4. Run Analysis

```bash
# Using the script
./scripts/sonarqube.sh analyze

# Or with Maven
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=YOUR_TOKEN
```

## GitHub Integration

### Repository Secrets

Add these secrets to your GitHub repository:

| Secret           | Description                                          |
| ---------------- | ---------------------------------------------------- |
| `SONAR_TOKEN`    | SonarQube analysis token                             |
| `SONAR_HOST_URL` | SonarQube server URL (e.g., http://your-server:9000) |

### Webhook Configuration (Optional)

1. In SonarQube: Administration > Configuration > Webhooks
2. Add new webhook:
   - Name: `GitHub`
   - URL: `https://api.github.com/repos/OWNER/REPO/statuses/{SHA}`

## Quality Gates

### Default Quality Gate Conditions

| Metric                 | Condition |
| ---------------------- | --------- |
| Coverage               | > 80%     |
| Duplicated Lines       | < 3%      |
| Maintainability Rating | A         |
| Reliability Rating     | A         |
| Security Rating        | A         |
| New Blocker Issues     | 0         |
| New Critical Issues    | 0         |

### Creating Custom Quality Gate

1. Go to Quality Gates in SonarQube
2. Create a new gate or copy the default
3. Add/modify conditions as needed
4. Set as default for the project

## SonarQube Rules Configuration

### Customizing Rules

1. Go to Quality Profiles
2. Select Java profile
3. Activate/deactivate rules as needed

### Recommended Rule Changes

```xml
<!-- In sonar-project.properties -->
sonar.issue.ignore.multicriteria=e1,e2

# Ignore TODO comments
sonar.issue.ignore.multicriteria.e1.ruleKey=java:S1135
sonar.issue.ignore.multicriteria.e1.resourceKey=**/*.java

# Ignore field injection warnings for Spring
sonar.issue.ignore.multicriteria.e2.ruleKey=java:S6813
sonar.issue.ignore.multicriteria.e2.resourceKey=**/*.java
```

## Troubleshooting

### SonarQube Won't Start

```bash
# Check Docker logs
docker logs safezone-sonarqube

# Common fix for memory issues
sudo sysctl -w vm.max_map_count=524288
```

### Analysis Fails

1. Verify token is correct
2. Check SonarQube is running: `curl http://localhost:9000/api/system/health`
3. Ensure project key matches

### Coverage Not Showing

1. Verify JaCoCo reports are generated
2. Check path in `sonar.coverage.jacoco.xmlReportPaths`
3. Run `mvn jacoco:report` before analysis

## Production Deployment

### Using External Database

```yaml
# In docker-compose.sonarqube.yml
environment:
  SONAR_JDBC_URL: jdbc:postgresql://db-server:5432/sonar
  SONAR_JDBC_USERNAME: sonar
  SONAR_JDBC_PASSWORD: secure_password
```

### HTTPS Configuration

1. Use a reverse proxy (nginx, traefik)
2. Configure SSL certificates
3. Update `SONAR_HOST_URL` accordingly

### Performance Tuning

```yaml
environment:
  SONAR_WEB_JAVAOPTS: "-Xmx2g -Xms1g"
  SONAR_CE_JAVAOPTS: "-Xmx2g -Xms1g"
  SONAR_SEARCH_JAVAOPTS: "-Xmx2g -Xms1g"
```

## Useful Commands

```bash
# Check status
./scripts/sonarqube.sh status

# Stop SonarQube
./scripts/sonarqube.sh stop

# Restart SonarQube
./scripts/sonarqube.sh restart

# View logs
docker logs -f safezone-sonarqube

# Access PostgreSQL
docker exec -it safezone-sonarqube-db psql -U sonar
```
