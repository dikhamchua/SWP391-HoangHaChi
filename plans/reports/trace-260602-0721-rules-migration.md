# Trace - Claude Code Rules Migration

> Phase 1 + 2 + 3 (skills only, hooks deferred) of the rules restructure proposed in
> `plans/reports/researcher-260602-0703-claude-code-rules-best-practices.md`.
> Run on 2026-06-02. Lane: normal (touches developer experience, no production behavior).

## What changed

### Top-level files

| File | Before | After | Delta |
|---|---|---|---|
| `CLAUDE.md` | 269 lines, full procedures inline | 80 lines, `@AGENTS.md` import + facts only | -189 lines |
| `AGENTS.md` | 355 lines, untouched (still single source of truth for the contract) | unchanged | 0 |

CLAUDE.md now imports AGENTS.md via `@AGENTS.md` so the operating contract finally
reaches Claude (per Anthropic doc - Claude Code does NOT auto-read AGENTS.md).

### New files - `.claude/rules/` (path-scoped)

| File | Lines | Scope (paths frontmatter) |
|---|---|---|
| `architecture.md` | 70 | always-load |
| `coding-standards.md` | 51 | always-load |
| `build-validation.md` | 51 | always-load |
| `dao-layer.md` | 47 | `src/main/java/com/kiotretail/**/dao/*.java` |
| `servlet-controller.md` | 56 | `**/controller/*.java` + `api/action/*.java` |
| `jsp-views.md` | 51 | `src/main/webapp/WEB-INF/views/**/*.jsp` |
| `sql-schema.md` | 48 | `sql/**/*.sql` + `DatabaseUtil.java` |

Path-scoped rules load only when Claude reads a matching file -> no token waste on
unrelated tasks. Total 374 lines spread across 7 files, average 53 lines/file.

### New files - `.claude/skills/` (load on-demand)

| Skill | Lines | Purpose |
|---|---|---|
| `mysql-health-check/SKILL.md` | 102 | Verify Docker container, reload schema, troubleshoot |
| `gh-project-board/SKILL.md` | 54 | Update issue status on the SWP391 board |
| `harness-intake/SKILL.md` | 70 | Classify request -> tiny / normal / high-risk lane |
| `harness-validate/SKILL.md` | 64 | Pick the right `mvn` command and report proof |

Skills frontmatter (`name` + `description`) loads at session start (~50 tokens
each). The body loads only when Claude invokes the skill.

### `.claude/settings.json`

Created with hooks disabled (commented under `_hooks_disabled`). Two hook
templates included for future activation:

- `PostToolUse: Edit|Write -> mvn compile -q -fae` (post-Java-edit syntax check)
- `Stop -> mvn test -q` with `blockOnFailure: true` (gate end of turn)

Both deferred. `mvn compile` takes 15-30s on Windows + Maven; running it on every
edit would dominate iteration time. User can flip the section name from
`_hooks_disabled` to `hooks` when ready.

## What was NOT attempted

- **Slimming `AGENTS.md`**: The 355-line operating contract still has its own
  Harness section (lines 237-311) and GitNexus block (lines 313-355) that
  partially duplicate the new skills. Did not touch because removing the
  GitNexus block from AGENTS.md would also remove it from CLAUDE.md (via the
  import), which weakens the always-on "use gitnexus_impact before editing"
  reminder. Recommend a follow-up pass to extract architecture-contract-only
  content and move Harness/GitNexus to dedicated rule files - but that needs
  team consensus.

- **Activating hooks**: Settings template is in place but commented. User must
  explicitly flip `_hooks_disabled` to `hooks` to enable.

- **`/init`-style auto-generation**: Did not regenerate CLAUDE.md from scratch
  via `/init`. The hand-written rewrite preserves project context that `/init`
  would not have known.

- **Migration of `docs/HARNESS.md` content into skills**: The Harness operating
  contract still lives in `AGENTS.md` and `docs/HARNESS.md`. Skills
  `harness-intake` and `harness-validate` are entry-points for the loop steps,
  not a full replacement.

- **`mvn test` validation**: This was a docs-only restructure. No Java/JSP/SQL
  source files changed. Per `harness-validate` rules: "Docs only -> skip, but
  state docs only - no validation needed."

## Validation

> Validation: docs only - no `mvn` run needed.
>
> Smoke check:
> - `git status` shows new files in `.claude/rules/` and `.claude/skills/`
> - `wc -l` confirms `CLAUDE.md` 80 lines, all new rules under 80 lines
> - Claude Code session reminder confirms 4 new skills loaded:
>   `gh-project-board`, `harness-intake`, `harness-validate`, `mysql-health-check`

## Friction recorded

For `docs/HARNESS_BACKLOG.md`:

1. **AGENTS.md duplication** - Harness section + GitNexus block still appear in
   both CLAUDE.md (via import) and standalone AGENTS.md from earlier history.
   After this migration, only AGENTS.md remains; no longer a duplicate. But the
   in-AGENTS.md Harness block (lines 237-311) and GitNexus block (lines 313-355)
   are now also covered by `.claude/skills/harness-intake`,
   `.claude/skills/harness-validate`, and `.claude/skills/gitnexus/*`. Pick one
   source of truth in a follow-up pass.

2. **Windows + `mvn` hook latency** - Cannot enable post-edit `mvn compile` hook
   without making iteration painful (~15-30s per edit). Need either:
   (a) faster incremental compile (Maven daemon `mvnd`), or
   (b) accept the cost and only enable the Stop hook (test on turn end).

3. **No `InstructionsLoaded` debug hook configured** - Hard to verify which rule
   files actually load in a given session. Consider adding a debug skill that
   inspects context.

## Next steps (suggested, not required)

1. Try a real task with the new structure to see which rules fire and whether
   the lane decision in `harness-intake` is helpful.
2. After 1-2 sprints, slim `AGENTS.md` to the architecture contract only;
   move Harness loop + GitNexus reminders to dedicated `.claude/rules/` files.
3. Enable the Stop hook (`mvn test`) once the team agrees the latency is
   acceptable. The post-edit hook stays off.
4. Add `.claude/rules/security.md` if a future task touches `AuthFilter`,
   `SessionUtil`, or password handling - currently those are spread across
   architecture.md "Protected modules" + sql-schema.md "high-risk".

## Reference

- Researcher report: `plans/reports/researcher-260602-0703-claude-code-rules-best-practices.md`
- Anthropic memory doc: https://code.claude.com/docs/en/memory
- AGENTS.md spec: https://agents.md/
