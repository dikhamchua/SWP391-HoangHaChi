package com.kiotretail.purchase.controller;

import com.kiotretail.employee.model.Employee;
import com.kiotretail.product.dao.SupplierDAO;
import com.kiotretail.product.model.Product;
import com.kiotretail.product.model.Supplier;
import com.kiotretail.product.service.ProductService;
import com.kiotretail.purchase.model.PurchaseOrder;
import com.kiotretail.purchase.model.PurchaseOrderDetail;
import com.kiotretail.purchase.dto.PurchaseFilterDTO;
import com.kiotretail.purchase.service.PurchaseService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.base.PageResult;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.DocumentStatus;
import com.kiotretail.shared.constant.ErrorMessages;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.dao.ApprovalHistoryDAO;
import com.kiotretail.shared.exception.ServiceException;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Purchase Order servlet (UC-4.2 / UC-4.3).
 *
 * <p>Handles listing, viewing, creating, editing, receiving and printing
 * purchase orders plus the full approval state machine
 * (submit / approve / reject / cancel / receive).</p>
 *
 * <p>All POST handlers follow the PRG pattern: re-load the order from the DB,
 * re-check permission server-side via {@link ApprovalService}, invoke the
 * matching {@link PurchaseService} method, set a flash message and then
 * {@code sendRedirect} to the detail view. Legacy lower-case status actions
 * (confirm / receive / cancel-without-reason) are kept working alongside the
 * new approval vocabulary.</p>
 */
