# KiotRetail Engineering Documentation

This directory is the persistent engineering brain for KiotRetail. It exists so future AI agents and developers can understand the system, preserve boundaries, plan safely, and evolve the codebase without re-discovering architecture context from scratch.

## Source Of Truth

- Application source: `src/main/java/com/kiotretail`
- Web UI source: `src/main/webapp`
- Database source: `sql`
- Build/dependency source: `pom.xml`
- AI/developer operating rules: `AGENTS.md` and `docs/rules`

Generated artifacts under `target` are not source of truth.

## Documentation Index

- `architecture/`: current architecture, module boundaries, dependency flow, folder structure
- `rules/`: mandatory coding, naming, AI workflow, refactoring, and protected module rules
- `planning/`: roadmap, active tasks, backlog, and future implementation plans
- `status/`: current implementation status, completed features, and technical debt
- `patterns/`: reusable implementation patterns for controllers, DAOs, repositories, and services
- `security/`: authentication, authorization, session, secret, and data handling rules
- `database/`: database architecture and schema governance
- `api/`: API routing and response standards
- `features/`: feature index and feature-level documentation
- `modules/`: module ownership and responsibility index
- `decisions/`: architecture decision records
- `workflows/`: repeatable engineering workflows
- `references/`: external references and project context
- `frontend/`: JSP/CSS/JavaScript UI conventions
- `backend/`: servlet/DAO/model backend conventions
- `devops/`: build, deploy, and environment conventions
- `testing/`: verification and test strategy

## Maintenance Rule

When source behavior changes, update the smallest relevant documentation file in the same change. Documentation drift is treated as technical debt.
