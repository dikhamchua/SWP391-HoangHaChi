# Best Practices for Claude Code Rules & AI Agent Instructions

> Báo cáo nghiên cứu chuyên sâu cho dự án KiotRetail (SWP391_Group_5)
> Ngày: 2026-06-02 | Tác giả: researcher (subagent)
> Phạm vi: Cấu trúc CLAUDE.md, AGENTS.md, .claude/rules/, skills, hooks, và các pattern tổ chức rule cho AI coding agents

---

## 0. Tóm tắt nhanh (TL;DR)

Sau khi đối chiếu doc chính thức của Anthropic (Claude Code), spec AGENTS.md, Cursor Rules MDC, và rà soát repo KiotRetail hiện tại:

- **`CLAUDE.md` của repo đang dài 269 dòng**, vượt mức khuyến nghị chính thức của Anthropic (**< 200 dòng**). Cần tách bớt ra `.claude/rules/` hoặc skills.
- **`AGENTS.md` dài 355 dòng**, chứa nhiều thông tin trùng với `CLAUDE.md` (Project Identity, Architecture Contract, Engineering Rules). Vi phạm DRY thì khi update phải sửa 2 chỗ thì dễ stale.
- **Claude Code KHÔNG đọc `AGENTS.md` trực tiếp** thì phải import qua `@AGENTS.md` trong `CLAUDE.md`. Hiện tại repo chưa import thì nội dung AGENTS.md không vào context Claude.
- Harness methodology (Intake -> Planning -> Implementation -> Validation -> Trace) là thiết kế tốt, nhưng đang để dạng prose dài thì nên rút thành **decision table** ngắn + skill `harness-intake` cho phần procedure.
- Đề xuất: chuyển sang mô hình **3-tier**: `CLAUDE.md` (< 150 dòng, facts) -> `.claude/rules/*.md` (path-scoped) -> `.claude/skills/*` (procedures load on-demand).

---

## 1. Claude Code official best practices for CLAUDE.md

### 1.1 Memory hierarchy (load order: broad -> specific)

Thứ tự load của Claude Code (broadest scope first, specific last giúp instruction cụ thể "thắng" instruction chung):

| Scope | Location | Mục đích | Share với |
|---|---|---|---|
| Managed policy | `/Library/Application Support/ClaudeCode/CLAUDE.md` (mac), `/etc/claude-code/CLAUDE.md` (Linux), `C:\Program Files\ClaudeCode\CLAUDE.md` (Win) | Org-wide policies, không thể bị override | Tất cả user trong org |
| User instructions | `~/.claude/CLAUDE.md` | Personal preferences áp dụng mọi project | Chỉ bạn |
| Project instructions | `./CLAUDE.md` HOẶC `./.claude/CLAUDE.md` | Team-shared | Team qua git |
| Local instructions | `./CLAUDE.local.md` | Personal project notes (gitignore) | Chỉ bạn ở project này |
| Subdirectory CLAUDE.md | `src/billing/CLAUDE.md` | On-demand khi Claude đọc file trong dir đó | Team |

**Recursive lookup**: Khi chạy Claude Code trong `foo/bar/`, nó walk up directory tree -> load `foo/bar/CLAUDE.md`, `foo/CLAUDE.md`, và mọi `CLAUDE.local.md` cùng cấp. Tất cả được **concatenated** (không override). Subdirectory files **load on-demand** khi Claude đọc file trong subdir đó.

**Lưu ý quan trọng**: Block-level HTML comments được **strip** trước khi inject context, dùng để ghi chú cho human maintainer mà không tốn token.

### 1.2 Import syntax `@path/to/file`

CLAUDE.md có thể import file khác bằng `@path` syntax. File imported được expand và load vào context tại launch (không phải lazy).

```markdown
See @README.md for project overview and @package.json for npm commands.

# Additional Instructions
- Git workflow: @docs/git-instructions.md
- Personal overrides: @~/.claude/my-project-instructions.md
```

- Path tương đối resolve theo file chứa import (không phải CWD).
- Imported files có thể recursive import. **Max depth 4 hops**.
- Lần đầu Claude gặp external import sẽ hiện approval dialog, user phải accept.

### 1.3 Token budget recommendation (Anthropic chính thức)

