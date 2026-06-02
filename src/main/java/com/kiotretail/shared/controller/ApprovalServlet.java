package com.kiotretail.shared.controller;

import com.kiotretail.employee.dao.EmployeeDAO;
import com.kiotretail.employee.model.Employee;
import com.kiotretail.purchase.dao.PurchaseOrderDAO;
import com.kiotretail.purchase.dto.PendingApprovalItem;
import com.kiotretail.purchase.dto.PurchaseFilterDTO;
import com.kiotretail.purchase.model.PurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.purchase.service.PurchaseService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ApprovalAction;
import com.kiotretail.shared.constant.DocumentStatus;
import com.kiotretail.shared.dao.ApprovalHistoryDAO;
import com.kiotretail.shared.model.ApprovalHistory;
import com.kiotretail.shared.service.ApprovalService;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Approval workflow controller (UC-4.3). Replaces the former mock
 * {@code ApprovalPreviewServlet} with live data sourced from
 * {@link PurchaseOrderDAO}, {@link ApprovalHistoryDAO} and
 * {@link PurchaseService}.
 *
 * <p>Routes:</p>
 * <pre>
 *   GET  /admin/approvals?action=pending                  -&gt; pending-approvals.jsp
 *   GET  /admin/approvals?action=detail&amp;type=..&amp;id=.. -&gt; approval-detail.jsp
 *   GET  /admin/approvals?action=history                   -&gt; approval-history.jsp
 *   POST /admin/approvals?action=approve|reject            -&gt; PRG to ?action=pending
 * </pre>
 *
 * <p>Only the {@code PURCHASE_ORDER} document type is wired today; the queue
 * and history screens are type-aware so other approvable documents can be
 * added later. Flash messaging reuses the shared session keys
 * {@code flashMessage} / {@code messageType}.</p>
 */
