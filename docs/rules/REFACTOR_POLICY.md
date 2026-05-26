# Refactor Policy

## Refactor Principles

- Refactor only to support a clear feature, bug fix, risk reduction, or documented technical-debt item.
- Prefer small, behavior-preserving refactors over broad rewrites.
- Do not introduce architecture layers without demonstrated need.
- Keep refactors isolated from unrelated feature work when possible.
- Never change protected modules casually.

## Safe Refactor Workflow

1. Identify the exact behavior that must remain unchanged.
2. Read all affected call sites and route mappings.
3. Check whether the module is protected.
4. Make the smallest internal structure change.
5. Run targeted verification.
6. Update docs if boundaries, patterns, or workflows changed.

## When To Extract Helpers

Extract a helper only when:

- The same logic appears in at least two real places.
- The extraction makes the original method easier to understand.
- The helper has a clear name and single responsibility.
- The helper does not hide request/session/database side effects.

## When To Add A Service Layer

Add service classes only when business logic spans multiple DAOs, needs transaction boundaries, or is reused by page controllers and API actions.

Do not add a service layer for one controller calling one DAO.

## Refactor Guardrails

- Do not rewrite working JSP pages into another frontend framework without explicit approval.
- Do not replace JDBC with ORM without an architecture decision record.
- Do not change database table/column names without migration planning.
- Do not change authentication/password behavior without security review.
- Do not combine unrelated cleanup with protected-area changes.