| Item | Recommendation |
|---|---|
| **CLAUDE.md size** | Target **< 200 dòng** mỗi file |
| **Auto memory MEMORY.md** | Chỉ **first 200 lines hoặc 25KB** load vào session |
| Imported files | Load **toàn bộ** kể cả dài. Import không giảm token cost |
| Path-scoped rules (`.claude/rules/*.md` với `paths:`) | Load **on-demand** khi Claude đọc file matching glob |
| Skills (`.claude/skills/*/SKILL.md`) | Chỉ frontmatter `name`+`description` load luôn; body load khi invoke |

**Quy tắc vàng**: "Would removing this cause Claude to make mistakes? If not, cut it." Bloated CLAUDE.md sẽ làm Claude **ignore actual instructions** vì rule quan trọng bị "lost in noise".

### 1.4 What to INCLUDE vs EXCLUDE trong CLAUDE.md

| Include | Exclude |
|---|---|
| Bash commands Claude không thể đoán (build, test, run) | Anything Claude figure out by reading code |
| Code style rules khác default | Standard language conventions Claude đã biết |
| Test runners preference (npm vs pnpm, mvn vs gradle) | Detailed API documentation (link to docs instead) |
| Repository etiquette (branch naming, PR format) | Information that changes frequently |
| Architectural decisions specific cho project | Long explanations or tutorials |
| Dev environment quirks (required env vars) | File-by-file descriptions of codebase |
| Common gotchas, non-obvious behaviors | Self-evident practices ("write clean code") |

### 1.5 `/init` command

Run `/init` để Claude tự generate CLAUDE.md draft từ codebase. Nếu repo có `AGENTS.md`, `.cursorrules`, `.windsurfrules` thì `/init` đọc và incorporate vào CLAUDE.md.

`CLAUDE_CODE_NEW_INIT=1` enable interactive multi-phase flow: hỏi setup CLAUDE.md/skills/hooks -> explore codebase qua subagent -> present reviewable proposal trước khi write.


---

## 2. AGENTS.md spec — universal alternative

### 2.1 AGENTS.md là gì?

