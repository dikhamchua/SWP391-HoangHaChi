# Repository Patterns

## Current Persistence Pattern

This codebase currently uses DAO classes rather than repository interfaces. Treat DAO classes as the persistence boundary.

## DAO Responsibilities

- Build SQL.
- Bind parameters.
- Execute JDBC statements.
- Map `ResultSet` values into model objects.
- Return simple success/failure values or domain objects.

## DAO Naming

- One DAO per primary entity or closely related aggregate.
- Use `<Entity>DAO`, for example `ProductDAO`.
- Private mapper methods should use `extract<Entity>`.

## Query Safety

- Always parameterize user input with `PreparedStatement`.
- Avoid string concatenation for user-controlled values.
- Validate pagination and limit values before binding.
- Keep dynamic SQL fragments limited to known-safe clauses.

## Transaction Policy

Current DAO methods open their own connections. This is acceptable for single-table or isolated operations.

For workflows requiring multi-step atomic updates, plan a service-level transaction strategy before implementation.
