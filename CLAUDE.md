# CLAUDE.md

## Project Identity

- **Project**: KiotRetail - Hệ thống quản lý cửa hàng bán lẻ
- **Type**: Maven Java WAR web application
- **Runtime**: Apache Tomcat + Jakarta Servlet/JSP
- **Architecture**: Layered MVC (JSP views, servlet controllers, DAO-based JDBC, MySQL via Docker)
- **Primary package**: `com.kiotretail`

## Harness (MANDATORY)

This project uses [Harness](https://github.com/hoangnb24/harness-experimental) as the agent operating model. Every AI agent MUST follow the Harness workflow before changing code.

### Before Any Work

1. Read `AGENTS.md` — the full operating contract
2. Read `docs/FEATURE_INTAKE.md` — classify the request
3. Read `docs/CONTEXT_RULES.md` — know what to read per phase and lane

### Task Loop (Every Task)

```
Intake -> Planning -> Implementation -> Validation -> Trace
```

1. **Intake**: Classify input type and risk lane (tiny/normal/high-risk)
2. **Planning**: Find affected docs, stories, and validation expectations
3. **Implementation**: Work only within the selected lane scope
4. **Validation**: Run `mvn test` or `mvn clean package`. Do not claim done without proof.
5. **Trace**: State what changed, what was not attempted, and any friction found

### Lane Selection (MANDATORY)

| Risk Flags | Lane | Action |
|---|---|---|
| 0-1 flags | Tiny | Patch directly, run quick checks |
| 2-3 flags | Normal | Create/update story in `docs/stories/`, add validation |
| 4+ flags or hard gate | High-Risk | Use `docs/templates/high-risk-story/`, ask human confirmation |

### Hard Gates (Always High-Risk)

- Auth (login, sessions, JWT, passwords)
- Authorization (roles, permissions, tenant scope)
- Data loss or migration (schema changes, DELETE operations)
- Audit/security
- External provider behavior (email, payment, cloud APIs)
- Removing or weakening validation requirements

### Context Budget

| Lane | Harness Context | What to Read |
|---|---|---|
| Tiny | ~2K tokens | AGENTS.md, FEATURE_INTAKE.md, affected file only |
| Normal | ~5K tokens | + product/story docs, ARCHITECTURE.md, validation expectations |
| High-Risk | ~10K tokens | + decisions, high-risk templates, full validation docs |

### Growth Rule

When confused, repeating reasoning, or discovering missing rules: either fix the harness directly or record friction in `docs/HARNESS_BACKLOG.md`.

### Done Definition

A task is NOT done until:
- Change is completed or blocker is documented
- Relevant docs and stories remain current
- Validation commands were run (not skipped)
- Final response states what changed and what was not attempted

## Build & Validation

```bash
# Quick check (compile + unit tests)
mvn test

# Full package (servlet/JSP changes)
mvn clean package

# Skip tests only when explicitly told
mvn clean package -DskipTests

# Run on Tomcat (port 9999)
mvn cargo:run -Dcargo.servlet.port=9999
```

## Local Server

- **Port**: 9999
- **URL**: http://localhost:9999/kiotretail
- **Run command**: `mvn cargo:run -Dcargo.servlet.port=9999`

## Database (MySQL via Docker)

- **Engine**: MySQL 8.0+
- **Database**: `DBFinora`
- **Host/Port**: `localhost:3306`
- **User/Password**: `root` / `root`
- **JDBC URL**: `jdbc:mysql://localhost:3306/DBFinora?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh&useSSL=false&allowPublicKeyRetrieval=true`
- **Config file**: `src/main/java/com/kiotretail/shared/util/DatabaseUtil.java`

### Container expectations

- Container name: `mysql-kiotretail` (default — adjust if user uses another)
- Port mapping: `3306:3306`
- Schema source: `sql/DBFinora.sql` (+ optional `sql/DBFinora_Extension.sql`)

### Health check (run before any DB-dependent task)

```bash
# 1. Container running?
docker ps --filter "name=mysql" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 2. MySQL accepting connections?
docker exec mysql-kiotretail mysqladmin ping -uroot -proot

# 3. Database exists?
echo "SHOW DATABASES LIKE 'DBFinora';" | docker exec -i mysql-kiotretail mysql -uroot -proot

# 4. Schema loaded? (table count > 0)
echo "SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema='DBFinora';" | docker exec -i mysql-kiotretail mysql -uroot -proot
```

### Common operations

```bash
# Start / stop
docker start mysql-kiotretail
docker stop mysql-kiotretail

# Tail logs (debug startup / connection issues)
docker logs --tail 100 -f mysql-kiotretail

# Open MySQL shell inside container
docker exec -it mysql-kiotretail mysql -uroot -proot DBFinora

# Run an ad-hoc query (use stdin pipe to avoid Windows backtick escaping issues)
echo "SELECT COUNT(*) FROM Employee;" | docker exec -i mysql-kiotretail mysql -uroot -proot DBFinora

# Reload schema from host file
docker exec -i mysql-kiotretail mysql -uroot -proot < sql/DBFinora.sql
```

### First-time setup (if container does not exist yet)

```bash
docker run -d \
  --name mysql-kiotretail \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=DBFinora \
  -p 3306:3306 \
  --restart unless-stopped \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci

# Wait until ready, then load schema
docker exec -i mysql-kiotretail mysql -uroot -proot < sql/DBFinora.sql
```

### Troubleshooting checklist

- `Connection refused` → container stopped → `docker start mysql-kiotretail`
- `Access denied for user 'root'` → password mismatch with `DatabaseUtil.java` (USER/PASSWORD constants)
- `Unknown database 'DBFinora'` → schema not loaded → re-run `sql/DBFinora.sql`
- `Public Key Retrieval is not allowed` → URL missing `allowPublicKeyRetrieval=true` (already set)
- Port conflict on 3306 → another MySQL on host → stop it OR remap container port and update `DatabaseUtil.java`

### Backtick escaping on Windows (IMPORTANT)

When running queries with backtick-quoted column names through `docker exec`, **use stdin pipe** (`echo ... | docker exec -i ...`) instead of `-e "..."`. The `-e` flag triggers multiple shell-escaping layers on Windows that corrupt backticks.

## Key Directories

```
src/main/java/com/kiotretail/   # Java source
src/main/webapp/WEB-INF/views/  # JSP views
sql/                            # Schema scripts
docs/                           # Governance docs + Harness
docs/stories/                   # Story packets
docs/decisions/                 # Architecture decisions
docs/templates/                 # Story/validation templates
scripts/                        # Harness entrypoint
```

## GitHub Project Board

- **Project ID**: `PVT_kwHOBH3C084BZTH_`
- **Owner**: `dikhamchua`
- **Project Number**: 4
- **Status Field ID**: `PVTSSF_lAHOBH3C084BZTH_zhUSvyQ`
- **Status Options**:
  - Todo: `f75ad846`
  - In progress: `47fc9ee4`
  - Done: `98236657`

### Update issue status on Project board

```bash
# Move an item to "In progress"
gh project item-edit --project-id PVT_kwHOBH3C084BZTH_ --id <ITEM_ID> --field-id PVTSSF_lAHOBH3C084BZTH_zhUSvyQ --single-select-option-id 47fc9ee4

# Move an item to "Done"
gh project item-edit --project-id PVT_kwHOBH3C084BZTH_ --id <ITEM_ID> --field-id PVTSSF_lAHOBH3C084BZTH_zhUSvyQ --single-select-option-id 98236657
```

### Find item IDs

```bash
gh project item-list 4 --owner dikhamchua --format json --limit 30 | python -c "
import json, sys
data = json.load(sys.stdin)
for item in data['items']:
    print(f\"{item['id']} | #{item['content'].get('number','')} | {item['status']} | {item['title'][:60]}\")
"
```

## Engineering Rules

- Smallest correct change wins
- No SQL in servlets, JSPs, filters, or API actions
- No request/session objects in DAOs
- No hardcoded secrets or credentials
- Reuse existing DAO/model/controller patterns before adding new abstractions
- Keep methods small enough to review and test
- Update documentation when architecture, boundaries, or patterns change

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **SWP391_Group_5** (2077 symbols, 5481 relationships, 171 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/SWP391_Group_5/context` | Codebase overview, check index freshness |
| `gitnexus://repo/SWP391_Group_5/clusters` | All functional areas |
| `gitnexus://repo/SWP391_Group_5/processes` | All execution flows |
| `gitnexus://repo/SWP391_Group_5/process/{name}` | Step-by-step execution trace |

## CLI

| Task | Read this skill file |
|------|---------------------|
| Understand architecture / "How does X work?" | `.claude/skills/gitnexus/gitnexus-exploring/SKILL.md` |
| Blast radius / "What breaks if I change X?" | `.claude/skills/gitnexus/gitnexus-impact-analysis/SKILL.md` |
| Trace bugs / "Why is X failing?" | `.claude/skills/gitnexus/gitnexus-debugging/SKILL.md` |
| Rename / extract / split / refactor | `.claude/skills/gitnexus/gitnexus-refactoring/SKILL.md` |
| Tools, resources, schema reference | `.claude/skills/gitnexus/gitnexus-guide/SKILL.md` |
| Index, status, clean, wiki CLI commands | `.claude/skills/gitnexus/gitnexus-cli/SKILL.md` |

<!-- gitnexus:end -->
