# Engineering Workflows

## Standard Change Workflow

1. Read `AGENTS.md`.
2. Read relevant architecture/rules/status docs.
3. Inspect existing code before editing.
4. Identify protected modules and dependency boundaries.
5. Make the smallest correct change.
6. Run targeted verification.
7. Update docs if behavior, boundaries, patterns, or status changed.

## Feature Planning Workflow

1. Create `docs/planning/<topic>/<TOPIC>_IMPLEMENTATION_PLAN.md`.
2. Include current-state analysis, scope, affected modules, protected impact, steps, validation, and open questions.
3. Update planning indexes.
4. Search Mem0 before implementation if the feature is large or protected.
5. Suggest long-term memories after major decisions.

## Protected Module Workflow

1. Confirm the protected area in `docs/rules/PROTECTED_MODULES.md`.
2. Read all route mappings, session usage, DAOs, JSPs, and SQL touched by the change.
3. Document risks before or during implementation.
4. Keep the change minimal.
5. Run build/tests or document residual risk.
