# AI Workflow Rules

## Mandatory Repository Analysis

Before implementation, AI agents must inspect relevant existing code and docs. For broad architecture work, inspect:

- `src/main/java/com/kiotretail`
- `src/main/webapp/WEB-INF/views`
- `src/main/webapp/WEB-INF/web.xml`
- `src/main/webapp/META-INF/context.xml`
- `sql`
- `pom.xml`
- `README.md`
- `AGENTS.md`
- relevant files under `docs`

## Mem0 Usage

Before large features or protected module work, search Mem0 for:

- architecture decisions
- reusable patterns
- protected module constraints
- security decisions
- prior implementation decisions

After major decisions, suggest adding concise long-term memories using categories such as `[ARCH]`, `[RULE]`, `[PATTERN]`, `[BOUNDARY]`, `[SECURITY]`, `[WORKFLOW]`, or `[DECISION]`.

## Planning Requirements

When asked to create a plan, roadmap, module analysis, implementation steps, or task organization:

1. Create a dedicated markdown file under `docs/planning/<topic>/`.
2. Use deterministic uppercase naming.
3. Update `docs/planning/ACTIVE_TASKS.md`, `docs/planning/BACKLOG.md`, or `docs/planning/ROADMAP.md` when relevant.
4. Link related architecture, status, module, or security docs.
5. Preserve planning history rather than overwriting it.

## Implementation Requirements

- Keep changes scoped to the task.
- Reuse current patterns before adding new ones.
- Do not edit generated `target` output.
- Do not modify unrelated modules.
- Update documentation for architecture, security, database, API, planning, or status changes.
- Explain residual risks if validation cannot run.

## Review Requirements

When reviewing code, prioritize:

- security issues
- broken dependency boundaries
- database/schema mismatches
- authentication/authorization regressions
- data loss or transaction risks
- missing validation and tests
- documentation drift
