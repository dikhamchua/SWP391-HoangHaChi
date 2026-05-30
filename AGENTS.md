# AGENTS.md

This file is the operating contract for every AI agent and developer working in this repository. Follow it before changing code, planning features, reviewing pull requests, or modifying architecture.

## Project Identity

- Project: KiotRetail
- Type: Maven Java WAR web application
- Runtime: Apache Tomcat with Jakarta Servlet/JSP APIs
- Architecture style: Modular Monolith with domain-based packages, layered MVC (Controller -> Service -> DAO -> Model), JSP views, Jakarta Servlet/JSP, JDBC persistence against SQL Server
- Primary package: `com.kiotretail`

## Mandatory First Step

Before implementing any non-trivial change, inspect the existing repository state:

1. **Use GitNexus first** to find affected symbols, execution flows, and dependencies:
   - `gitnexus query` — find relevant code by natural language
   - `gitnexus context` — get 360-degree view of a symbol (callers, callees, references)
   - `gitnexus impact` — analyze blast radius before changing a symbol
   - `gitnexus detect_changes` — map uncommitted changes to affected flows
2. Read relevant files under `src/main/java/com/kiotretail`.
3. Read relevant JSP views under `src/main/webapp/WEB-INF/views`.
4. Read relevant SQL under `sql` before changing persistence behavior.
5. Read root configuration files, especially `pom.xml`, `web.xml`, `.gitignore`, and deployment context files.
6. Check the governance docs under `docs` for boundaries, standards, status, and prior decisions.

Generated output under `target` is build artifact output. Do not treat it as source of truth and do not edit it directly.

## Architecture Contract

The codebase follows a **Modular Monolith** pattern organized by business domain. Each domain is self-contained with its own controller, service, DAO, and model layers.

### Package Structure

```
com.kiotretail/
├── shared/              # Cross-cutting infrastructure
│   ├── base/            # BaseServlet, BaseDAO, BaseService, Pagination, PageResult
│   ├── constant/        # AppConstants, ErrorMessages, ViewPaths
│   ├── exception/       # ServiceException, ValidationException, NotFoundException
│   ├── filter/          # AuthFilter, EncodingFilter
│   └── util/            # DatabaseUtil, PasswordUtil, SessionUtil, JsonUtil, DateUtil, CurrencyUtil, CodeGenerator
├── product/             # Product + Category + Supplier domain
│   ├── controller/      # ProductServlet, CategoryServlet
│   ├── service/         # ProductService, CategoryService
│   ├── dao/             # ProductDAO, CategoryDAO, SupplierDAO
│   ├── model/           # Product, Category, Supplier
│   └── dto/             # ProductFilterDTO
├── customer/            # Customer domain
│   ├── controller/      # CustomerServlet
│   ├── service/         # CustomerService
│   ├── dao/             # CustomerDAO
│   ├── model/           # Customer
│   └── dto/             # CustomerFilterDTO
├── employee/            # Employee + Auth + Role + Branch domain
│   ├── controller/      # LoginServlet, LogoutServlet, RegisterServlet, ForgotPasswordServlet, ChangePasswordServlet, RoleSelectionServlet, EmployeeServlet
│   ├── service/         # AuthService, EmployeeService
│   ├── dao/             # EmployeeDAO, RoleDAO, BranchDAO
│   └── model/           # Employee, Role, Branch
├── invoice/             # Invoice/Order domain
│   ├── controller/      # InvoiceServlet
│   ├── service/         # InvoiceService
│   ├── dao/             # OrderDAO, OrderDetailDAO, PaymentDAO
│   ├── model/           # Order, OrderDetail, Payment
│   └── dto/             # InvoiceFilterDTO
├── pos/                 # POS sale domain
│   ├── controller/      # POSServlet
│   ├── service/         # POSService
│   └── dto/             # CartItem, CartSession
├── report/              # Report + Dashboard domain
│   ├── controller/      # DashboardServlet, ReportServlet
│   ├── service/         # DashboardService
│   └── dao/             # ReportDAO
└── api/                 # REST API layer
    ├── action/          # GetProductsAction
    └── dto/             # API-specific DTOs
```

