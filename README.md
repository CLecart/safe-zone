# SafeZone – E-Commerce Microservices (Audit-Ready)

Projet e-commerce microservices avec intégration stricte de la qualité et sécurité via SonarCloud et CI/CD GitHub Actions.

> **Conforme à l’audit et à l’énoncé strict** (protection de branche, PR obligatoire, checks qualité, bonus SonarCloud, optionnels IDE/notifications).

## Architecture

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

## Prérequis

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (pour tests locaux)
- Git

## Démarrage rapide

### 1. Analyse Qualité (SonarCloud)

L’analyse qualité et sécurité est **automatique** à chaque push/PR grâce à GitHub Actions et SonarCloud.

**Aucune configuration locale requise pour l’audit.**

### 2. Lancer les microservices (pour développement local)

```bash
mvn spring-boot:run -pl product-service
mvn spring-boot:run -pl order-service
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl api-gateway
```

## Structure du projet

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

## Intégration SonarCloud & CI/CD

- **Analyse qualité/sécurité** : Automatique à chaque push/PR via GitHub Actions & SonarCloud ([voir le projet sur SonarCloud](https://sonarcloud.io/)).
- **Protection de branche** : PR obligatoire, merge bloqué si qualité KO.
- **Quality Gate** : Aucune vulnérabilité critique, duplications < 3%, sécurité et couverture testées.

## Processus de revue & merge

1. Toute modification passe par une Pull Request (PR)
2. Merge impossible si SonarCloud ou CI échoue
3. (Bonus) CODEOWNERS pour assigner les reviewers automatiquement

## Bonus & Optionnels

- **Couverture de tests** : Peut être activée pour bonus audit (voir doc SonarCloud + Jacoco)
- **Notifications** : Slack/email configurables dans SonarCloud (Administration > Webhooks)
- **SonarLint IDE** : Feedback qualité en temps réel (VS Code/IntelliJ)

## Intégration IDE (optionnel)

- **IntelliJ/VS Code** : Installer SonarLint pour voir les problèmes qualité en direct

## Documentation API

Chaque microservice expose Swagger UI à `/swagger-ui.html`

## Sécurité

- Endpoints sécurisés (JWT, RBAC)
- Détection automatique des hotspots sécurité via SonarCloud

## Contribution

1. Créer une branche depuis `main` ou `develop`
2. Faire les changements + tests
3. Vérifier que SonarCloud et CI sont verts
4. Ouvrir une PR
5. Merger uniquement si tout est vert

## Licence

MIT
