# Service Patterns

## Current Status

The repository does not currently use a formal `service` package. Existing controllers call DAOs directly.

This is acceptable for simple CRUD page flows, but it should not be expanded into complex multi-step business workflows.

## When To Introduce Services

Introduce `com.kiotretail.service` only when a workflow:

- Coordinates multiple DAOs.
- Requires a transaction across multiple SQL operations.
- Is reused by both servlet controllers and API actions.
- Contains business rules that would make a servlet large or hard to test.

## Service Responsibilities

Services may:

- Validate domain-level business rules.
- Coordinate DAO calls.
- Own transaction boundaries once transaction support is introduced.
- Return domain models, DTOs, or operation result objects.

Services must not:

- Depend on JSPs.
- Write directly to `HttpServletResponse`.
- Store or read servlet session state.
- Hide database credentials or connection creation outside the established persistence strategy.

## Candidate Future Services

- `AuthService` after password behavior is standardized.
- `InventoryService` for stock movement and warehouse rules.
- `CheckoutService` for order, order detail, inventory, payment, and finance transaction coordination.
- `ReportingService` for aggregate reads and report generation.
