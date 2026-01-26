# Hide Chat Snapshot Problems (Workspace helper)

This tiny extension toggles a workspace setting to hide `Problems` that originate from chat-editing snapshot URIs, e.g. `chat-editing-snapshot-*`.

Why: the VS Code Problems panel aggregates diagnostics from in-memory snapshots created by chat agents (e.g., GitHub Copilot Chat). Those snapshots can occasionally contain transient invalid content which produces noisy/irrelevant diagnostics. This helper hides such diagnostics non-invasively by updating `problems.exclude` for the workspace.

Usage:

- Install locally by loading the folder as an extension in your VS Code (see `Extension Development Host`) or add it to the workspace extensions (developer).
- The extension will enable the hide behavior on activation.
- Use the command `Hide Chat Snapshot Problems (toggle)` to toggle visibility.

Notes:

- This is a local/workspace helper — it only changes the workspace settings and is reversible.
- It does not alter your files nor suppress real diagnostics in your actual files on disk.
