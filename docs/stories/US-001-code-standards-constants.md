# US-001 Code Standards and Constants Layer

## Status

completed

## Lane

normal

## Product Contract

Establish consistent code standards across all domain packages: constants for status values, error messages, session keys, pagination defaults; consistent method naming; proper validation patterns; and a shared constants class per domain.

## Relevant Product Docs

- `AGENTS.md` - Engineering Rules
- `CLAUDE.md` - Project Identity

## Acceptance Criteria

- Each domain has a Constants class with status values, default configs
- Shared constants for HTTP status codes, session keys, pagination defaults
- Consistent method naming across all DAOs (get*, count*, insert, update, softDelete)
- Consistent error messages (Vietnamese, reusable from constants)
- No magic strings/numbers in controllers or services
- All validated - compile check passes

## Design Notes

- Constants: `shared/constant/AppConstants.java` (global), `{domain}/constant/{Domain}Constants.java` (per-domain)
- Patterns: Builder pattern for filters, consistent validation in services
- Naming: DAO methods follow get/count/insert/update/softDelete convention (already done)
- Messages: Vietnamese error messages centralized in constants

## Validation

| Layer | Expected proof |
| --- | --- |
| Unit | N/A - structural change |
| Integration | Compile passes |
| Platform | All servlet mappings resolve |

## Harness Delta

Update AGENTS.md Architecture Contract section to reflect new modular structure after completion.

## Evidence

Pending implementation.
