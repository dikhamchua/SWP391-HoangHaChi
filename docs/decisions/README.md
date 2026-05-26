# Architecture Decisions

Architecture decision records belong here.

## Naming

Use:

```text
YYYY-MM-DD-short-title.md
```

## Template

```markdown
# Decision: <Title>

## Status
Proposed | Accepted | Superseded

## Context
What problem or constraint requires a decision?

## Decision
What is the chosen approach?

## Consequences
What tradeoffs, risks, and follow-up work result?

## Related Files
- `path/to/file`
```

## Current Decisions To Record Later

- Whether to keep DAO-only architecture or introduce services for checkout/payment workflows.
- Whether to adopt Flyway/Liquibase/manual SQL migrations.
- How to externalize secrets and environment configuration.
- How to standardize password hashing and migrate existing data.