@WebServlet("/admin/approvals")
public class ApprovalServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private PurchaseOrderDAO purchaseOrderDAO;
    private ApprovalHistoryDAO historyDAO;
    private PurchaseService purchaseService;
    private ApprovalService approvalService;
    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
        this.historyDAO = new ApprovalHistoryDAO();
        this.purchaseService = new PurchaseService();
        this.approvalService = new ApprovalService();
        this.employeeDAO = new EmployeeDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        syncSessionContext(request);
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "pending");
        switch (action) {
            case "detail":
                handleDetail(request, response);
                break;
            case "history":
                handleHistory(request, response);
                break;
            case "pending":
            default:
                handlePending(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");
        switch (action) {
            case "approve":
            case "reject":
                handleDecision(request, response, action);
                break;
            default:
                setFlash(request, "Hanh dong khong duoc ho tro: " + action, AppConstants.FLASH_DANGER);
                redirect(request, response, "/admin/approvals?action=pending");
                break;
        }
    }

    // ====================================================================
    // GET handlers
    // ====================================================================

    private void handlePending(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Pagination pagination = buildPagination(request);

        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, "");
        String documentType = getStringParam(request, "documentType", "");
        String submittedBy = getStringParam(request, "submittedBy", "");
        String fromDate = getStringParam(request, "fromDate", "");
        String toDate = getStringParam(request, "toDate", "");

        List<PendingApprovalItem> items;
        int total;
        // The pending queue only contains purchase orders today. When a caller
        // narrows to another document type there is nothing to show.
        if (!documentType.isEmpty() && !AppConstants.DOC_TYPE_PURCHASE_ORDER.equals(documentType)) {
            items = Collections.emptyList();
            total = 0;
        } else {
            PurchaseFilterDTO filter = new PurchaseFilterDTO();
            if (!keyword.isEmpty()) {
                filter.setKeyword(keyword);
            }
            Integer createdBy = parseIntOrNull(submittedBy);
            if (createdBy != null) {
                filter.setCreatedBy(createdBy);
            }
            if (!fromDate.isEmpty()) {
                filter.setDateFrom(fromDate);
            }
            if (!toDate.isEmpty()) {
                filter.setDateTo(toDate);
            }
            items = purchaseOrderDAO.findPendingApprovals(filter, pagination);
            total = purchaseOrderDAO.countByStatus(DocumentStatus.PENDING_APPROVAL.name());
        }

        PageResult<PendingApprovalItem> pageResult =
                new PageResult<>(items, total, pagination.getPage(), pagination.getSize());

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute("keyword", keyword);
        request.setAttribute("documentType", documentType);
        request.setAttribute("submittedBy", submittedBy);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("submitters", loadEmployees());
        request.setAttribute("now", new Timestamp(System.currentTimeMillis()));

        forward(request, response, "approval/pending-approvals.jsp");
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = getStringParam(request, "type", AppConstants.DOC_TYPE_PURCHASE_ORDER);
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);

        if (!AppConstants.DOC_TYPE_PURCHASE_ORDER.equals(type) || id <= 0) {
            setFlash(request, "Loai chung tu chua duoc ho tro hoac ma phieu khong hop le.",
                    AppConstants.FLASH_DANGER);
            redirect(request, response, "/admin/approvals?action=pending");
            return;
        }

        PurchaseOrder po = purchaseOrderDAO.getById(id);
        if (po == null) {
            setFlash(request, "Khong tim thay phieu can xem.", AppConstants.FLASH_DANGER);
            redirect(request, response, "/admin/approvals?action=pending");
            return;
        }

        List<PurchaseOrderDetail> details = purchaseService.getOrderDetails(id);
        List<ApprovalHistory> histories =
                historyDAO.getByDocument(AppConstants.DOC_TYPE_PURCHASE_ORDER, id);

        request.setAttribute("document", toDocumentView(po, details));
        request.setAttribute("histories", histories);
        request.setAttribute("canApprove", computeCanApprove(request, po));

        forward(request, response, "approval/approval-detail.jsp");
    }

    private void handleHistory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Pagination pagination = buildPagination(request);

        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, "");
        String historyAction = getStringParam(request, "historyAction", "");
        String documentType = getStringParam(request, "documentType", "");
        String performedBy = getStringParam(request, "performedBy", "");
        String fromDate = getStringParam(request, "fromDate", "");
        String toDate = getStringParam(request, "toDate", "");

        String docTypeFilter = documentType.isEmpty() ? null : documentType;
        ApprovalAction actionFilter = parseAction(historyAction);
        Integer performedByFilter = parseIntOrNull(performedBy);
        LocalDate from = parseDateOrNull(fromDate);
        LocalDate to = parseDateOrNull(toDate);

        List<ApprovalHistory> items = historyDAO.search(
                docTypeFilter, actionFilter, performedByFilter, from, to, pagination);
        int total = historyDAO.countSearch(docTypeFilter, actionFilter, performedByFilter, from, to);

        PageResult<ApprovalHistory> pageResult =
                new PageResult<>(items, total, pagination.getPage(), pagination.getSize());

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute("stats", historyDAO.countByAction());
        request.setAttribute("keyword", keyword);
        request.setAttribute("historyAction", historyAction);
        request.setAttribute("documentType", documentType);
        request.setAttribute("performedBy", performedBy);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("approvers", loadEmployees());
        request.setAttribute("approverName", resolveApproverName(performedByFilter));

        forward(request, response, "approval/approval-history.jsp");
    }

    // ====================================================================
    // POST handler (approve / reject) - PRG
    // ====================================================================

    private void handleDecision(HttpServletRequest request, HttpServletResponse response,
                                String action) throws IOException {
        String type = getStringParam(request, "documentType", AppConstants.DOC_TYPE_PURCHASE_ORDER);
        int id = getIntParam(request, "documentId", 0);
        String reason = getStringParam(request, "reason", "");

        if (!AppConstants.DOC_TYPE_PURCHASE_ORDER.equals(type) || id <= 0) {
            setFlash(request, "Loai chung tu chua duoc ho tro hoac ma phieu khong hop le.",
                    AppConstants.FLASH_DANGER);
            redirect(request, response, "/admin/approvals?action=pending");
            return;
        }

        HttpSession session = request.getSession(false);
        Employee actor = resolveEmployee(session);
        int userId = actor != null ? actor.getEmployeeId() : 0;
        String role = resolveRole(session, actor);

        try {
            // The PurchaseService re-loads the order and re-checks the rule via
            // ApprovalService.canApprove(..)/canReject(..) before mutating state,
            // so authorization is enforced authoritatively in the service layer.
            boolean ok;
            String successMsg;
            if ("approve".equals(action)) {
                ok = purchaseService.approve(id, userId, role);
                successMsg = "Da phe duyet phieu nhap.";
            } else {
                ok = purchaseService.reject(id, userId, role, reason);
                successMsg = "Da tu choi phieu nhap.";
            }
            if (ok) {
                setFlash(request, successMsg, AppConstants.FLASH_SUCCESS);
            } else {
                setFlash(request, "Khong the cap nhat trang thai phieu.", AppConstants.FLASH_DANGER);
            }
        } catch (RuntimeException ex) {
            setFlash(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirect(request, response, "/admin/approvals?action=pending");
    }

    // ====================================================================
    // Helpers
    // ====================================================================

    /**
     * Builds the Map-shaped {@code document} view expected by
     * approval-detail.jsp ({@code documentCode}, {@code partnerName},
     * {@code items}, ...). The detail JSP reads dotted keys via EL, so a Map
     * lets us project a purchase order without coupling the JSP to the
     * PurchaseOrder getter names.
     */
    private Map<String, Object> toDocumentView(PurchaseOrder po, List<PurchaseOrderDetail> details) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("documentId", po.getPurchaseOrderId());
        doc.put("documentCode", po.getOrderCode());
        doc.put("documentType", AppConstants.DOC_TYPE_PURCHASE_ORDER);
        doc.put("status", po.getStatus());
        doc.put("submitterName", po.getCreatedByName());
        doc.put("branchName", po.getBranchName());
        doc.put("partnerName", po.getSupplierName());
        doc.put("note", po.getNote());
        doc.put("totalAmount", po.getTotalAmount());
        doc.put("createdAt", po.getCreatedAt());
        doc.put("submittedAt", po.getSubmittedAt());
        doc.put("items", details);
        return doc;
    }

    /**
     * Mirrors the service-side approval gate so the JSP can show/hide the
     * decision form. The authoritative check still runs in PurchaseService on
     * POST; this is purely for rendering.
     */
    private boolean computeCanApprove(HttpServletRequest request, PurchaseOrder po) {
        HttpSession session = request.getSession(false);
        Employee actor = resolveEmployee(session);
        if (actor == null) {
            return false;
        }
        String role = resolveRole(session, actor);
        String statusStr = po.getStatus() != null ? po.getStatus().trim().toUpperCase() : "";
        DocumentStatus status = DocumentStatus.fromString(statusStr);
        int creatorId = po.getCreatedBy() != null ? po.getCreatedBy() : 0;
        BigDecimal total = po.getTotalAmount();
        return approvalService.canApprove(status, role, creatorId, actor.getEmployeeId(), total);
    }

    /** Loads a bounded employee list for the filter dropdowns. */
    private List<Employee> loadEmployees() {
        return employeeDAO.getAll(Pagination.of(1, AppConstants.MAX_PAGE_SIZE));
    }

    private String resolveApproverName(Integer employeeId) {
        if (employeeId == null || employeeId <= 0) {
            return null;
        }
        Employee e = employeeDAO.getById(employeeId);
        return e != null ? e.getFullName() : null;
    }

    /**
     * Populates the navbar context (display name + pending badge) from the
     * logged-in employee so every approval screen renders consistently.
     */
    private void syncSessionContext(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Employee actor = resolveEmployee(session);
        if (actor != null) {
            session.setAttribute("employeeName", actor.getFullName());
            String role = resolveRole(session, actor);
            int pending;
            if (role != null) {
                pending = purchaseOrderDAO.countPendingForApprover(role, actor.getEmployeeId());
            } else {
                pending = purchaseOrderDAO.countByStatus(DocumentStatus.PENDING_APPROVAL.name());
            }
            session.setAttribute("pendingApprovalCount", pending);
        }
    }

    private Pagination buildPagination(HttpServletRequest request) {
        int page = Math.max(getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE), 1);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        size = Math.max(Math.min(size, AppConstants.MAX_PAGE_SIZE), AppConstants.MIN_PAGE_SIZE);
        return Pagination.of(page, size);
    }

    private Employee resolveEmployee(HttpSession session) {
        Object obj = SessionUtil.getEmployee(session);
        return obj instanceof Employee ? (Employee) obj : null;
    }

    private String resolveRole(HttpSession session, Employee actor) {
        String role = SessionUtil.getRoleName(session);
        if ((role == null || role.isEmpty()) && actor != null) {
            role = actor.getRoleName();
        }
        return role;
    }

    private Integer parseIntOrNull(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private ApprovalAction parseAction(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return ApprovalAction.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private LocalDate parseDateOrNull(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(raw.trim());
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private void setFlash(HttpServletRequest request, String message, String type) {
        HttpSession session = request.getSession();
        session.setAttribute(AppConstants.SESSION_FLASH_MESSAGE, message);
        session.setAttribute(AppConstants.SESSION_FLASH_TYPE, type);
    }
}
