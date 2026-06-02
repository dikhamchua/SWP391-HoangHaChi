## 1. New Enums and Model

- [x] 1.1 Create `PurchaseOrderStatus` enum in `purchase/constant/` with 7 values + `fromString()` method
- [x] 1.2 Create `PurchaseOrderAction` enum in `purchase/constant/` with 7 values
- [x] 1.3 Create `PurchaseOrderHistory` model in `purchase/model/` with all fields and getters/setters

## 2. Database

- [x] 2.1 Create SQL migration file `sql/migration-purchase-history.sql` with CREATE TABLE + FK + index ← (verify: table DDL matches spec, FK constraints correct, index on PurchaseOrderID)

## 3. DAO

- [x] 3.1 Create `PurchaseOrderHistoryDAO` in `purchase/dao/` with `insert()` and `getByOrderId()` methods ← (verify: insert returns generated ID, getByOrderId joins Employee for performedByName, ordered DESC)

## 4. Service Refactor

- [x] 4.1 Add private approval gate methods to `PurchaseService`: `canSubmit()`, `canApprove()`, `canReject()`, `canCancel()`, `isApproverRole()`
- [x] 4.2 Add private `logTransition()` method that writes to `PurchaseOrderHistoryDAO` instead of shared `ApprovalService`
- [x] 4.3 Replace all `approvalService.canSubmit/canApprove/canReject/canCancel` calls with inline private methods
- [x] 4.4 Replace all `approvalService.logTransition()` calls with private `logTransition()` using `PurchaseOrderHistoryDAO`
- [x] 4.5 Replace all `DocumentStatus` references with `PurchaseOrderStatus` in PurchaseService
- [x] 4.6 Replace all `ApprovalAction` references with `PurchaseOrderAction` in PurchaseService
- [x] 4.7 Remove `ApprovalService` import and field from PurchaseService ← (verify: no remaining imports of shared.service.ApprovalService, shared.constant.DocumentStatus, shared.constant.ApprovalAction in PurchaseService)

## 5. Model Update

- [x] 5.1 Change `PurchaseOrder.getStatusEnum()` return type from `DocumentStatus` to `PurchaseOrderStatus`
- [x] 5.2 Update import in `PurchaseOrder.java` from `DocumentStatus` to `PurchaseOrderStatus` ← (verify: no DocumentStatus import remains in PurchaseOrder.java)

## 6. Controller Update

- [x] 6.1 Replace `ApprovalService` usage in `PurchaseServlet` with inline `PurchaseOrderStatus` checks
- [x] 6.2 Replace `ApprovalHistoryDAO` usage in `PurchaseServlet` with `PurchaseOrderHistoryDAO`
- [x] 6.3 Replace `DocumentStatus` references with `PurchaseOrderStatus` in PurchaseServlet
- [x] 6.4 Remove shared approval imports from PurchaseServlet ← (verify: no remaining imports of shared.service.ApprovalService, shared.dao.ApprovalHistoryDAO, shared.constant.DocumentStatus in PurchaseServlet)

## 7. DAO Update

- [x] 7.1 Remove or replace any `DocumentStatus` reference in `PurchaseOrderDAO` (javadoc only — update comment)

## 8. Tests

- [x] 8.1 Create `PurchaseApprovalTest.java` with 10-15 unit tests covering: canSubmit (2), canApprove (4), canReject (2), canCancel (3), logTransition (2-3)
- [x] 8.2 Run `mvn test` — all tests pass ← (verify: mvn test exits 0, no compile errors, no test failures)

## 9. Build Verification

- [x] 9.1 Run `mvn clean package` — WAR builds successfully ← (verify: BUILD SUCCESS, no errors)
