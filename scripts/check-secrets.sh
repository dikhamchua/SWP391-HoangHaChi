#!/usr/bin/env bash
# Pre-commit guard: block secrets / hardcoded credentials in source.
# Exits 1 if any blacklisted pattern is found in tracked files.
set -e

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

FAIL=0

# Block context-param holding db credentials in any web.xml.
if git ls-files '*web.xml' | xargs -r grep -nE '<param-name>db\.(url|username|password)</param-name>' 2>/dev/null; then
  echo "ERROR: db.* context-param detected in web.xml. Move to JNDI / env. See HRS-001." >&2
  FAIL=1
fi

# Block hardcoded credentials in DatabaseUtil.
if git ls-files 'src/main/java/**/DatabaseUtil.java' 2>/dev/null | xargs -r grep -nE 'private static final String (PASSWORD|USERNAME|URL)\s*=\s*"' 2>/dev/null; then
  echo "ERROR: hardcoded DB credentials detected in DatabaseUtil.java." >&2
  FAIL=1
fi

# Block .env files from being staged.
if git diff --cached --name-only | grep -E '^\.env$' >/dev/null 2>&1; then
  echo "ERROR: .env file is staged. Add it to .gitignore and unstage." >&2
  FAIL=1
fi

if [ "$FAIL" -ne 0 ]; then
  exit 1
fi

echo "check-secrets: OK"
exit 0