[AGENTS.md](https://agents.md/) là format markdown **vendor-neutral** dành cho AI coding agents. 60k+ open-source projects dùng. Stewarded by Agentic AI Foundation (Linux Foundation).

**Tools support**: Codex, Jules, Cursor, Aider, goose, Zed, Warp, VS Code, Devin, Junie, Gemini CLI, GitHub Copilot coding agent, Windsurf...

**KHÔNG** trong list: Claude Code (Claude đọc CLAUDE.md, **không đọc AGENTS.md trực tiếp**).

### 2.2 Cách Claude Code tích hợp với AGENTS.md

Doc Anthropic chính thức:

> Claude Code reads `CLAUDE.md`, not `AGENTS.md`. If your repository already uses `AGENTS.md`, create a `CLAUDE.md` that imports it.

Có 2 cách:

**Cách 1 — Import (khuyến nghị)**:
```markdown
@AGENTS.md

## Claude Code

Use plan mode for changes under `src/billing/`.
```

**Cách 2 — Symlink** (Linux/Mac, hoặc Windows Developer Mode):
```bash
ln -s AGENTS.md CLAUDE.md
```

> Trên Windows, symlink yêu cầu Admin / Developer Mode, dùng `@AGENTS.md` import safer hơn.

### 2.3 Nested AGENTS.md cho monorepo

Spec hỗ trợ nested files — "the closest AGENTS.md to the edited file wins". OpenAI's main repo có 88 file AGENTS.md.

```
project/
  AGENTS.md              # Global
  frontend/
    AGENTS.md            # Frontend-specific
  backend/
    AGENTS.md            # Backend-specific
```

---

## 3. `.claude/rules/` — modular path-scoped rules

### 3.1 Cấu trúc

`.claude/rules/` là directory chứa nhiều file `.md` topic-specific. **Tất cả `.md` discovered recursively**. Mỗi file cover 1 topic, filename mô tả mục đích (`testing.md`, `api-design.md`, `security.md`).

```
your-project/
├── .claude/
│   ├── CLAUDE.md           # Main project instructions
│   └── rules/
│       ├── code-style.md   # Always loaded
│       ├── testing.md      # Always loaded
│       ├── api/
│       │   └── endpoints.md  # Path-scoped
│       └── security.md
```

### 3.2 Path-specific rules với YAML frontmatter

Rule **không có `paths` frontmatter** -> load mọi session (cùng priority với CLAUDE.md).
Rule **có `paths`** -> chỉ load khi Claude đọc file matching glob -> tiết kiệm context.

```markdown
---
paths:
  - "src/api/**/*.ts"
  - "src/handlers/**/*.ts"
---

# API Development Rules

- All API endpoints must include input validation
- Use the standard error response format
- Include OpenAPI documentation comments
```

Glob patterns:
| Pattern | Match |
|---|---|
| `**/*.ts` | All TS files anywhere |
| `src/**/*` | All under `src/` |
| `*.md` | Markdown trong root |
| `src/**/*.{ts,tsx}` | Brace expansion |

### 3.3 User-level rules

`~/.claude/rules/*.md` -> áp dụng mọi project. Load **trước project rules** -> project rules có higher priority.

### 3.4 Symlink shared rules

`.claude/rules/` support symlink -> maintain shared rule set across projects:

```bash
ln -s ~/shared-claude-rules .claude/rules/shared
ln -s ~/company-standards/security.md .claude/rules/security.md
```

---

## 4. Skills (`.claude/skills/`) — load on-demand

### 4.1 Khi nào dùng Skill thay vì CLAUDE.md / rules?

| Use case | Mechanism |
|---|---|
| Fact áp dụng broadly cho project | CLAUDE.md |
| Fact chỉ áp dụng cho dir/file pattern cụ thể | `.claude/rules/*.md` với `paths:` |
| Multi-step procedure / workflow / checklist | **Skill** |
| Có side effect cần manual trigger | Skill với `disable-model-invocation: true` |

> "Create a skill when you keep pasting the same instructions, checklist, or multi-step procedure into chat, OR khi 1 section trong CLAUDE.md đã grow thành procedure."

### 4.2 SKILL.md format

```markdown
---
name: harness-intake
description: Run Harness intake gate to classify request and choose lane (tiny/normal/high-risk)
---
# Harness Intake

1. Read user request, classify input type per docs/FEATURE_INTAKE.md
2. Run risk checklist (10 flags)
3. Determine lane:
   - 0-1 flags -> tiny (patch directly)
   - 2-3 flags -> normal (create story file)
   - 4+ or hard gate -> high-risk (full template)
4. State lane + reason + docs + story + validation expected
```

Gọi: `/harness-intake` HOẶC Claude tự load khi description match prompt.

**Progressive disclosure**: chỉ frontmatter `name`+`description` load luôn -> cost ~50 tokens/skill kể cả body 500 dòng.

### 4.3 Skills with side effects (manual-only)

```markdown
---
name: deploy-prod
description: Deploy to production environment
disable-model-invocation: true
---
1. Run mvn clean package -DskipTests=false
2. Verify build artifact in target/
3. SSH to prod server...
```

`disable-model-invocation: true` -> **chỉ user invoke bằng `/deploy-prod`**, Claude không tự gọi.

---

## 5. Hooks — deterministic enforcement

CLAUDE.md/rules/skills là **advisory** (Claude có thể ignore). Hooks là **deterministic** — script chạy 100% tại lifecycle event:

| Hook event | Use case |
|---|---|
| `PreToolUse` | Block specific tools/paths trước khi thực thi |
| `PostToolUse` | Run linter/formatter sau mỗi edit |
| `Stop` | Gate kết thúc turn — chạy test/build, block stop nếu fail |
| `InstructionsLoaded` | Debug xem rule files nào đã load |

Configure trong `.claude/settings.json`:

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          { "type": "command", "command": "mvn compile -q" }
        ]
      }
    ]
  }
}
```

> "Use hooks for actions that must happen every time with zero exceptions." Ví dụ rule "luôn chạy `mvn test` trước khi commit" -> nên là hook chứ không phải CLAUDE.md instruction.

---

## 6. Cursor Rules MDC format (so sánh)

### 6.1 Bốn rule types

1. **Always Apply** — Mọi chat session
2. **Apply Intelligently** — Agent decide qua `description` field
3. **Apply to Specific Files** — Glob match
4. **Apply Manually** — Chỉ khi @-mention

### 6.2 Frontmatter

```markdown
---
description: RPC service conventions for backend
globs: src/services/**/*.ts
alwaysApply: false
---
- Define each service in src/services/
- Return structured error objects with code + message
```

| `alwaysApply` | `description` | `globs` | Behavior |
|---|---|---|---|
| `true` | — | — | Always (ignore other fields) |
| `false` | — | provided | Auto-attached khi file match in context |
| `false` | provided | omitted | Agent pull khi relevant |
| `false` | omitted | omitted | Manual @-mention only |

### 6.3 Best practices Cursor (apply được cho Claude)

- Cap rule **< 500 lines**, split khi lớn hơn
- Reference files với `@filename.ts` thay vì paste content (avoid stale)
- Concrete examples > abstract description
- **Avoid**: dump entire style guide (use linter), document common tools (npm/git/pytest), edge-case-only rules, duplicate code đã ở repo

### 6.4 Key takeaway từ comparison

| Aspect | Claude Code | Cursor |
|---|---|---|
| Format | Plain markdown | MDC với frontmatter |
| Path-scoping | `.claude/rules/*.md` với `paths:` | `.cursor/rules/*.mdc` với `globs:` |
| Multi-file org | Yes (rules dir) | Yes (rules dir, subfolder support) |
| User-level | `~/.claude/CLAUDE.md` + `~/.claude/rules/` | Cursor Settings -> Rules |
| Plain text alt | AGENTS.md (via import) | AGENTS.md native support |

-> Pattern thống nhất: **modular rules với path-scoping + frontmatter metadata**.


---

## 7. Đánh giá repo KiotRetail hiện tại

### 7.1 Trạng thái

```
CLAUDE.md       269 dòng  [VƯỢT MỨC] vượt 200
AGENTS.md       355 dòng  [TRÙNG]    trùng CLAUDE.md, không được import
docs/CONTEXT_RULES.md   136 dòng
docs/FEATURE_INTAKE.md  137 dòng
docs/HARNESS.md         (tồn tại)
docs/stories/           (story packets — đúng spec)
docs/decisions/         5 ADR files — đúng spec
docs/templates/         (story/validation templates — đúng spec)
.claude/rules/          [CHƯA CÓ] không tồn tại
.claude/skills/         [ĐÃ CÓ] gitnexus skills
```

### 7.2 Vấn đề DRY phát hiện

**Trùng giữa `CLAUDE.md` và `AGENTS.md`**:

| Topic | CLAUDE.md | AGENTS.md |
|---|---|---|
| Project Identity | Có (lines 5-12) | Có (lines 5-12) |
| Architecture overview | Có | Có (chi tiết hơn) |
| Engineering Rules | Có (lines 207-217) | Có (lines 123-149) |
| Build commands | Có (lines 50-66) | Có (lines 188-198) |
| Database setup | Có (lines 68-150) | Tham chiếu |
| Harness methodology | Có (compressed) | Có (full) |

-> **Risk**: Sửa rule ở 1 file -> quên file kia -> Claude follow rule lỗi thời.

**`AGENTS.md` không vào context**: Claude Code không tự đọc `AGENTS.md`. CLAUDE.md hiện tại không có `@AGENTS.md` import -> toàn bộ content AGENTS.md (Architecture Contract, Protected Areas, Code Standards) **không tới Claude**.

### 7.3 Vấn đề khác

- **CLAUDE.md đang có 2 huge sections**: MySQL Docker setup (~80 dòng) + GitHub Project Board (~30 dòng) -> đa phần là procedure, không phải rule. Nên chuyển thành skills.
- **GitNexus block ở cuối (lines 230-269)** lặp ở cả CLAUDE.md và AGENTS.md.
- **Harness methodology** mô tả chi tiết trong AGENTS.md (lines 237-310) thực chất là procedure -> nên là skill `harness-task-loop`.

---

## 8. Recommendations cho KiotRetail

### 8.1 Cấu trúc đề xuất (3-tier)

```
SWP391_Group_5/
├── CLAUDE.md                          # < 150 dòng, chỉ facts thiết yếu
├── AGENTS.md                          # Single source of truth, được import
├── .claude/
│   ├── rules/
│   │   ├── architecture.md            # Layer rules, package structure (always-load)
│   │   ├── coding-standards.md        # Naming, exceptions, constants (always-load)
│   │   ├── jsp-views.md               # paths: src/main/webapp/WEB-INF/views/**
│   │   ├── dao-layer.md               # paths: **/dao/*.java
│   │   ├── servlet-controller.md      # paths: **/controller/*.java
│   │   └── sql-schema.md              # paths: sql/**/*.sql
│   ├── skills/
│   │   ├── harness-intake/SKILL.md    # Procedure: classify request -> lane
│   │   ├── harness-validate/SKILL.md  # Procedure: run mvn test/package
│   │   ├── mysql-health-check/SKILL.md # Procedure: docker checks
│   │   ├── gh-project-board/SKILL.md   # Procedure: update issue status
│   │   └── add-domain/SKILL.md        # Procedure: scaffold new module
│   └── settings.json                  # Hooks: post-edit mvn compile, pre-commit lint
└── docs/                              # Existing — keep
```

### 8.2 CLAUDE.md tối giản (đề xuất < 150 dòng)

```markdown
# CLAUDE.md

