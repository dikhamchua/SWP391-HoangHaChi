# Backlog

## Security

- Externalize database credentials from source and XML config.
- Remove sensitive login debug logging.
- Standardize password hashing and verification.
- Review session cookie flags for production deployment.
- Add CSRF protection for state-changing forms.

## Database

- Confirm active schema file and deprecate duplicate or legacy schema scripts clearly.
- Align DAO table/column names with active SQL Server schema.
- Add migration/versioning workflow before production use.
- Review financial tables for audit requirements.

## Architecture

- Define criteria for introducing `service` package.
- Add ADRs for any framework or persistence strategy changes.
- Document feature/module ownership as modules grow.

## Testing

- Add baseline unit tests for utility classes.
- Add DAO integration test strategy with local SQL Server or test containers if feasible.
- Add controller-level smoke verification strategy.

## API

- Standardize error response shape and status handling.
- Standardize pagination parameter parsing.
- Document all active API routes.
