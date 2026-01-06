# IDE Integration Guide

## SonarLint Integration

SonarLint provides real-time feedback on code quality issues directly in your IDE.

## VS Code Setup

### 1. Install SonarLint Extension

1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search for "SonarLint"
4. Install "SonarLint" by SonarSource

### 2. Configure SonarQube Connection

The project includes pre-configured settings in `.vscode/settings.json`:

```json
{
  "sonarlint.connectedMode.connections.sonarqube": [
    {
      "connectionId": "safezone-sonarqube",
      "serverUrl": "http://localhost:9000",
      "token": "${env:SONAR_TOKEN}"
    }
  ],
  "sonarlint.connectedMode.project": {
    "connectionId": "safezone-sonarqube",
    "projectKey": "safe-zone"
  }
}
```

### 3. Set Environment Variable

```bash
# Add to ~/.bashrc or ~/.zshrc
export SONAR_TOKEN="your-sonarqube-token"
```

### 4. Bind Project

1. Open Command Palette (Ctrl+Shift+P)
2. Run "SonarLint: Update all project bindings"

## IntelliJ IDEA Setup

### 1. Install SonarLint Plugin

1. Go to File > Settings > Plugins
2. Search for "SonarLint"
3. Install and restart IDE

### 2. Configure SonarQube Server

1. Go to File > Settings > Tools > SonarLint
2. Click "+" to add a connection
3. Configure:
   - Name: `safezone-sonarqube`
   - Server URL: `http://localhost:9000`
   - Authentication: Use token

### 3. Bind Project

1. Right-click on project in Project Explorer
2. Select "SonarLint > Bind to SonarQube or SonarCloud"
3. Select the connection and project key

### 4. Import Project Settings

The project includes `.idea/sonarlint.xml` with pre-configured bindings.

## Eclipse Setup

### 1. Install SonarLint Plugin

1. Go to Help > Eclipse Marketplace
2. Search for "SonarLint"
3. Install and restart

### 2. Configure Server Connection

1. Go to Window > Preferences > SonarLint > Connected Mode
2. Add new connection to `http://localhost:9000`

### 3. Bind Project

1. Right-click project > SonarLint > Bind to a SonarQube server
2. Select connection and project

## Recommended Extensions/Plugins

### VS Code

Extensions are pre-configured in `.vscode/extensions.json`:

- **SonarLint** - Code quality analysis
- **Java Extension Pack** - Java development support
- **Spring Boot Extension Pack** - Spring Boot support
- **GitLens** - Git integration
- **Error Lens** - Inline error display
- **Docker** - Docker support

### IntelliJ IDEA

- **SonarLint** - Code quality
- **Spring Boot Assistant** - Spring support
- **Lombok** - Lombok support
- **CheckStyle-IDEA** - Code style checking
- **SpotBugs** - Bug detection

## Code Quality Settings

### Auto-formatting on Save

#### VS Code

Already configured in `.vscode/settings.json`:

```json
{
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": "explicit"
  }
}
```

#### IntelliJ IDEA

1. Go to Settings > Tools > Actions on Save
2. Enable "Reformat code" and "Optimize imports"

### Code Style

#### VS Code

Install "Checkstyle for Java" extension.

#### IntelliJ IDEA

Import Google Java Style:

1. Settings > Editor > Code Style > Java
2. Import from `google-java-format`

## Troubleshooting

### SonarLint Not Connecting

1. Verify SonarQube is running
2. Check token is valid
3. Verify network connectivity
4. Check IDE console for errors

### Rules Not Syncing

1. Update project bindings
2. Clear SonarLint cache
3. Restart IDE

### Performance Issues

1. Exclude large generated files
2. Limit analysis scope
3. Increase IDE memory

## Best Practices

1. **Review Issues Before Commit**

   - Check SonarLint panel before committing
   - Fix all Critical and Blocker issues

2. **Use Quick Fixes**

   - SonarLint provides quick fixes for many issues
   - Use Alt+Enter (IntelliJ) or Ctrl+. (VS Code)

3. **Understand the Rules**

   - Click on issue to see rule description
   - Learn why the rule exists

4. **Configure Rule Severity**

   - Customize rules in IDE settings if needed
   - Keep in sync with SonarQube server

5. **Regular Binding Updates**
   - Update bindings when server rules change
   - Sync weekly or after quality profile changes