### Layer Rules

- **Controllers** (extend `BaseServlet`): Parse requests, delegate to services, set attributes, forward/redirect. No business logic.
- **Services**: Business logic, validation, orchestration. Throw `ServiceException`/`ValidationException`/`NotFoundException`. No request/session access.
- **DAOs** (extend `BaseDAO`): JDBC operations only. No business logic. Use try-with-resources.
- **Models**: POJOs mapping DB tables. No logic beyond simple helpers (isActive, isLowStock).
- **DTOs**: Filter/transfer objects for cross-layer communication.
- **Constants**: All magic strings/numbers must use `AppConstants`, `ErrorMessages`, or `ViewPaths`.

### Cross-Domain Rules

- Domains may depend on `shared/` freely.
- Domain-to-domain dependencies are allowed only at the service level (e.g., `POSService` uses `InvoiceService`).
- DAOs must not call other domain DAOs directly — go through services.
- Controllers must not call DAOs directly — always go through services.

### JSP View Structure

```
WEB-INF/views/
├── common/     # header.jsp, navbar.jsp, pagination.jsp, sidebar.jsp, footer.jsp
├── auth/       # login, register, forgot-password, change-password, role-selection
├── product/    # products, product-create, product-detail, product-edit, categories
├── customer/   # customers, customer-create, customer-edit, customer-detail
├── employee/   # employees, employee-create, employee-edit, employee-detail
├── invoice/    # invoices, invoice-detail, invoice-create
├── pos/        # sale
├── report/     # dashboard, sales-report
└── error/      # 404, 500
```

## Protected Areas

Treat the following as protected modules:

- Authentication and session flow: `employee/controller/LoginServlet`, `LogoutServlet`, `RegisterServlet`, `ForgotPasswordServlet`, `RoleSelectionServlet`, `employee/service/AuthService`, `shared/filter/AuthFilter`, auth JSPs
- Authorization: `shared/constant/AppConstants` role constants, `shared/util/SessionUtil`, role-selection routing, protected admin/POS URL mappings in `web.xml`
- Database infrastructure: `shared/util/DatabaseUtil`, `web.xml` database parameters, SQL schema files under `sql/`
- Payment and finance: `invoice/` domain (Order, Payment, InvoiceService), `FinanceTransaction` table, POS checkout flow
- Shared infrastructure: `shared/filter/EncodingFilter`, `shared/base/*`, `shared/constant/*`, `api/BaseController`, Maven build configuration (`pom.xml`)

Do not modify protected areas unless the user explicitly asks or the active task cannot be completed safely without doing so. If modification is required, document impact and keep the change minimal.

## Engineering Rules

- Prefer the smallest correct change.
- Reuse existing DAO/model/controller patterns before adding new abstractions.
- Do not create generic utility files unless at least two real call sites need them.
- Do not put SQL in servlets, JSPs, filters, or API actions.
- Do not access request/session objects from DAOs or services.
- Do not hardcode secrets, passwords, tokens, or production credentials.
- Do not edit `target`, `.git`, IDE private state, or generated artifacts.
- Keep naming consistent with existing package conventions.
- Keep methods small enough to review and test.
- Update documentation when architecture, boundaries, workflow, status, or patterns change.

### Code Standards

