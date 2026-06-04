-- Migration: Copy PO approval history from shared table to module-owned table
-- Part of issue #57: Decouple PO approval from shared ApprovalHistory
-- This is Phase 1 (dual-write): copies existing data, does NOT delete from source.
-- Idempotent: uses INSERT IGNORE to skip rows that already exist (matched by unique combo).

-- Step 1: Add a unique index to prevent duplicate imports (if not exists)
-- The combination of PurchaseOrderID + Action + PerformedBy + CreatedAt is unique per transition.
-- We use a conditional approach: only insert rows not already present.

INSERT INTO PurchaseOrderHistory (PurchaseOrderID, FromStatus, ToStatus, Action, PerformedBy, Reason, CreatedAt)
SELECT
    ah.DocumentID,
    ah.FromStatus,
    ah.ToStatus,
    ah.Action,
    ah.PerformedBy,
    ah.Reason,
    ah.CreatedAt
FROM ApprovalHistory ah
WHERE ah.DocumentType = 'PURCHASE_ORDER'
  AND NOT EXISTS (
    SELECT 1 FROM PurchaseOrderHistory poh
    WHERE poh.PurchaseOrderID = ah.DocumentID
      AND poh.Action COLLATE utf8mb4_unicode_ci = ah.Action COLLATE utf8mb4_unicode_ci
      AND poh.PerformedBy = ah.PerformedBy
      AND poh.CreatedAt = ah.CreatedAt
  );

-- Verify: count migrated rows
SELECT
    (SELECT COUNT(*) FROM ApprovalHistory WHERE DocumentType = 'PURCHASE_ORDER') AS source_count,
    (SELECT COUNT(*) FROM PurchaseOrderHistory) AS target_count;
