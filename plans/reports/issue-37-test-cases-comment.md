## Test Cases Implementation - ApprovalService

File: `src/test/java/com/kiotretail/shared/service/ApprovalServiceTest.java`
Run: `mvn test -Dtest=ApprovalServiceTest`
Result: **60 tests PASS** / 0 fail / 0 skip

### 1. Core test cases (theo bảng đã chốt)

| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-001 | canSubmit khi status = DRAFT | return true |
| APPR-002 | canSubmit khi status != DRAFT (PENDING_APPROVAL) | return false |
| APPR-003 | canApprove - PENDING_APPROVAL + role STORE_MANAGER + approver != creator | return true |
| APPR-004 | canApprove - approver == creator (self-approve) | return false |
| APPR-005 | canApprove - role SALES_STAFF | return false |
| APPR-006 | canReject - PENDING_APPROVAL + role STORE_MANAGER | return true |
| APPR-007 | canReject - status DRAFT | return false |
| APPR-008 | canCancel - DRAFT, isOwner = true | return true |
| APPR-009 | canCancel - APPROVED, role OWNER | return true |
| APPR-010 | canCancel - APPROVED, role STORE_MANAGER | return false |
| APPR-011 | logTransition - REJECT without reason | throw ValidationException |
| APPR-012 | logTransition - CANCEL without reason | throw ValidationException |
| APPR-013 | logTransition - valid transition | insert vào ApprovalHistory |

### 2. Edge cases bổ sung (47 test, đã pass)

#### canSubmit edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-014 | canSubmit khi status = null | return false |
| APPR-015 | canSubmit khi status = APPROVED | return false |
| APPR-016 | canSubmit khi status = REJECTED (không cho resubmit) | return false |
| APPR-017 | canSubmit khi status = CANCELLED | return false |
| APPR-018 | canSubmit khi status = COMPLETED | return false |

#### canApprove edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-019 | canApprove role OWNER + PENDING_APPROVAL + approver != creator | return true |
| APPR-020 | canApprove status = DRAFT (chưa submit) | return false |
| APPR-021 | canApprove status = APPROVED (đã duyệt rồi) | return false |
| APPR-022 | canApprove status = REJECTED | return false |
| APPR-023 | canApprove role WAREHOUSE_STAFF | return false |
| APPR-024 | canApprove role = null (không NPE) | return false |
| APPR-025 | canApprove role = chuỗi rỗng | return false |
| APPR-026 | canApprove role lowercase "owner" (case-sensitive) | return false |
| APPR-046 | canApprove status = null | return false |

#### canReject edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-027 | canReject PENDING_APPROVAL + OWNER | return true |
| APPR-028 | canReject SALES_STAFF | return false |
| APPR-029 | canReject WAREHOUSE_STAFF | return false |
| APPR-030 | canReject status = APPROVED | return false |
| APPR-031 | canReject status = CANCELLED | return false |
| APPR-032 | canReject status = null | return false |
| APPR-033 | canReject role = null (không NPE) | return false |

#### canCancel edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-034 | canCancel PENDING_APPROVAL + isOwner = true | return true |
| APPR-035 | canCancel PENDING_APPROVAL + STORE_MANAGER | return true |
| APPR-036 | canCancel PENDING_APPROVAL + SALES_STAFF + không phải owner | return false |
| APPR-037 | canCancel IN_PROGRESS + OWNER | return true |
| APPR-038 | canCancel IN_PROGRESS + STORE_MANAGER (kể cả là owner) | return false |
| APPR-039 | canCancel RECEIVING + OWNER | return true |
| APPR-040 | canCancel RECEIVING + SALES_STAFF | return false |
| APPR-041 | canCancel COMPLETED (terminal state) | return false |
| APPR-042 | canCancel CANCELLED (đã hủy rồi) | return false |
| APPR-043 | canCancel REJECTED | return false |
| APPR-044 | canCancel FINALIZED | return false |
| APPR-045 | canCancel status = null | return false |

#### logTransition edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-047 | logTransition docType = null | throw ValidationException |
| APPR-048 | logTransition docType blank ("   ") | throw ValidationException |
| APPR-049 | logTransition target status = null | throw ValidationException |
| APPR-050 | logTransition action = null | throw ValidationException |
| APPR-051 | logTransition performedBy = 0 | throw ValidationException |
| APPR-052 | logTransition performedBy < 0 | throw ValidationException |
| APPR-053 | logTransition REJECT reason chỉ có whitespace | throw ValidationException |
| APPR-054 | logTransition CANCEL reason rỗng | throw ValidationException |
| APPR-055 | logTransition APPROVE không cần reason | insert ok |
| APPR-056 | logTransition CREATE với fromStatus = null | insert ok, fromStatus = null |
| APPR-057 | logTransition trim docType trước khi lưu | docType được trim |
| APPR-058 | logTransition khi DAO trả về -1 | throw ServiceException (500) |

#### DocumentStatus parser edges
| TC-ID | Scenario | Expected |
|-------|----------|----------|
| APPR-059 | DocumentStatus.fromString case-insensitive ("draft", "Pending_Approval") | parse được |
| APPR-060 | DocumentStatus.fromString null/blank/unknown | return null |

### 3. Cách triển khai

- **Không phụ thuộc DB**: các case `canSubmit/canApprove/canReject/canCancel` thuần logic, không chạm DAO.
- **logTransition**: dùng reflection inject 1 stub `ApprovalHistoryDAO` (in-memory) để verify field được set đúng và validate đúng các nhánh exception. Một stub thứ hai mô phỏng DAO fail (return -1) để cover nhánh `ServiceException`.
- **Tổng**: 13 core + 47 edge = **60 test cases**, all green.

### 4. Tổng kết coverage

- Status transitions: DRAFT, PENDING_APPROVAL, APPROVED, REJECTED, IN_PROGRESS, RECEIVING, COMPLETED, FINALIZED, CANCELLED, null
- Roles: OWNER, STORE_MANAGER, SALES_STAFF, WAREHOUSE_STAFF, null, empty, lowercase
- Validation: null/blank docType, null toStatus, null action, performedBy <= 0, blank reason cho REJECT/CANCEL
- Side-of-duty: self-approve bị chặn
- DAO failure: insert trả -1 → 500