- **No magic strings/numbers**: Use `AppConstants`, `ErrorMessages`, `ViewPaths` from `shared/constant/`.
- **Status values**: Always use `AppConstants.STATUS_ACTIVE`, `STATUS_INACTIVE`, `STATUS_PENDING`, `STATUS_COMPLETED`, `STATUS_CANCELLED`.
- **Error messages**: Use `ErrorMessages` constants. Format with `String.format(ErrorMessages.FIELD_REQUIRED, "fieldName")`.
- **View paths**: Use `ViewPaths.PRODUCT_LIST`, `ViewPaths.REDIRECT_PRODUCTS`, etc.
- **Session keys**: Use `AppConstants.SESSION_EMPLOYEE`, `SESSION_ROLE_NAME`, `SESSION_BRANCH_ID`.
- **Flash messages**: Use `AppConstants.FLASH_SUCCESS`, `FLASH_DANGER`, `FLASH_WARNING`.
- **New domain**: Follow existing pattern — create `{domain}/controller/`, `service/`, `dao/`, `model/`, `dto/` packages.
- **DAO naming**: `getById()`, `getAll()`, `get{Entity}s(filter, pagination)`, `count{Entity}s(filter)`, `insert()`, `update()`, `softDelete()`.
- **Service naming**: `list{Entity}s()`, `get{Entity}ById()`, `create{Entity}()`, `update{Entity}()`, `delete{Entity}()`.
- **Controller pattern**: Extend `BaseServlet`, use `getIntParam()`/`getStringParam()`, delegate to service, set flash messages via session.
- **Exceptions**: Services throw `ValidationException` (400), `NotFoundException` (404), `ServiceException` (500). Controllers catch and redirect with flash message.

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
  
  
Before implementation:
- delegate memory retrieval to Mem0 subagents
- search previous architecture decisions
- search reusable patterns
- search protected module rules

For major features:
1. Create planning documents
2. Search Mem0 for related decisions
3. Analyze existing architecture
4. Generate implementation plan
5. Implement incrementally
6. Update docs
7. Suggest long-term memory additions


<!-- HARNESS:BEGIN -->
## Harness

This repo uses Harness. Before work, read:

- `README.md`
- `docs/HARNESS.md`
- `docs/FEATURE_INTAKE.md`
- `docs/ARCHITECTURE.md`
- `docs/CONTEXT_RULES.md`

## Harness Agents

### Task Loop (Every Task)

1. **Intake**: Classify the request using `docs/FEATURE_INTAKE.md`. Determine lane: tiny, normal, or high-risk.
2. **Planning**: Locate affected product docs and story files. Check proof status.
3. **Implementation**: Work only inside the selected lane scope.
4. **Validation**: Run validation commands. Confirm proof before claiming done.
5. **Trace**: Record what changed, what was not attempted, and any harness friction.

### Lane Rules

| Lane | When | Requirements |
| --- | --- | --- |
| Tiny | 0-1 risk flags, low-risk docs/copy/narrow edits | Patch directly, keep docs current, run quick checks |
| Normal | 2-3 risk flags, story-sized behavior | Create/update story file, link product docs, add validation expectations |
| High-Risk | 4+ flags or hard gate triggered | Use high-risk story template, fill execplan/design/validation, ask human confirmation |

### Risk Hard Gates (Always High-Risk)

- Auth (login, sessions, JWT, passwords)
- Authorization (roles, permissions, tenant scope)
- Data loss or migration
- Audit/security
- External provider behavior
- Removing or weakening validation requirements

### Context Rules Summary

- **Intake phase**: Read AGENTS.md, FEATURE_INTAKE.md, relevant product docs
- **Planning phase**: Read current files to edit, story templates, ARCHITECTURE.md
- **Implementation phase**: Read only files directly affecting the selected story
- **Validation phase**: Read story acceptance criteria, TEST_MATRIX.md
- **Trace phase**: Record files read/changed, outcome, friction

### Harness Change Policy

Agents may update directly:
- Story status and evidence
- Test matrix rows
- Links from story packets to product docs
- Validation notes and reports
- Small clarifications tied to the current task

Agents must ask human confirmation before:
- Changing architecture direction
- Removing validation requirements
- Changing the source-of-truth hierarchy
- Changing risk classification rules
- Replacing the feature workflow

### Done Definition

A task is done only when:
- The requested change is completed or the blocker is documented
- Relevant docs, stories, and test matrix entries remain current
- Validation commands were run when they exist
- Missing harness capabilities were recorded
- The final response says what changed and what was not attempted

### Growth Rule

When an agent is confused, repeats manual reasoning, needs a new validation command, discovers a missing rule, or sees a recurring failure pattern, it must either improve the harness directly or record the friction in `docs/HARNESS_BACKLOG.md`.
<!-- HARNESS:END -->

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
