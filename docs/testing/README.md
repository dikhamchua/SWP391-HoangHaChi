# Testing Strategy

## Current State

The project includes a JUnit dependency but no visible test suite was identified during the governance setup.

## Verification Baseline

Run:

```bash
mvn test
```

For servlet/JSP/config changes, run:

```bash
mvn clean package
```

## Testing Priorities

1. Utility tests for deterministic helpers such as role permissions and password hashing.
2. Controller validation tests where feasible.
3. DAO integration tests against a controlled SQL Server test database.
4. Authentication and authorization regression tests.
5. API response shape tests for `/api/*` endpoints.

## Protected Regression Areas

- Login and logout
- Registration and password reset
- Role selection and permission checks
- Category management authorization
- Database connection and schema compatibility
- Payment, finance, order, and inventory workflows once implemented