@WebServlet("/admin/purchases")
public class PurchaseServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String REDIRECT_LIST = ViewPaths.REDIRECT_PURCHASES;
    private static final String VIEW_LIST = ViewPaths.PURCHASE_LIST;
    private static final String VIEW_CREATE = ViewPaths.PURCHASE_CREATE;
    private static final String VIEW_EDIT = ViewPaths.PURCHASE_EDIT;
    private static final String VIEW_DETAIL = ViewPaths.PURCHASE_DETAIL;
    private static final String VIEW_RECEIVE = ViewPaths.PURCHASE_RECEIVE;
    private static final String VIEW_PRINT = ViewPaths.PURCHASE_PRINT;

    private static final String ATTR_OWNER_THRESHOLD = "ownerThreshold";
    private static final String RECEIVE_PARAM_PREFIX = "receivedQty[";

    private PurchaseService purchaseService;
    private SupplierDAO supplierDAO;
    private ProductService productService;
    private ApprovalService approvalService;
    private ApprovalHistoryDAO approvalHistoryDAO;

    @Override
    public void init() throws ServletException {
        this.purchaseService = new PurchaseService();
        this.supplierDAO = new SupplierDAO();
        this.productService = new ProductService();
        this.approvalService = new ApprovalService();
        this.approvalHistoryDAO = new ApprovalHistoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, AppConstants.PARAM_ACTION, "list");

        try {
            switch (action) {
                case "view":
                    handleView(request, response);
                    break;
                case "create":
                    handleCreateForm(request, response);
                    break;
                case "edit":
                    handleEditForm(request, response);
                    break;
                case "receive":
                    handleReceiveForm(request, response);
                    break;
                case "print":
                    handlePrint(request, response);
                    break;
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = getStringParam(request, AppConstants.PARAM_ACTION, "");

        switch (action) {
            case AppConstants.ACTION_ADD:
                handleAdd(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "submit":
                handleSubmit(request, response);
                break;
            case "approve":
                handleApprove(request, response);
                break;
            case "reject":
                handleReject(request, response);
                break;
            case "cancel":
                handleCancel(request, response);
                break;
            case "receiveSubmit":
                handleReceiveSubmit(request, response);
                break;
            // Legacy lower-case transitions kept for backward compatibility.
            case "confirm":
                handleConfirm(request, response);
                break;
            case "receive":
                handleReceiveLegacy(request, response);
                break;
            default:
                redirect(request, response, REDIRECT_LIST);
                break;
        }
    }

    // -----------------------------------------------------------------------
    // GET handlers
    // -----------------------------------------------------------------------

    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int page = getIntParam(request, AppConstants.PARAM_PAGE, AppConstants.DEFAULT_PAGE);
        int size = getIntParam(request, "size", AppConstants.DEFAULT_PAGE_SIZE);
        Pagination pagination = Pagination.of(
                Math.max(page, 1),
                Math.max(Math.min(size, AppConstants.MAX_PAGE_SIZE), 1));

        String keyword = getStringParam(request, AppConstants.PARAM_KEYWORD, null);
        String status = getStringParam(request, AppConstants.PARAM_STATUS, null);
        String approvalStatus = getStringParam(request, "approvalStatus", null);

        PurchaseFilterDTO filter = new PurchaseFilterDTO();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        filter.setApprovalStatus(approvalStatus);
        PageResult<PurchaseOrder> pageResult = purchaseService.listOrders(filter, pagination);

        request.setAttribute(AppConstants.ATTR_PAGE_RESULT, pageResult);
        request.setAttribute(AppConstants.PARAM_KEYWORD, keyword);
        request.setAttribute(AppConstants.PARAM_STATUS, status);

        forward(request, response, VIEW_LIST);
    }

    private void handleView(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        PurchaseOrder order = purchaseService.getOrderById(id);
        List<PurchaseOrderDetail> details = purchaseService.getOrderDetails(id);
        List<ApprovalHistory> approvalHistory =
                approvalHistoryDAO.getByDocument(AppConstants.DOC_TYPE_PURCHASE_ORDER, id);

        request.setAttribute(AppConstants.ATTR_ORDER, order);
        request.setAttribute(AppConstants.ATTR_ORDER_DETAILS, details);
        request.setAttribute("approvalHistory", approvalHistory);
        request.setAttribute(ATTR_OWNER_THRESHOLD, AppConstants.OWNER_APPROVAL_THRESHOLD);

        forward(request, response, VIEW_DETAIL);
    }

    private void handleCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        populateFormLookups(request);
        forward(request, response, VIEW_CREATE);
    }

    /**
     * Edit form. Only DRAFT orders are editable: any other status sets an error
     * flash and redirects back to the detail view (never forwards to the JSP).
     */
    private void handleEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        PurchaseOrder order = purchaseService.getOrderById(id);

        if (order.getStatusEnum() != DocumentStatus.DRAFT) {
            setFlashMessage(request, ErrorMessages.PO_INVALID_STATUS, AppConstants.FLASH_DANGER);
            redirectToView(request, response, id);
            return;
        }

        List<PurchaseOrderDetail> details = purchaseService.getOrderDetails(id);
        populateFormLookups(request);
        request.setAttribute(AppConstants.ATTR_ORDER, order);
        request.setAttribute(AppConstants.ATTR_ORDER_DETAILS, details);

        forward(request, response, VIEW_EDIT);
    }

    /**
     * Receive (stock-in) form. Available only while the order is APPROVED or
     * RECEIVING; otherwise an error flash is shown and we redirect to detail.
     */
    private void handleReceiveForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        PurchaseOrder order = purchaseService.getOrderById(id);

        DocumentStatus status = order.getStatusEnum();
        if (status != DocumentStatus.APPROVED && status != DocumentStatus.RECEIVING) {
            setFlashMessage(request, ErrorMessages.PO_INVALID_STATUS, AppConstants.FLASH_DANGER);
            redirectToView(request, response, id);
            return;
        }

        List<PurchaseOrderDetail> details = purchaseService.getOrderDetails(id);
        request.setAttribute(AppConstants.ATTR_ORDER, order);
        request.setAttribute(AppConstants.ATTR_ORDER_DETAILS, details);

        forward(request, response, VIEW_RECEIVE);
    }

    private void handlePrint(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        PurchaseOrder order = purchaseService.getOrderById(id);
        List<PurchaseOrderDetail> details = purchaseService.getOrderDetails(id);

        request.setAttribute(AppConstants.ATTR_ORDER, order);
        request.setAttribute(AppConstants.ATTR_ORDER_DETAILS, details);

        forward(request, response, VIEW_PRINT);
    }

    // -----------------------------------------------------------------------
    // POST handlers (new approval workflow)
    // -----------------------------------------------------------------------

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            PurchaseOrder order = buildOrderFromRequest(request);
            List<PurchaseOrderDetail> details = buildDetailsFromRequest(request);
            int newId = purchaseService.createOrder(order, details);
            setFlashMessage(request,
                    String.format(ErrorMessages.CREATE_SUCCESS, "phieu nhap"),
                    AppConstants.FLASH_SUCCESS);
            redirectToView(request, response, newId);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST + "?action=create");
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST + "?action=create");
        }
    }

    /** Save edits to a DRAFT order (header + line items). */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        try {
            PurchaseOrder existing = purchaseService.getOrderById(id);
            if (existing.getStatusEnum() != DocumentStatus.DRAFT) {
                setFlashMessage(request, ErrorMessages.PO_INVALID_STATUS, AppConstants.FLASH_DANGER);
                redirectToView(request, response, id);
                return;
            }

            PurchaseOrder order = buildOrderFromRequest(request);
            List<PurchaseOrderDetail> details = buildDetailsFromRequest(request);
            purchaseService.updateDraft(id, order, details, currentUserId(request));

            setFlashMessage(request,
                    String.format(ErrorMessages.UPDATE_SUCCESS, "phieu nhap"),
                    AppConstants.FLASH_SUCCESS);
            redirectToView(request, response, id);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST + "?action=edit&id=" + id);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
            redirect(request, response, REDIRECT_LIST + "?action=edit&id=" + id);
        }
    }

    private void handleSubmit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        try {
            PurchaseOrder order = purchaseService.getOrderById(id);
            if (!approvalService.canSubmit(order.getStatusEnum())) {
                setFlashMessage(request, ErrorMessages.PO_INVALID_STATUS, AppConstants.FLASH_DANGER);
                redirectToView(request, response, id);
                return;
            }
            purchaseService.submit(id, currentUserId(request));
            setFlashMessage(request, "Da gui phieu nhap cho cap duyet", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    private void handleApprove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        try {
            PurchaseOrder order = purchaseService.getOrderById(id);
            int userId = currentUserId(request);
            String role = currentRole(request);
            int creatorId = order.getCreatedBy() != null ? order.getCreatedBy() : 0;

            if (!approvalService.canApprove(order.getStatusEnum(), role, creatorId, userId,
                    order.getTotalAmount())) {
                setFlashMessage(request, permissionMessage(order, userId, creatorId),
                        AppConstants.FLASH_DANGER);
                redirectToView(request, response, id);
                return;
            }
            purchaseService.approve(id, userId, role);
            setFlashMessage(request, "Da duyet phieu nhap", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    private void handleReject(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        try {
            PurchaseOrder order = purchaseService.getOrderById(id);
            String role = currentRole(request);
            if (!approvalService.canReject(order.getStatusEnum(), role)) {
                setFlashMessage(request, ErrorMessages.PO_NO_PERMISSION, AppConstants.FLASH_DANGER);
                redirectToView(request, response, id);
                return;
            }
            String reason = getStringParam(request, "reason", null);
            purchaseService.reject(id, currentUserId(request), role, reason);
            setFlashMessage(request, "Da tu choi phieu nhap", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        if (id <= 0) {
            // Legacy detail form posts the id under "purchaseOrderId".
            id = getIntParam(request, "purchaseOrderId", 0);
        }
        try {
            PurchaseOrder order = purchaseService.getOrderById(id);
            int userId = currentUserId(request);
            String role = currentRole(request);
            boolean isOwner = order.getCreatedBy() != null && order.getCreatedBy() == userId;

            if (!approvalService.canCancel(order.getStatusEnum(), role, isOwner)) {
                setFlashMessage(request, ErrorMessages.PO_NO_PERMISSION, AppConstants.FLASH_DANGER);
                redirectToView(request, response, id);
                return;
            }
            String reason = getStringParam(request, "reason", null);
            purchaseService.cancel(id, userId, role, reason);
            setFlashMessage(request, "Da huy phieu nhap", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    /**
     * Partial / full goods receipt. Reads {@code receivedQty[poDetailId]} params
     * into a detailId -&gt; quantity map and delegates to the service, which
     * updates stock and advances the order to RECEIVING / COMPLETED.
     */
    private void handleReceiveSubmit(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, AppConstants.PARAM_ID, 0);
        try {
            Map<Integer, Integer> receivedByDetail = parseReceivedQuantities(request);
            purchaseService.receive(id, receivedByDetail, currentUserId(request));
            setFlashMessage(request, "Da nhap kho thanh cong", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    // -----------------------------------------------------------------------
    // POST handlers (legacy lower-case transitions)
    // -----------------------------------------------------------------------

    private void handleConfirm(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, "purchaseOrderId", 0);
        try {
            purchaseService.confirmOrder(id);
            setFlashMessage(request, "Da xac nhan phieu nhap", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    private void handleReceiveLegacy(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = getIntParam(request, "purchaseOrderId", 0);
        try {
            purchaseService.receiveGoods(id);
            setFlashMessage(request, "Da nhap kho thanh cong", AppConstants.FLASH_SUCCESS);
        } catch (ServiceException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        } catch (RuntimeException ex) {
            setFlashMessage(request, ex.getMessage(), AppConstants.FLASH_DANGER);
        }
        redirectToView(request, response, id);
    }

    // -----------------------------------------------------------------------
    // Builders
    // -----------------------------------------------------------------------

    private PurchaseOrder buildOrderFromRequest(HttpServletRequest request) {
        PurchaseOrder po = new PurchaseOrder();
        po.setSupplierId(getIntParam(request, "supplierId", 0));
        int branchId = getIntParam(request, "branchId", 0);
        if (branchId <= 0) {
            HttpSession session = request.getSession(false);
            Integer sessionBranch = SessionUtil.getBranchId(session);
            branchId = sessionBranch != null ? sessionBranch : 0;
        }
        po.setBranchId(branchId);

        // Resolve current employee from session
        HttpSession session = request.getSession(false);
        Object empObj = SessionUtil.getEmployee(session);
        if (empObj instanceof Employee) {
            po.setEmployeeId(((Employee) empObj).getEmployeeId());
        }

        po.setNote(getStringParam(request, "note", null));
        po.setStatus(DocumentStatus.DRAFT.name());
        return po;
    }

    private List<PurchaseOrderDetail> buildDetailsFromRequest(HttpServletRequest request) {
        List<PurchaseOrderDetail> details = new ArrayList<>();

        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] unitCosts = request.getParameterValues("unitCost");

        if (productIds == null) {
            return details;
        }

        for (int i = 0; i < productIds.length; i++) {
            int productId = parseInt(productIds[i], 0);
            int quantity = i < (quantities == null ? 0 : quantities.length)
                    ? parseInt(quantities[i], 0) : 0;
            BigDecimal unitCost = i < (unitCosts == null ? 0 : unitCosts.length)
                    ? parseBigDecimal(unitCosts[i]) : BigDecimal.ZERO;

            if (productId <= 0 || quantity <= 0) {
                continue;
            }

            PurchaseOrderDetail d = new PurchaseOrderDetail();
            d.setProductId(productId);
            d.setQuantity(quantity);
            d.setUnitCost(unitCost);
            d.setSubtotal(unitCost.multiply(BigDecimal.valueOf(quantity)));
            details.add(d);
        }
        return details;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void populateFormLookups(HttpServletRequest request) {
        List<Supplier> suppliers = supplierDAO.getActive();
        List<Product> products = productService.searchProducts("", 200);
        if (products == null || products.isEmpty()) {
            products = productService.searchProducts("a", 500);
        }
        request.setAttribute(AppConstants.ATTR_SUPPLIERS, suppliers);
        request.setAttribute(AppConstants.ATTR_PRODUCTS, products);
    }

    /** Reads {@code receivedQty[<detailId>]} request params into a map. */
    private Map<Integer, Integer> parseReceivedQuantities(HttpServletRequest request) {
        Map<Integer, Integer> received = new HashMap<>();
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.startsWith(RECEIVE_PARAM_PREFIX) && name.endsWith("]")) {
                String inner = name.substring(RECEIVE_PARAM_PREFIX.length(), name.length() - 1);
                int detailId = parseInt(inner, 0);
                int qty = parseInt(request.getParameter(name), 0);
                if (detailId > 0) {
                    received.put(detailId, qty);
                }
            }
        }
        return received;
    }

    private int currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object empObj = SessionUtil.getEmployee(session);
        if (empObj instanceof Employee) {
            return ((Employee) empObj).getEmployeeId();
        }
        return 0;
    }

    private String currentRole(HttpServletRequest request) {
        return SessionUtil.getRoleName(request.getSession(false));
    }

    /** Picks the most helpful denial message for an approve attempt. */
    private String permissionMessage(PurchaseOrder order, int userId, int creatorId) {
        if (creatorId == userId) {
            return ErrorMessages.PO_CREATOR_CANNOT_APPROVE;
        }
        BigDecimal total = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        if (total.compareTo(AppConstants.OWNER_APPROVAL_THRESHOLD) >= 0) {
            return ErrorMessages.PO_OWNER_REQUIRED;
        }
        return ErrorMessages.PO_NO_PERMISSION;
    }

    private void redirectToView(HttpServletRequest request, HttpServletResponse response, int id)
            throws IOException {
        redirect(request, response, REDIRECT_LIST + "?action=view&id=" + id);
    }

    private int parseInt(String raw, int defaultValue) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private BigDecimal parseBigDecimal(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void setFlashMessage(HttpServletRequest request, String message, String type) {
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_MESSAGE, message);
        request.getSession().setAttribute(AppConstants.SESSION_FLASH_TYPE, type);
    }
}
