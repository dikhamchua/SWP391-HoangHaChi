#!/usr/bin/env bash
# Pre-commit guard: block JSP XSS / pagination smell patterns.
# Exits 1 if any high-risk pattern is found in tracked JSP files.
#
# Patterns checked (see docs/HARNESS_BACKLOG.md #6, #11):
#   1. Pagination baseUrl built from raw EL (allows reflected XSS + URL corruption).
#   2. <input value="${...}"> without <c:out> wrap (HTML attribute injection).
#
# To run manually:
#   bash scripts/check-jsp-xss.sh
set -e

REPO_ROOT="$(git rev-parse --show-toplevel)"
cd "$REPO_ROOT"

FAIL=0

JSP_FILES="$(git ls-files '*.jsp' 2>/dev/null || true)"
if [ -z "$JSP_FILES" ]; then
  echo "check-jsp-xss: no JSP files tracked, skipping."
  exit 0
fi

# 1) Pagination baseUrl built with raw EL — must use <c:url><c:param/></c:url>.
#    Match: <c:set var="baseUrl" value="...?keyword=${something}..." />
#    Allow: <c:url var="baseUrl" ...> ... <c:param ... /> ... </c:url>
PAGINATION_HITS="$(printf '%s\n' "$JSP_FILES" \
  | xargs -r grep -nE 'c:set[^>]+var="baseUrl"[^>]+value="[^"]*\?[^"]*\$\{' 2>/dev/null || true)"
if [ -n "$PAGINATION_HITS" ]; then
  echo "ERROR: pagination baseUrl built with raw EL (XSS / URL corruption risk)." >&2
  echo "$PAGINATION_HITS" >&2
  echo "FIX: replace with <c:url var=\"baseUrl\" value=\"...\"><c:param name=\"...\" value=\"\${...}\"/></c:url>." >&2
  echo "See docs/HARNESS_BACKLOG.md #6." >&2
  FAIL=1
fi

# 2) <input value="${...}"> without <c:out> — HTML attribute injection.
#    Match: value="${...}" anywhere on an <input ... line.
#    Skip:  value="<c:out value='${...}'/>" (already escaped).
#    Skip:  value="${entity.somethingId}" / value="${entity.id}" — numeric PK, safe.
INPUT_HITS="$(printf '%s\n' "$JSP_FILES" \
  | xargs -r grep -nE '<input[^>]*value="\$\{[^}]+\}"' 2>/dev/null \
  | grep -vE 'value="\$\{[A-Za-z_][A-Za-z0-9_]*\.(id|[A-Za-z]+Id)\}"' || true)"
if [ -n "$INPUT_HITS" ]; then
  echo "ERROR: <input value=\"\${...}\"> without c:out wrap (XSS attribute risk)." >&2
  echo "$INPUT_HITS" >&2
  echo "FIX: change to value=\"<c:out value='\${...}'/>\" or use fn:escapeXml." >&2
  echo "See docs/HARNESS_BACKLOG.md #11." >&2
  FAIL=1
fi

if [ "$FAIL" -ne 0 ]; then
  exit 1
fi

echo "check-jsp-xss: OK"
exit 0
