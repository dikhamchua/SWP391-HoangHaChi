package com.kiotretail.shared.constant;

public final class AppConstants {
    private AppConstants() {}

    // Pagination
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 15;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int MIN_PAGE_SIZE = 1;

    // Status
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELLED = "cancelled";

    // Session Keys
    public static final String SESSION_EMPLOYEE = "employee";
    public static final String SESSION_ROLE_NAME = "roleName";
    public static final String SESSION_BRANCH_ID = "branchId";
    public static final String SESSION_FLASH_MESSAGE = "flashMessage";
    public static final String SESSION_FLASH_ERROR = "flashError";
    public static final String SESSION_FLASH_TYPE = "messageType";

    // Request Attribute Keys
    public static final String ATTR_ERROR = "error";
    public static final String ATTR_ERROR_MESSAGE = "errorMessage";
    public static final String ATTR_PAGE_RESULT = "pageResult";
    public static final String ATTR_FILTER = "filter";
    public static final String ATTR_ROLES = "roles";
    public static final String ATTR_BRANCHES = "branches";
    public static final String ATTR_EMPLOYEE = "employee";
    public static final String ATTR_CUSTOMER = "customer";
    public static final String ATTR_PRODUCT = "product";
    public static final String ATTR_CATEGORIES = "categories";
    public static final String ATTR_SUPPLIERS = "suppliers";
    public static final String ATTR_ORDER = "order";
    public static final String ATTR_ORDER_DETAILS = "orderDetails";
    public static final String ATTR_PAYMENTS = "payments";
    public static final String ATTR_CART = "cart";
    public static final String ATTR_PRODUCTS = "products";
    public static final String ATTR_EMAIL = "email";
    public static final String ATTR_FULL_NAME = "fullName";
    public static final String ATTR_PHONE = "phone";

    // Report Attributes
    public static final String ATTR_TOTAL_REVENUE = "totalRevenue";
    public static final String ATTR_TOTAL_ORDERS = "totalOrders";
    public static final String ATTR_TOTAL_PRODUCTS = "totalProducts";
    public static final String ATTR_TOTAL_CUSTOMERS = "totalCustomers";
    public static final String ATTR_TOP_PRODUCTS = "topProducts";
    public static final String ATTR_RECENT_ORDERS = "recentOrders";
    public static final String ATTR_REVENUE_DATA = "revenueData";
    public static final String ATTR_SUMMARY = "summary";
    public static final String ATTR_REPORT = "report";
    public static final String ATTR_DATE_FROM = "dateFrom";
    public static final String ATTR_DATE_TO = "dateTo";

    // Flash Message Types
    public static final String FLASH_SUCCESS = "success";
    public static final String FLASH_DANGER = "danger";
    public static final String FLASH_WARNING = "warning";

    // Roles
    public static final String ROLE_OWNER = "Owner";
    public static final String ROLE_STORE_MANAGER = "StoreManager";
    public static final String ROLE_SALES_STAFF = "SalesStaff";
    public static final String ROLE_WAREHOUSE_STAFF = "WarehouseStaff";

    // Order Types
    public static final String ORDER_TYPE_SALE = "sale";
    public static final String ORDER_TYPE_PURCHASE = "purchase";
    public static final String ORDER_TYPE_RETURN = "return";

    // Payment Methods
    public static final String PAYMENT_CASH = "cash";
    public static final String PAYMENT_CARD = "card";
    public static final String PAYMENT_TRANSFER = "transfer";

    // Membership Tiers
    public static final String TIER_MEMBER = "member";
    public static final String TIER_SILVER = "silver";
    public static final String TIER_GOLD = "gold";
    public static final String TIER_PLATINUM = "platinum";

    // Parameter Names
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_LIMIT = "limit";
    public static final String PARAM_KEYWORD = "keyword";
    public static final String PARAM_STATUS = "status";
    public static final String PARAM_CATEGORY_ID = "categoryId";
    public static final String PARAM_ACTION = "action";
    public static final String PARAM_ID = "id";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_NEW_PASSWORD = "newPassword";
    public static final String PARAM_CURRENT_PASSWORD = "currentPassword";
    public static final String PARAM_CONFIRM_PASSWORD = "confirmPassword";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_FULL_NAME = "fullName";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_PARENT_ID = "parentId";
    public static final String PARAM_AMOUNT = "amount";
    public static final String PARAM_UNIT_PRICE = "unitPrice";
    public static final String PARAM_COST_PRICE = "costPrice";
    public static final String PARAM_PRICE = "price";
    public static final String PARAM_DISCOUNT = "discount";
    public static final String PARAM_DATE_OF_BIRTH = "dateOfBirth";

    // Actions
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_ADD = "add";
    public static final String ACTION_REMOVE = "remove";
    public static final String ACTION_CHECKOUT = "checkout";
    public static final String ACTION_CLEAR = "clear";

    // API
    public static final int HTTP_OK = 200;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_SERVER_ERROR = 500;
    public static final String API_NOT_FOUND = "Not Found";
}