# Code Review Process

## Overview

This document outlines the code review process for the SafeZone project to ensure code quality and security standards are met.

## Pull Request Workflow

### 1. Before Creating a PR

- [ ] All tests pass locally
- [ ] Code follows project coding standards
- [ ] No new SonarQube Critical/Blocker issues
- [ ] Coverage is maintained at ≥80%
- [ ] Self-review completed
- [ ] Commits are atomic and well-described

### 2. Creating a Pull Request

1. Create a feature branch from `develop`:

   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```

2. Make changes and commit:

   ```bash
   git add .
   git commit -m "feat: description of changes"
   ```

3. Push and create PR:

   ```bash
   git push -u origin feature/your-feature-name
   ```

4. Fill out the PR template completely

### 3. Automated Checks

The following checks run automatically:

| Check                  | Requirement                 |
| ---------------------- | --------------------------- |
| Build                  | Must pass                   |
| Unit Tests             | All must pass               |
| Integration Tests      | All must pass               |
| Code Coverage          | ≥80%                        |
| SonarQube Quality Gate | Must pass                   |
| Security Scan          | No critical vulnerabilities |

### 4. Review Requirements

- Minimum 1 approval required
- All automated checks must pass
- All review comments must be resolved
- CODEOWNERS approval required for protected areas

## Code Review Guidelines

### For Authors

1. **Keep PRs Small**

   - Maximum 400 lines of changed code
   - Single responsibility per PR
   - Split large features into smaller PRs

2. **Write Descriptive Commit Messages**

   ```
   feat(product): add search functionality

   - Implement full-text search for products
   - Add pagination support
   - Include category filtering

   Closes #123
   ```

3. **Respond to Feedback Promptly**
   - Address all comments
   - Explain reasoning when not accepting suggestions
   - Request re-review after changes

### For Reviewers

1. **Review Checklist**

   **Functionality**

   - [ ] Code does what it's supposed to do
   - [ ] Edge cases are handled
   - [ ] Error handling is appropriate

   **Code Quality**

   - [ ] Code is readable and maintainable
   - [ ] No code duplication
   - [ ] SOLID principles followed
   - [ ] Appropriate design patterns used

   **Security**

   - [ ] No security vulnerabilities
   - [ ] Input validation implemented
   - [ ] Authentication/authorization correct
   - [ ] Sensitive data not exposed

   **Testing**

   - [ ] Tests cover the changes
   - [ ] Tests are meaningful
   - [ ] Edge cases tested

   **Performance**

   - [ ] No obvious performance issues
   - [ ] Database queries optimized
   - [ ] Caching considered where appropriate

2. **Providing Feedback**

   Use conventional comment prefixes:

   - `[BLOCKER]` - Must be fixed before merging
   - `[MUST]` - Required change
   - `[SHOULD]` - Strongly recommended
   - `[COULD]` - Optional improvement
   - `[QUESTION]` - Request for clarification
   - `[NIT]` - Minor nitpick

3. **Review Timeline**
   - Initial review within 24 hours
   - Follow-up reviews within 4 hours
   - Don't let PRs sit for more than 48 hours

## Branch Protection Rules

### Main Branch (`main`)

- Require PR reviews (minimum 1)
- Require status checks to pass
- Require conversation resolution
- No direct pushes
- Require linear history

### Develop Branch (`develop`)

- Require PR reviews (minimum 1)
- Require status checks to pass
- Allow squash merging only

## CODEOWNERS

The project uses CODEOWNERS to automatically assign reviewers:

```
# Service-specific owners
/product-service/ @safezone/product-team
/order-service/ @safezone/order-team
/user-service/ @safezone/user-team

# Security-sensitive code
**/security/** @safezone/security-team

# Infrastructure
/docker/ @safezone/devops-team
/.github/ @safezone/devops-team
```

## Quality Gate Criteria

### SonarQube Quality Gate

| Metric                     | Threshold        |
| -------------------------- | ---------------- |
| New Bugs                   | 0                |
| New Vulnerabilities        | 0                |
| New Code Smells (A rating) | ≤1 per 100 lines |
| New Coverage               | ≥80%             |
| New Duplicated Lines       | <3%              |
| New Security Hotspots      | All reviewed     |

### Definition of Done

- [ ] Feature implemented as specified
- [ ] Unit tests written and passing
- [ ] Integration tests written and passing
- [ ] Documentation updated
- [ ] Code reviewed and approved
- [ ] Quality gate passed
- [ ] No merge conflicts
- [ ] Deployed to staging (if applicable)

## Merge Strategy

1. **Squash and Merge** - For feature branches

   - Combines all commits into one
   - Clean, linear history

2. **Merge Commit** - For release branches
   - Preserves full history
   - Clear release boundaries

## Handling Review Feedback

### Disagreements

1. Discuss in PR comments
2. If unresolved, schedule a quick call
3. Escalate to tech lead if needed
4. Document final decision

### Continuous Improvement

- Monthly review of process effectiveness
- Retrospectives after major releases
- Update guidelines based on learnings

## Emergency Procedures

### Hotfix Process

1. Create hotfix branch from `main`
2. Make minimal fix
3. Get expedited review (single approval)
4. Merge to `main` and back-merge to `develop`

### Bypassing Checks (Emergency Only)

Only with:

- Tech lead approval
- Written justification
- Follow-up PR to address issues