@AGENTS.md

## Quick Reference (Claude Code specific)

- **Project**: KiotRetail — Maven Java WAR webapp (Tomcat + Servlet/JSP + MySQL)
- **Run**: `mvn cargo:run -Dcargo.servlet.port=9999` -> http://localhost:9999/kiotretail
- **Test**: `mvn test`
- **Package**: `mvn clean package`
- **DB**: MySQL 8 Docker container `mysql-kiotretail`, db `DBFinora`, user/pass `root/root`

## Rule loading order

1. AGENTS.md (imported above) — operating contract, architecture, engineering rules
2. .claude/rules/*.md — modular rules (path-scoped where possible)
3. .claude/skills/*/SKILL.md — procedures (load on-demand)

## Hard rules (must follow)

- NEVER put SQL in servlets/JSPs/filters/API actions
- NEVER access HttpServletRequest/HttpSession from DAOs or services
- NEVER hardcode secrets, passwords, tokens
- NEVER edit `target/` (build artifact)
- ALWAYS run `mvn test` before claiming task done

## When stuck

- Architecture question -> read `docs/ARCHITECTURE.md`
- Risk classification -> run `/harness-intake` skill
- DB not responding -> run `/mysql-health-check` skill
- Adding new domain -> run `/add-domain {name}` skill

