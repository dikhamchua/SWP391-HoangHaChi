# CLAUDE.md

> Quick reference for Claude Code. The full operating contract lives in AGENTS.md.

@AGENTS.md

## Project at a glance

- **Project**: KiotRetail — Maven Java WAR webapp (Tomcat + Servlet/JSP + MySQL Docker)
- **Primary package**: `com.kiotretail`
- **Architecture**: Modular Monolith with domain packages, layered MVC (Controller -> Service -> DAO -> Model)

## Build & validation commands

```bash
mvn test                                  # Quick check (compile + unit tests)
mvn clean package                         # Full package (servlet/JSP/web.xml changes)
mvn clean package -DskipTests             # Skip tests only when explicitly told
mvn cargo:run -Dcargo.servlet.port=9999   # Run on Tomcat -> http://localhost:9999/kiotretail
```

A task is NOT done until `mvn test` (or `mvn clean package` for web changes) was run with proof.

## Database

MySQL 8 in Docker, container `mysql-kiotretail`, db `DBFinora`, user/pass `root/root`.
Config file: `src/main/java/com/kiotretail/shared/util/DatabaseUtil.java`.

For health check, schema reload, troubleshooting, and Windows backtick escaping: load skill **mysql-health-check** (`.claude/skills/mysql-health-check/SKILL.md`).

## Hard rules (non-negotiable)

- NEVER put SQL in servlets, JSPs, filters, or API actions
- NEVER access `HttpServletRequest`/`HttpSession` from DAOs or services
- NEVER hardcode secrets, passwords, tokens, or production credentials
- NEVER edit `target/` (build artifact)
- NEVER rename symbols with find-and-replace — use `gitnexus_rename`
- ALWAYS run `gitnexus_impact` before editing a function/class/method
- ALWAYS run `mvn test` before claiming a task done

Full architecture contract, layer rules, code standards, protected areas, and Harness task loop -> see AGENTS.md (imported above).

## Rule loading order

1. **AGENTS.md** (imported) — operating contract, architecture, code standards, Harness, GitNexus
2. **`.claude/rules/*.md`** — modular rules (path-scoped where applicable)
3. **`.claude/skills/*/SKILL.md`** — procedures loaded on-demand

## Skills available

| Skill | Use when |
|---|---|
| `mysql-health-check` | Verify MySQL container, reload schema, debug connection |
| `gh-project-board` | Update issue status on the SWP391 Project board |
| `gitnexus-exploring` | "How does X work?" — find execution flows |
| `gitnexus-impact-analysis` | "What breaks if I change X?" — blast radius |
| `gitnexus-debugging` | "Why is X failing?" — trace bugs |
| `gitnexus-refactoring` | Rename / extract / split / refactor safely |
| `gitnexus-guide` | Tools, resources, schema reference |
| `gitnexus-cli` | Index, status, clean, wiki CLI commands |

## Key directories

```
src/main/java/com/kiotretail/   # Java source (domain packages)
src/main/webapp/WEB-INF/views/  # JSP views (domain folders + common/)
sql/                            # Schema scripts
docs/                           # Governance: harness, stories, decisions, templates
docs/stories/                   # Story packets (per Harness lane)
docs/decisions/                 # Architecture decision records
docs/templates/                 # Story/validation templates
.claude/rules/                  # Modular rules (path-scoped)
.claude/skills/                 # Procedures load on-demand
```

## Windows + PowerShell quirks

- Bash tool runs Git Bash on Windows; use POSIX paths inside scripts.
- For MySQL queries with backticks, always use stdin pipe (`echo ... | docker exec -i ...`), never `-e "..."`. See **mysql-health-check** skill for details.
- Avoid `2>$null` in Bash — that is PowerShell syntax. Use `2>/dev/null` instead.
