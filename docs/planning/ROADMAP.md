# Roadmap

## Current Phase: Stabilization And Governance

Goals:

- Establish architecture governance and documentation.
- Make current boundaries explicit for future AI agents and developers.
- Identify security and database risks before feature expansion.
- Preserve the current MVC/JSP/DAO architecture while improving consistency.

## Near-Term Priorities

1. Remove hardcoded secrets from database configuration.
2. Standardize password hashing and login verification.
3. Align DAO SQL table/column names with the active SQL Server schema.
4. Add basic test coverage around authentication, category validation, and DAO mapping where feasible.
5. Replace debug console output in sensitive flows with safe logging strategy.
6. Document implemented and pending feature modules in `docs/features` and `docs/modules`.

## Mid-Term Priorities

1. Introduce service classes only for multi-step workflows such as checkout, inventory movement, and payments.
2. Add transaction boundaries for workflows that update multiple tables.
3. Formalize API route standards and pagination/error behavior.
4. Improve error pages and global error handling.
5. Add deployment profiles for local, test, and production environments.

## Long-Term Priorities

1. Establish automated CI verification.
2. Add audit logging for finance, inventory, user access, and payment events.
3. Add role/permission management backed by database permissions instead of hardcoded role checks.
4. Add observability for application health and database connectivity.
5. Evaluate whether a fuller service/application layer is justified by feature complexity.
