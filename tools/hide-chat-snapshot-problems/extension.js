const vscode = require("vscode");

const PATTERN = "**/chat-editing-snapshot-*";
const SETTING_KEY = "problems.exclude";

function getProblemsExclude() {
  const cfg = vscode.workspace.getConfiguration();
  return cfg.get(SETTING_KEY) || {};
}

async function setProblemsExclude(map) {
  const cfg = vscode.workspace.getConfiguration();
  await cfg.update(SETTING_KEY, map, vscode.ConfigurationTarget.Workspace);
}

function isHidden(map) {
  return map && (map[PATTERN] === true || map[PATTERN] === "true");
}

async function activate(context) {
  // On activation, enable the hide behavior if not already set
  let map = getProblemsExclude();
  if (!isHidden(map)) {
    map = Object.assign({}, map);
    map[PATTERN] = true;
    await setProblemsExclude(map);
  }

  let disposable = vscode.commands.registerCommand(
    "hideChatSnapshotProblems.toggle",
    async () => {
      let map = getProblemsExclude();
      map = Object.assign({}, map);
      if (isHidden(map)) {
        delete map[PATTERN];
        await setProblemsExclude(map);
        vscode.window.showInformationMessage(
          "Chat snapshot diagnostics are now visible (toggle off).",
        );
      } else {
        map[PATTERN] = true;
        await setProblemsExclude(map);
        vscode.window.showInformationMessage(
          "Chat snapshot diagnostics are now hidden (toggle on).",
        );
      }
    },
  );

  context.subscriptions.push(disposable);
}

function deactivate() {}

module.exports = {
  activate,
  deactivate,
};
