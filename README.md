# SafeZone - E-Commerce Microservices with SonarQube

A comprehensive e-commerce microservices project with integrated code quality analysis using SonarQube.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway                               │
│                      (Spring Cloud Gateway)                      │
└─────────────────────────┬───────────────────────────────────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
┌───────────────┐ ┌───────────────┐ ┌───────────────┐
│    Product    │ │     Order     │ │     User      │
│   Service     │ │    Service    │ │   Service     │
│   (Port 8081) │ │   (Port 8082) │ │   (Port 8083) │
└───────┬───────┘ └───────┬───────┘ └───────┬───────┘
        │                 │                 │
        ▼                 ▼                 ▼
   ┌─────────┐       ┌─────────┐       ┌─────────┐
   │ H2 DB   │       │ H2 DB   │       │ H2 DB   │
   └─────────┘       └─────────┘       └─────────┘
```

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

## Quick Start

### 1. Start SonarQube

```bash
docker-compose -f docker/docker-compose.sonarqube.yml up -d
```

Wait for SonarQube to be ready at http://localhost:9000 (default credentials: admin/admin).

### 2. Build and Analyze

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=safe-zone \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<your-token>
```

### 3. Run Microservices

```bash
mvn spring-boot:run -pl product-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl api-gateway
```

## Project Structure

```
safe-zone/
├── api-gateway/              # API Gateway service
├── product-service/          # Product management microservice
├── order-service/            # Order management microservice
├── user-service/             # User management microservice
├── docker/                   # Docker configurations
│   ├── docker-compose.sonarqube.yml
│   └── docker-compose.services.yml
├── .github/
│   └── workflows/
│       ├── sonarqube-analysis.yml
│       └── pr-quality-gate.yml
├── sonar-project.properties  # SonarQube configuration
├── CODEOWNERS               # Code review assignments
└── pom.xml                  # Parent POM
```

## SonarQube Integration

### Local Setup

1. Start SonarQube using Docker:

   ```bash
   docker-compose -f docker/docker-compose.sonarqube.yml up -d
   ```

2. Access SonarQube at http://localhost:9000

   - Default credentials: admin/admin
   - Change password on first login

3. Generate a token:
   - Go to User > My Account > Security
   - Generate a new token for CI/CD

### GitHub Actions Integration

The project includes automated workflows:

- **sonarqube-analysis.yml**: Runs on every push to main/develop
- **pr-quality-gate.yml**: Blocks PRs that fail quality gates

### Quality Gates

The project enforces:

- Coverage > 80%
- No new critical/blocker issues
- Code duplication < 3%
- Security hotspots reviewed

## Code Review Process

### Branch Protection Rules

1. All changes must go through Pull Requests
2. At least 1 approval required
3. SonarQube quality gate must pass
4. All CI checks must pass

### CODEOWNERS

The `CODEOWNERS` file ensures appropriate reviewers:

- `/product-service/` → @product-team
- `/order-service/` → @order-team
- `/user-service/` → @user-team

## Notifications

### Slack Integration

Configure in SonarQube:

1. Administration > Configuration > Webhooks
2. Add Slack webhook URL

### Email Notifications

Configure SMTP in `docker/docker-compose.sonarqube.yml`

## IDE Integration

### IntelliJ IDEA

1. Install SonarLint plugin
2. Connect to SonarQube server
3. Bind project for synchronized rules

### VS Code

1. Install SonarLint extension
2. Configure connection in settings.json

## API Documentation

Each service exposes Swagger UI at `/swagger-ui.html`

## Security

- All endpoints require authentication (except health checks)
- JWT-based authentication
- Role-based access control
- SonarQube security hotspot detection

## Contributing

1. Create feature branch from `develop`
2. Make changes with tests
3. Ensure SonarQube quality gate passes
4. Submit PR for review
5. Merge after approval

## License

MIT License
