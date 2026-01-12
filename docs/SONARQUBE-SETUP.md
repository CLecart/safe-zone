# SonarQube Setup Guide for SafeZone

This guide provides comprehensive instructions for setting up SonarQube locally with Docker and integrating it with your development workflow.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local SonarQube Setup](#local-sonarqube-setup)
3. [First-Time Configuration](#first-time-configuration)
4. [GitHub Integration](#github-integration)
5. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)
6. [Quality Gates](#quality-gates)
7. [Branch Protection Rules](#branch-protection-rules)
8. [Notifications Setup (BONUS)](#notifications-setup-bonus)
9. [IDE Integration with SonarLint (BONUS)](#ide-integration-with-sonarlint-bonus)
10. [Troubleshooting](#troubleshooting)

---

## Prerequisites

- **Docker** and **Docker Compose** installed
- **Java 21** (OpenJDK)
- **Maven 3.9+**
- **Git** configured with remote repositories
- **GitHub account** with repository access

---

## Local SonarQube Setup

### 1. Start SonarQube with Docker Compose

```bash
# From the project root directory
docker-compose up -d
```

This command starts two services:

- **SonarQube Community Edition 10.x** (accessible at http://localhost:9000)
- **PostgreSQL 15** (database backend)

### 2. Verify Services are Running

```bash
# Check container status
docker-compose ps

# View SonarQube logs
docker-compose logs -f sonarqube

# View PostgreSQL logs
docker-compose logs -f postgres
```

Wait for the message: `SonarQube is operational` (typically 2-3 minutes on first startup).

### 3. Environment Configuration

```bash
# Copy the example environment file
cp .env.example .env

# Edit .env with your credentials (optional for local development)
nano .env
```

Default credentials:

- **PostgreSQL User**: `sonar`
- **PostgreSQL Password**: `sonar`
- **SonarQube Admin**: `admin` / `admin` (change on first login)

---

## First-Time Configuration

### 1. Access SonarQube Web Interface

Open your browser and navigate to: **http://localhost:9000**

### 2. Initial Login

- **Username**: `admin`
- **Password**: `admin`

You'll be prompted to change the admin password immediately.

### 3. Generate Authentication Token

1. Click on your profile (top-right) → **My Account**
2. Navigate to **Security** tab
3. Under **Generate Tokens**:
   - Name: `safezone-local`
   - Type: **User Token**
   - Expires in: **90 days** (or No expiration)
4. Click **Generate** and **copy the token**
5. Update `.env` file with the token:

```bash
SONAR_TOKEN=your_generated_token_here
```

### 4. Create SafeZone Project

#### Option A: Manual Project Creation

1. Click **Create Project** → **Manually**
2. Fill in project details:
   - **Project key**: `com.safezone:safe-zone-parent`
   - **Display name**: `SafeZone Microservices`
3. Click **Set Up**
4. Choose **Locally** as analysis method
5. Select **Maven** as build tool

#### Option B: Use Existing Configuration

The project is already configured via `sonar-project.properties`. Simply run:

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=com.safezone:safe-zone-parent \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your_token_here
```

---

## GitHub Integration

### 1. Configure GitHub Authentication

1. Go to **Administration** → **Configuration** → **General Settings** → **DevOps Platform Integrations**
2. Select **GitHub** and fill in:
   - **Configuration name**: `safezone-github`
   - **GitHub URL**: `https://github.com`
   - **GitHub App details** (or use Personal Access Token)

### 2. Create GitHub Personal Access Token

1. Go to https://github.com/settings/tokens
2. Click **Generate new token (classic)**
3. Select scopes:
   - `repo` (full control)
   - `workflow` (update workflows)
4. Copy the token
5. Add to GitHub repository secrets:
   - Go to repository **Settings** → **Secrets and variables** → **Actions**
   - Add secret: `SONAR_TOKEN` with your SonarQube token
   - Add secret: `SONAR_HOST_URL` with `http://your-sonarqube-server:9000` (or cloud URL)

### 3. Link Repository to SonarQube Project

1. In SonarQube project settings, go to **General Settings** → **Pull Requests**
2. Set **Provider**: GitHub
3. Enter **Repository identifier**: `owner/repository-name`
4. Add **GitHub token** with appropriate permissions

---

## CI/CD Pipeline Configuration

The SafeZone project uses **GitHub Actions** for continuous integration. The pipeline is defined in `.github/workflows/ci-sonar.yml`.

### Pipeline Workflow

```yaml
Trigger (push/PR) → Build & Test → SonarQube Analysis → Quality Gate Check
```

### Pipeline Jobs

#### 1. Build and Test Job (`build-test`)

- Checks out code
- Sets up Java 21
- Caches Maven dependencies
- Runs `mvn clean verify` (compiles + tests + generates coverage)

#### 2. SonarQube Analysis Job (`sonar`)

- Depends on `build-test` completion
- Downloads build artifacts
- Runs `mvn sonar:sonar` with coverage reports
- Uploads results to SonarQube server

### Trigger Conditions

The pipeline runs on:

- **Push** to `main` or `feature/**` branches
- **Pull Requests** targeting `main`
- **Manual trigger** via workflow dispatch

### Verify Pipeline Configuration

```bash
# Check workflow syntax
cat .github/workflows/ci-sonar.yml

# View recent workflow runs
gh run list --workflow=ci-sonar.yml

# View specific run details
gh run view <run-id>
```

---

## Quality Gates

SafeZone uses strict quality gates defined in `sonar-project.properties`.

### Current Quality Gate Configuration

```properties
# Coverage Requirements
sonar.coverage.overall.minimum=80
sonar.coverage.branch.minimum=80

# Code Duplication
sonar.cpd.minimum=3

# Technical Debt
sonar.issue.ignore.block=e1,e2

# Security
sonar.security.ignoreSecurityHotspots=false
```

### Quality Gate Conditions

| Metric                     | Threshold | Severity |
| -------------------------- | --------- | -------- |
| **Coverage**               | ≥ 80%     | Error    |
| **Branch Coverage**        | ≥ 80%     | Error    |
| **Duplicated Lines**       | ≤ 3%      | Warning  |
| **Maintainability Rating** | A         | Error    |
| **Reliability Rating**     | A         | Error    |
| **Security Rating**        | A         | Error    |

### Current Project Status

All service implementations have achieved **100% coverage**:

- **UserServiceImpl**: 481/481 instructions, 26/26 branches
- **OrderServiceImpl**: 422/422 instructions, 92/92 branches
- **ProductServiceImpl**: 361/361 instructions, 75/75 branches

---

## Branch Protection Rules

Configure GitHub branch protection to enforce code quality before merging.

### 1. Enable Branch Protection

1. Go to repository **Settings** → **Branches**
2. Click **Add rule** under "Branch protection rules"
3. Set **Branch name pattern**: `main`

### 2. Required Settings

Enable the following rules:

#### Required Reviews

- ✅ **Require a pull request before merging**
- ✅ **Require approvals**: 1
- ✅ **Dismiss stale pull request approvals when new commits are pushed**
- ✅ **Require review from Code Owners** (optional)

#### Status Checks

- ✅ **Require status checks to pass before merging**
- ✅ **Require branches to be up to date before merging**
- Select required checks:
  - `build-test` (GitHub Actions job)
  - `sonar` (SonarQube analysis job)
  - `SonarQube Quality Gate` (if configured)

#### Additional Protection

- ✅ **Require conversation resolution before merging**
- ✅ **Require signed commits** (optional)
- ✅ **Include administrators** (enforce rules for admins)

### 3. Code Review Workflow

```
Developer → Create Feature Branch → Commit Changes → Push to GitHub
         ↓
    Open Pull Request → CI/CD Runs (Build + Test + SonarQube)
         ↓
    Code Review Required → Approve PR → Quality Gate Pass
         ↓
    Merge to Main (Protected)
```

---

## Notifications Setup (BONUS)

Configure notifications to alert team members about code quality issues.

### Option 1: Slack Integration

#### 1. Create Slack Incoming Webhook

1. Go to https://api.slack.com/apps
2. Create new app → Select workspace
3. Enable **Incoming Webhooks**
4. Add webhook to desired channel
5. Copy webhook URL

#### 2. Configure GitHub Actions Notification

Add to `.github/workflows/ci-sonar.yml` after sonar job:

```yaml
- name: Notify Slack
  if: always()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    text: "SonarQube analysis completed for ${{ github.repository }}"
    webhook_url: ${{ secrets.SLACK_WEBHOOK_URL }}
  env:
    SLACK_WEBHOOK_URL: ${{ secrets.SLACK_WEBHOOK_URL }}
```

#### 3. Add Slack Webhook Secret

```bash
gh secret set SLACK_WEBHOOK_URL --body "https://hooks.slack.com/services/YOUR/WEBHOOK/URL"
```

### Option 2: Email Notifications

#### 1. Configure SonarQube Email

1. Go to **Administration** → **Configuration** → **General Settings** → **Email**
2. Configure SMTP settings:
   - **SMTP host**: smtp.gmail.com (or your SMTP server)
   - **SMTP port**: 587
   - **SMTP username**: your-email@example.com
   - **SMTP password**: your-app-password
   - **From address**: sonarqube@example.com

#### 2. Enable User Notifications

1. Each user navigates to **My Account** → **Notifications**
2. Enable desired notifications:
   - ✅ **Quality gate status changes**
   - ✅ **New issues assigned to me**
   - ✅ **Issues resolved as fixed**

### Option 3: GitHub Actions Email

Add to workflow:

```yaml
- name: Send Email Notification
  if: failure()
  uses: dawidd6/action-send-mail@v3
  with:
    server_address: smtp.gmail.com
    server_port: 587
    username: ${{ secrets.EMAIL_USERNAME }}
    password: ${{ secrets.EMAIL_PASSWORD }}
    subject: "SonarQube Analysis Failed: ${{ github.repository }}"
    to: team@example.com
    from: github-actions@example.com
    body: "Quality gate failed for commit ${{ github.sha }}"
```

---

## IDE Integration with SonarLint (BONUS)

SonarLint provides real-time feedback in your IDE as you code.

### VS Code Setup

#### 1. Install SonarLint Extension

```bash
code --install-extension SonarSource.sonarlint-vscode
```

Or via VS Code Marketplace: Search "SonarLint"

#### 2. Configure Connected Mode

1. Open VS Code settings (`Ctrl+,` or `Cmd+,`)
2. Search for "SonarLint"
3. Click **Edit in settings.json**
4. Add configuration:

```json
{
  "sonarlint.connectedMode.connections.sonarqube": [
    {
      "serverUrl": "http://localhost:9000",
      "token": "your_sonarqube_token"
    }
  ],
  "sonarlint.connectedMode.project": {
    "projectKey": "com.safezone:safe-zone-parent"
  }
}
```

#### 3. Bind Project

1. Open Command Palette (`Ctrl+Shift+P` or `Cmd+Shift+P`)
2. Type: **SonarLint: Update all project bindings to SonarQube/SonarCloud**
3. Select your SafeZone project

### IntelliJ IDEA Setup

#### 1. Install SonarLint Plugin

1. Go to **File** → **Settings** → **Plugins**
2. Search for "SonarLint"
3. Click **Install** and restart IDEA

#### 2. Configure Connection

1. Go to **File** → **Settings** → **Tools** → **SonarLint**
2. Click **+** under "SonarQube Connections"
3. Fill in:
   - **Connection Name**: `SafeZone Local`
   - **Server URL**: `http://localhost:9000`
   - **Token**: Your SonarQube token
4. Click **Test Connection** → **OK**

#### 3. Bind Project

1. In SonarLint settings, select **Project Settings**
2. Enable **Bind project to SonarQube/SonarCloud**
3. Select **Connection**: SafeZone Local
4. Enter **Project key**: `com.safezone:safe-zone-parent`
5. Click **OK**

### Benefits of IDE Integration

- ✅ **Real-time issue detection** as you type
- ✅ **Inline rule descriptions** and fix suggestions
- ✅ **Consistent rules** with CI/CD pipeline
- ✅ **Catch issues before commit**
- ✅ **Quality gates preview** locally

---

## Troubleshooting

### Issue: SonarQube Container Won't Start

**Symptoms**: Container exits immediately or shows "unhealthy" status

**Solutions**:

1. Check system resources:

```bash
docker stats
```

2. Increase Docker memory (Docker Desktop → Settings → Resources):

   - Memory: ≥ 4GB recommended
   - Swap: ≥ 2GB

3. Check logs:

```bash
docker-compose logs sonarqube
```

4. Reset and restart:

```bash
docker-compose down -v
docker-compose up -d
```

### Issue: Quality Gate Fails Despite 100% Coverage

**Symptoms**: Pipeline shows red status even with perfect coverage

**Solutions**:

1. Check quality gate details in SonarQube UI
2. Review other metrics (duplications, code smells, security hotspots)
3. Verify coverage report path in `sonar-project.properties`:

```properties
sonar.coverage.jacoco.xmlReportPaths=**/target/site/jacoco/jacoco.xml
```

4. Ensure JaCoCo reports are generated:

```bash
mvn clean verify
ls -la */target/site/jacoco/jacoco.xml
```

### Issue: GitHub Actions Can't Connect to SonarQube

**Symptoms**: `sonar` job fails with connection errors

**Solutions**:

1. Verify secrets are set correctly:

```bash
gh secret list
```

2. Check `SONAR_TOKEN` is valid:

   - Regenerate token in SonarQube
   - Update GitHub secret

3. Verify `SONAR_HOST_URL`:

   - For local: Use public URL (not localhost)
   - For cloud: Use `https://sonarcloud.io`

4. Check network accessibility from GitHub Actions runners

### Issue: Coverage Reports Not Showing

**Symptoms**: SonarQube shows 0% coverage despite running tests

**Solutions**:

1. Verify JaCoCo plugin is configured in `pom.xml`:

```bash
grep -A 20 "jacoco-maven-plugin" */pom.xml
```

2. Check coverage file exists:

```bash
find . -name "jacoco.xml"
```

3. Verify report path matches configuration:

```bash
mvn clean verify
mvn sonar:sonar -X | grep jacoco
```

4. Ensure tests run before analysis:

```bash
mvn clean verify sonar:sonar
```

### Issue: Module Not Found in Multi-Module Project

**Symptoms**: Some modules don't appear in SonarQube

**Solutions**:

1. Verify module list in `sonar-project.properties`:

```properties
sonar.modules=common,product-service,order-service,user-service,api-gateway
```

2. Check module source paths:

```properties
common.sonar.projectBaseDir=common
common.sonar.sources=src/main/java
```

3. Ensure all modules have valid `pom.xml`

### Issue: Docker Compose Permission Denied

**Symptoms**: `permission denied while trying to connect to Docker daemon`

**Solutions**:

1. Add user to docker group:

```bash
sudo usermod -aG docker $USER
newgrp docker
```

2. Restart Docker service:

```bash
sudo systemctl restart docker
```

3. Verify Docker is running:

```bash
docker ps
```

---

## Additional Resources

- [SonarQube Documentation](https://docs.sonarqube.org/latest/)
- [SonarLint Documentation](https://www.sonarlint.org/docs/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

## Support

For issues or questions:

1. Check this documentation
2. Review SonarQube logs: `docker-compose logs -f sonarqube`
3. Consult [SonarQube Community](https://community.sonarsource.com/)
4. Contact SafeZone development team

---

**Last Updated**: January 2025  
**Version**: 1.0  
**Maintained By**: SafeZone Development Team
