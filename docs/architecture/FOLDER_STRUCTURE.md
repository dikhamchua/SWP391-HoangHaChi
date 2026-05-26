# Folder Structure

## Repository Structure

```text
kiotretail/
├── AGENTS.md
├── README.md
├── pom.xml
├── sql/
├── src/
│   └── main/
│       ├── java/com/kiotretail/
│       │   ├── api/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── filter/
│       │   ├── model/
│       │   └── util/
│       └── webapp/
│           ├── META-INF/
│           ├── WEB-INF/
│           │   ├── web.xml
│           │   └── views/
│           ├── assets/
│           └── index.jsp
└── docs/
```

## Java Source Structure

| Path | Purpose |
| --- | --- |
| `com/kiotretail/controller` | Servlet controllers for server-rendered page flows |
| `com/kiotretail/dao` | JDBC data access objects |
| `com/kiotretail/model` | Domain entity/data carrier classes |
| `com/kiotretail/filter` | Servlet filters for cross-cutting HTTP behavior |
| `com/kiotretail/util` | Focused utility classes used across layers |
| `com/kiotretail/api` | JSON API dispatching, response contract, API action interface |
| `com/kiotretail/api/action` | API action implementations |
| `com/kiotretail/api/dto` | API response DTOs distinct from server-rendered domain models |

## Web Structure

| Path | Purpose |
| --- | --- |
| `WEB-INF/web.xml` | Servlet/filter/session configuration |
| `WEB-INF/views/auth` | Login, registration, reset, and role-selection pages |
| `WEB-INF/views/admin` | Admin management pages |
| `WEB-INF/views/common` | Shared JSP fragments |
| `assets/css` | Theme, component, custom page styles |
| `assets/js` | Browser JavaScript |
| `META-INF/context.xml` | Tomcat context path configuration |

## Database Structure

| Path | Purpose |
| --- | --- |
| `sql/DBFinora.sql` | Primary SQL Server database creation and seed script |
| `sql/schema_sqlserver.sql` | Additional SQL Server schema script |
| `sql/schema.sql` | Legacy/deprecated MySQL schema according to existing README |

## Documentation Structure

The required documentation structure lives under `docs`:

```text
docs/
├── api/
├── architecture/
├── backend/
├── database/
├── decisions/
├── devops/
├── features/
├── frontend/
├── modules/
├── patterns/
├── planning/
├── references/
├── rules/
├── security/
├── status/
├── testing/
└── workflows/
```

## Generated And Ignored Structure

- `target/` is Maven output. Do not edit.
- `.git/` is repository metadata. Do not edit.
- IDE files and generated package files are ignored through `.gitignore`.