## GitNexus

This project is indexed as SWP391_Group_5. Run `gitnexus_impact()` before
editing any symbol. See `.claude/skills/gitnexus/` for full guide.
```

### 8.3 Tách rule path-scoped: `.claude/rules/dao-layer.md`

```markdown
---
paths:
  - "src/main/java/com/kiotretail/**/dao/*.java"
---
# DAO Layer Rules

- Extend `BaseDAO`, use try-with-resources for all JDBC ops
- No business logic — pure persistence
- Method naming: `getById()`, `getAll()`, `get{Entity}s(filter, pagination)`,
  `count{Entity}s(filter)`, `insert()`, `update()`, `softDelete()`
- NEVER call DAOs from other domains — go through their service
- NEVER access HttpServletRequest/HttpSession
- Use parameterized queries — no string concatenation in SQL
- Soft-delete via status flag, not DELETE statement
```

-> Rule này chỉ load vào context khi Claude đọc file DAO -> tiết kiệm token cho task không liên quan DAO.

### 8.4 Skill `harness-intake/SKILL.md`

```markdown
---
name: harness-intake
description: Classify a user request and choose Harness lane (tiny/normal/high-risk) before implementation
---

# Harness Intake Gate

Run this before any non-trivial change.

## Step 1: Classify input type

| Type | Use when |
|---|---|
| New spec | Turning project spec into harness-ready docs |
| Spec slice | Implementing selected behavior |
| Change request | Changing/fixing/refining accepted behavior |
| Maintenance | Technical/operational/dependency change |
| Harness improvement | Improving collaboration |

## Step 2: Risk checklist (mark each that applies)

- Auth (login, sessions, JWT, password)
- Authorization (roles, permissions, scope)
- Data model (schema, migrations, retention)
- Audit/security (logs, privacy, sensitive data)
- External systems (email, payments, providers)
- Public contracts (API shape, response envelope)
- Cross-platform (desktop/mobile/browser)
- Existing covered behavior changes
- Weak proof (unclear/missing tests)
- Multi-domain (>1 product domain)

## Step 3: Choose lane

- 0-1 flags -> tiny (patch directly, quick checks)
- 2-3 flags -> normal (create/update story in docs/stories/)
- 4+ flags or any hard gate -> high-risk (full template, ask human)

Hard gates: Auth, Authorization, Data loss/migration, Audit/security,
External provider behavior, Removing/weakening validation requirements.

## Step 4: State decision

