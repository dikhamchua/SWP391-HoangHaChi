# Backend Standards

## Current Backend

The backend uses Jakarta Servlet APIs, JSP forwarding, JDBC DAOs, and SQL Server.

## Backend Rules

- Controllers handle HTTP request/response flow.
- DAOs handle SQL and result mapping.
- Models carry domain data.
- Utilities must be focused and reused.
- Filters handle cross-cutting HTTP concerns.
- Add services only for complex multi-DAO workflows or shared business logic.

## Build Baseline

Use Maven for verification:

```bash
mvn test
mvn clean package
```

Run the narrowest useful command first.
