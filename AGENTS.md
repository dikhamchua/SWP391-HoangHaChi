# AGENTS.md

This file is the operating contract for every AI agent and developer working in this repository. Follow it before changing code, planning features, reviewing pull requests, or modifying architecture.

## Project Identity

- Project: KiotRetail
- Type: Maven Java WAR web application
- Runtime: Apache Tomcat with Jakarta Servlet/JSP APIs
- Architecture style: Layered MVC with JSP views, servlet controllers, DAO-based JDBC persistence, SQL Server schema scripts, and a small JSON API action router
- Primary package: `com.kiotretail`

## Mandatory First Step

Before implementing any non-trivial change, inspect the existing repository state:

1. Read relevant files under `src/main/java/com/kiotretail`.
2. Read relevant JSP views under `src/main/webapp/WEB-INF/views`.
3. Read relevant SQL under `sql` before changing persistence behavior.
4. Read root configuration files, especially `pom.xml`, `web.xml`, `.gitignore`, and deployment context files.
5. Check the governance docs under `docs` for boundaries, standards, status, and prior decisions.

Generated output under `target` is build artifact output. Do not treat it as source of truth and do not edit it directly.

## Architecture Contract

- Controllers are servlets in `com.kiotretail.controller` and should coordinate request parsing, validation, redirects, forwards, and response status only.
- Database access belongs in DAO classes under `com.kiotretail.dao`.
- Models under `com.kiotretail.model` are data carriers for domain entities used by JSP/server-side flows.
- JSON API routing belongs under `com.kiotretail.api` and `com.kiotretail.api.action`.
- JSP files under `WEB-INF/views` render UI and should not contain database access or heavy business logic.
- Shared cross-cutting behavior belongs in `filter` or focused utility classes under `util` only when reuse is proven.

## Protected Areas

Treat the following as protected modules:

- Authentication and session flow: `LoginServlet`, `LogoutServlet`, `RegisterServlet`, `ForgotPasswordServlet`, `RoleSelectionServlet`, `AuthFilter`, login/register/reset JSPs
- Authorization: `RolePermissionUtil`, role-based session attributes, role-selection routing, protected admin/POS URL mappings
- Database infrastructure: `DatabaseUtil`, `web.xml` database parameters, SQL schema files under `sql`
- Payment and finance schema areas: `Payments`, `FinanceTransaction`, order/payment tables, future payment service code
- Shared infrastructure: `EncodingFilter`, API `BaseController`, Maven build configuration

Do not modify protected areas unless the user explicitly asks or the active task cannot be completed safely without doing so. If modification is required, document impact and keep the change minimal.

## Engineering Rules

- Prefer the smallest correct change.
- Reuse existing DAO/model/controller patterns before adding new abstractions.
- Do not create generic utility files unless at least two real call sites need them.
- Do not put SQL in servlets, JSPs, filters, or API actions.
- Do not access request/session objects from DAOs.
- Do not hardcode secrets, passwords, tokens, or production credentials.
- Do not edit `target`, `.git`, IDE private state, or generated artifacts.
- Keep naming consistent with existing package conventions.
- Keep methods small enough to review and test.
- Update documentation when architecture, boundaries, workflow, status, or patterns change.

## Planning Workflow

Feature plans must be stored under `docs/planning/<topic>/` using deterministic uppercase names such as `INVOICE_IMPLEMENTATION_PLAN.md` or `AUTH_REFACTOR_PLAN.md`.

Every plan must include:

- Scope
- Current-state analysis
- Affected modules
- Protected-area impact
- Implementation steps
- Validation strategy
- Documentation updates
- Open questions

Update `docs/planning/ACTIVE_TASKS.md`, `docs/planning/BACKLOG.md`, or `docs/planning/ROADMAP.md` when relevant.

## Mem0 Workflow

Before large features, search long-term memory for related rules, architecture decisions, protected modules, security decisions, and reusable patterns.

After major decisions, suggest storing concise memories using categories:

- `[RULE]`
- `[ARCH]`
- `[PATTERN]`
- `[BOUNDARY]`
- `[SECURITY]`
- `[WORKFLOW]`
- `[DECISION]`

Do not store temporary task noise.

## Required Validation

For application changes, run the narrowest useful verification first. Preferred baseline:

```bash
mvn test
```

For packaging or servlet/JSP changes, also run:

```bash
mvn clean package
```

If verification cannot run locally, state the reason and the residual risk.

## Documentation Map

- `docs/architecture`: system shape, dependency flow, folder structure, module boundaries
- `docs/rules`: coding standards, naming, refactor policy, AI workflow, protected modules
- `docs/planning`: roadmap, active tasks, backlog, feature plans
- `docs/status`: current status, implemented features, technical debt
- `docs/patterns`: reusable controller, DAO, service, repository, and API patterns
- `docs/security`: authentication, authorization, secrets, session, and data protection rules
- `docs/database`: schema and persistence architecture
- `docs/api`: JSON API conventions and standards
- `docs/features`, `docs/modules`, `docs/decisions`, `docs/workflows`, `docs/references`, `docs/frontend`, `docs/backend`, `docs/devops`, `docs/testing`: supporting governance indexes


MEM0 MEMORY POLICY:

- After any architectural decision, pattern creation, or workflow change:
  - analyze if it has long-term value
  - suggest adding it to Mem0
  - categorize it as [ARCH], [RULE], [PATTERN], [WORKFLOW], or [DECISION]