```text
Lane: normal
Reason: touches authorization + API contract + audit
Docs: permissions, account-settings, audit-log
Story: docs/stories/US-014-manager-updates-role.md
Validation: unit + integration + E2E
```
```

### 8.5 Hook đề xuất: auto-compile sau Edit

`.claude/settings.json`:

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "matcherPath": "src/main/java/**/*.java",
        "hooks": [
          {
            "type": "command",
            "command": "mvn compile -q -fae",
            "timeout": 60000
          }
        ]
      }
    ]
  }
}
```

-> Mọi lần Claude edit Java file -> tự động compile -> bắt syntax error ngay, không cần Claude phải nhớ.

### 8.6 Migration plan (tránh phá vỡ workflow hiện tại)

**Phase 1 — Quick wins (ngay)**:
1. Add `@AGENTS.md` ở đầu CLAUDE.md để Claude đọc được AGENTS.md.
2. Xoá Project Identity / Engineering Rules duplicate khỏi CLAUDE.md (giữ ở AGENTS.md).
3. Move MySQL Docker setup từ CLAUDE.md -> `.claude/skills/mysql-health-check/SKILL.md`.
4. Move GitHub Project Board commands -> `.claude/skills/gh-project-board/SKILL.md`.

**Phase 2 — Modular rules (1 sprint)**:
1. Tạo `.claude/rules/architecture.md` (layer rules, đã có ở docs/architecture).
2. Tách `.claude/rules/dao-layer.md`, `servlet-controller.md`, `jsp-views.md` với `paths:` frontmatter.
3. Move "Code Standards" section từ AGENTS.md -> `.claude/rules/coding-standards.md`.

**Phase 3 — Hooks & skills (ad-hoc)**:
1. Add post-edit `mvn compile` hook.
2. Convert Harness task loop -> skills (`harness-intake`, `harness-validate`).
3. Add `/add-domain` skill scaffolding new module per pattern.

---

## 9. Common pitfalls & anti-patterns

### 9.1 Rules quá dài/verbose

**Symptom**: Claude bỏ qua rule rõ ràng đã có trong CLAUDE.md.
**Cause**: File >200 dòng -> "important rules get lost in noise".
**Fix**:
- Cắt ruthlessly: "Would removing this cause Claude to make mistakes?"
- Move detail vào skill (load on-demand).
- Tránh "self-evident" rule như "write clean code", "use good naming".

### 9.2 Conflicting rules

**Symptom**: Claude pick approach inconsistent giữa session.
**Cause**: 2 rule contradict nhau ở 2 file khác nhau.
**Fix**:
- Periodic review tất cả CLAUDE.md + nested + `.claude/rules/`.
- Trong monorepo: dùng `claudeMdExcludes` skip rule không liên quan.
- Single source of truth — xoá duplicate.

### 9.3 Stale rules

**Symptom**: Rule đề cập file/pattern không còn tồn tại.
**Cause**: Refactor code nhưng quên update rule.
**Fix**:
- Reference file bằng `@path` thay vì paste content (auto-resolve).
- Rule trong git -> review trong PR như code.
- Use `InstructionsLoaded` hook để debug rule nào đang load.

### 9.4 Over-engineering

**Symptom**: Setup phức tạp cho project nhỏ -> maintenance burden.
**Cause**: Áp dụng full Harness/3-tier cho dự án không cần.
**Fix**: Start simple. "Add rules only when you notice agent making the same mistake repeatedly." Repo nhỏ chỉ cần `CLAUDE.md` đơn giản.

### 9.5 Rules vs documentation confusion

**Symptom**: CLAUDE.md trở thành README dài lê thê.
**Cause**: Lẫn lộn giữa "instruction cho agent" và "doc cho human".
**Fix**:
| Audience | Format | Location |
|---|---|---|
| Agent (every session) | Markdown ngắn, imperative | CLAUDE.md, .claude/rules/ |
| Agent (on-demand) | SKILL.md procedure | .claude/skills/ |
| Human contributor | README, CONTRIBUTING | README.md, docs/ |
| Both | AGENTS.md ngắn | AGENTS.md (Claude import) |

### 9.6 Trust-then-verify gap

**Symptom**: Claude báo "done" nhưng chưa thật sự test/build.
**Cause**: Không có check it can run.
**Fix**:
- Mọi rule "task done" phải link với `mvn test` hoặc `mvn package`.
- Stop hook gate kết thúc turn nếu test fail.
- Mục `## Required Validation` trong AGENTS.md đã đúng -> cần thêm Stop hook để **enforce**.

### 9.7 Kitchen sink session

