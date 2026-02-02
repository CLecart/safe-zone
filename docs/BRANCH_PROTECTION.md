Branch Protection Recommendations

Objective: Reduce the chance of conflicts and ensure CI/formatting checks run before merging.

Recommended settings (GitHub UI > Settings > Branches > Branch protection rules):

- Branch name pattern: main
- Require pull request reviews before merging: ON (1+ approvals)
- Require status checks to pass before merging: ON
  - Add `build-test` and `sonar` (or the specific check names shown in PR checks)
- Require branches to be up to date before merging: ON (this forces a rebase/merge with main before merge)
- Include administrators: ON (enforce for admins to avoid accidental bypass)
- Require linear history: Optional but recommended to avoid merge commits
- Restrict who can push to matching branches: Optional (limit direct pushes to a small set of users/automation)

Apply via `gh` CLI (requires admin permissions and `gh auth login`):

1. Create a rule via API (example):

```
gh api -X POST /repos/:owner/:repo/branches/main/protection --raw-field 'required_status_checks.contexts=["build-test","sonarExpected"]' \
  --raw-field 'enforce_admins=true' --raw-field 'required_pull_request_reviews.dismiss_stale_reviews=true'
```

2. Or configure using the GitHub UI under Settings > Branches.

Notes:

- After these protections, PRs must be up to date and checks green to be merged, which prevents the "conflicts after formatting" problem.
- Also consider enabling `code owners` to require specific reviewer approvals for security-sensitive modules.

If you want, I can attempt to apply these settings automatically using the GitHub API if you provide an admin PAT with `repo` and `admin:repo_hook` scopes. Otherwise, follow the UI steps above.
