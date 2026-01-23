# SafeZone - E-Commerce Microservices with SonarQube

A comprehensive e-commerce microservices project with integrated code quality analysis using SonarQube.

<!-- ci: 2026-01-19 - small non-functional entry to record today's contribution -->

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

> Note: SonarCloud supports _Automatic Analysis_ via the SonarCloud GitHub App. If Automatic Analysis is enabled for the project `CLecart_safe-zone`, do **not** run `sonar:sonar` from CI or in automated pipelines because that causes a conflict (SonarCloud will report an error). Instead rely on the SonarCloud app to analyse PRs automatically.

For a local, manual analysis against a Sonar server (or to force a SonarCloud scan locally), use the helper script (requires `SONAR_TOKEN` in `.env`):

```bash
# run a local analysis (will refuse against SonarCloud unless forced)
FORCE_LOCAL_SONAR=1 source ./run-sonar-local.sh
```

If you maintain CI scans instead of using Automatic Analysis, set the repository secret `FORCE_SONAR_CI_SCAN=true` and re-run the workflow to enable the CI-invoked Sonar scan.

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

## Local SonarQube Analysis with .env

Pour éviter de stocker des secrets dans le code, place ton token SonarQube dans un fichier `.env` (déjà ignoré par Git) :

```
SONAR_TOKEN=ton_token_ici
SONAR_HOST_URL=http://localhost:9000
```

Utilise le script fourni pour charger automatiquement les variables et lancer l’analyse :

```bash
source ./run-sonar-local.sh
```

Le script charge les variables de `.env` et lance Maven avec le bon token. Ne jamais commettre `.env` : il est déjà dans `.gitignore`.

Pour un exemple de structure, vois `.env.example`.