**Symptom**: Session dài, Claude quên instruction ban đầu.
**Cause**: Context full of irrelevant exploration.
**Fix**: `/clear` giữa unrelated tasks, dùng subagent cho investigation.

---

## 10. Patterns đặc thù cho Java/JSP/Maven

### 10.1 Build validation rules

```markdown
# .claude/rules/build-validation.md (always-load)
- Run `mvn test` after any Java source change before claiming done
- Run `mvn clean package` for servlet/JSP/web.xml changes
- Use `mvn compile -q -fae` for fast syntax check during dev
- NEVER use `-DskipTests` unless user explicitly asks
- If build fails: address root cause, do NOT suppress error
```

### 10.2 DAO/MVC layer separation (path-scoped)

Đã viết ở Section 8.3. Lợi ích: Claude đọc DAO file -> load DAO rules. Đọc Servlet file -> load controller rules. Không lẫn.

### 10.3 Database migration safety (high-risk hard gate)

```markdown
---
paths:
  - "sql/**/*.sql"
  - "**/dao/*.java"
---
# Database Changes (High-Risk Hard Gate)

Schema changes ALWAYS trigger high-risk lane per Harness rules.

Required artifacts:
- docs/templates/high-risk-story/ template filled
- Migration script in sql/ with timestamp prefix
- Rollback script
- Validation: load schema fresh + run integration tests

NEVER:
- Drop columns/tables without migration plan
- Change column types in-place (add new + migrate + drop)
- Remove indexes without performance check
- Modify primary key without explicit user approval
```

### 10.4 JSP/Servlet best practices (path-scoped)

```markdown
---
paths:
  - "src/main/webapp/WEB-INF/views/**/*.jsp"
---
# JSP View Rules

- Always include `<%@ page contentType="text/html;charset=UTF-8" %>` header
- Include `<jsp:include page="../common/toast.jsp" />` for flash messages
- NEVER write `<% ... scriptlet %>` blocks — use JSTL/EL only
- NEVER access DAO/database from JSP
- Use ViewPaths constants from controller side, never hardcode paths
- For toast notifications: use built-in showToast() — NOT alert/confirm/iziToast
- Pagination: include common/pagination.jsp with required attributes
```

### 10.5 Test enforcement (Stop hook)

```json
{
  "hooks": {
    "Stop": [
      {
        "hooks": [
          {
            "type": "command",
            "command": "mvn test -q",
            "blockOnFailure": true
          }
        ]
      }
    ]
  }
}
```

-> Claude không thể end turn nếu `mvn test` fail (override sau 8 consecutive blocks per spec).

---

## 11. Authoritative sources

- [Anthropic — Claude Code Memory (CLAUDE.md)](https://code.claude.com/docs/en/memory) — official spec for memory hierarchy, imports, rules dir, auto memory
- [Anthropic — Claude Code Best Practices](https://code.claude.com/docs/en/best-practices) — official guidance on workflows, hooks, skills, subagents
- [Anthropic — Skills](https://code.claude.com/docs/en/skills) — SKILL.md spec, progressive disclosure
- [AGENTS.md](https://agents.md/) — universal agent file standard, Linux Foundation
- [Cursor — Rules](https://cursor.com/docs/context/rules) — MDC format, rule types, frontmatter
- [Agent Skills open standard](https://agentskills.io) — cross-tool skill spec

---

## 12. Unresolved questions

1. **`.claude/settings.json` schema chính xác cho Stop hook `blockOnFailure`** — chưa fetch được full spec, cần verify trước khi implement.
2. **Hook execution context trên Windows** — `mvn` cần PATH env, có thể conflict với PowerShell escape rules; cần test thực tế.
3. **Claude Code version** mà user đang dùng — auto memory chỉ available v2.1.59+; nếu version cũ thì không nên rely vào auto memory.
4. **Team consensus về migration plan** — có 5 thành viên trong nhóm SWP391, restructure CLAUDE.md cần đồng thuận; nên propose qua PR.
5. **Có cần giữ `AGENTS.md` riêng** hay merge thẳng vào CLAUDE.md? AGENTS.md có ích nếu team dùng nhiều tool (Cursor + Claude). Nếu chỉ Claude Code -> có thể xoá AGENTS.md, gộp vào CLAUDE.md + `.claude/rules/`.
6. **Harness methodology có đang được agents tuân thủ?** Cần audit recent PRs xem story files được tạo đúng template không, và có phải lane classification đang giúp ích thực sự, hay chỉ thêm overhead.
