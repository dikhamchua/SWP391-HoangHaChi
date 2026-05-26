# Database Architecture

## Current Database Target

The primary database target is SQL Server with database name `DBFinora`.

The most complete active schema script currently appears to be:

- `sql/DBFinora.sql`

Additional schema files exist:

- `sql/schema_sqlserver.sql`
- `sql/schema.sql`, described in the README as deprecated MySQL schema

## Core Tables In Current Schema

- `Role`
- `Branch`
- `Employee`
- `Customer`
- `Supplier`
- `Category`
- `Product`
- `Warehouse`
- `Orders`
- `OrderDetail`
- `Payments`
- `FinanceTransaction`

The SQL script includes additional domain tables after the first 300 lines and must be reviewed fully before database changes.

## Persistence Pattern

- DAOs use `DatabaseUtil.getConnection()`.
- DAOs use JDBC and mostly `PreparedStatement`.
- Result mapping is handled inside DAO-private methods.
- No migration framework is currently present.

## Naming Warning

The active SQL Server schema uses PascalCase singular table and column names such as `Employee`, `Product`, `Category`, `EmployeeID`, and `CreatedAt`.

Some DAO methods currently reference lowercase plural table/column names such as `employees`, `roles`, `branches`, `employee_id`, and `created_at`. This mismatch is technical debt and may break those DAO paths depending on database collation and actual schema.

## Database Governance Rules

- Do not change schema without updating DAO SQL and database docs.
- Do not introduce new schema files without documenting which file is authoritative.
- Do not store production credentials in SQL scripts, Java files, XML files, or README examples.
- Use soft delete through status fields where existing patterns already use `Status = 'inactive'`.
- Payment, finance, order, and inventory schema changes require explicit planning and audit review.

## Migration Direction

Before production use, add a database migration workflow. Candidate approaches:

- Manual numbered SQL migrations under `sql/migrations`.
- Flyway if the team accepts an added dependency and process.
- Liquibase if rollback metadata and change tracking are required.

Do not introduce a migration tool without an architecture decision record.
