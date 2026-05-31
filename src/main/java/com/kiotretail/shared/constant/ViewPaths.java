package com.kiotretail.shared.constant;

public final class ViewPaths {
    private ViewPaths() {}

    // Auth views
    public static final String LOGIN = "auth/login.jsp";
    public static final String REGISTER = "auth/register.jsp";
    public static final String FORGOT_PASSWORD = "auth/forgot-password.jsp";
    public static final String CHANGE_PASSWORD = "auth/change-password.jsp";
    public static final String ROLE_SELECTION = "auth/role-selection.jsp";

    // Product views
    public static final String PRODUCT_LIST = "product/products.jsp";
    public static final String PRODUCT_CREATE = "product/product-create.jsp";
    public static final String PRODUCT_DETAIL = "product/product-detail.jsp";
    public static final String PRODUCT_EDIT = "product/product-edit.jsp";
    public static final String CATEGORY_LIST = "product/categories.jsp";

    // Customer views
    public static final String CUSTOMER_LIST = "customer/customers.jsp";
    public static final String CUSTOMER_CREATE = "customer/customer-create.jsp";
    public static final String CUSTOMER_EDIT = "customer/customer-edit.jsp";
    public static final String CUSTOMER_DETAIL = "customer/customer-detail.jsp";

    // Employee views
    public static final String EMPLOYEE_LIST = "employee/employees.jsp";
    public static final String EMPLOYEE_CREATE = "employee/employee-create.jsp";
    public static final String EMPLOYEE_EDIT = "employee/employee-edit.jsp";
    public static final String EMPLOYEE_DETAIL = "employee/employee-detail.jsp";

    // Branch views
    public static final String BRANCH_LIST = "branch/branches.jsp";
    public static final String BRANCH_CREATE = "branch/branch-create.jsp";
    public static final String BRANCH_EDIT = "branch/branch-edit.jsp";
    public static final String BRANCH_DETAIL = "branch/branch-detail.jsp";

    // Supplier views
    public static final String SUPPLIER_LIST = "supplier/suppliers.jsp";
    public static final String SUPPLIER_CREATE = "supplier/supplier-create.jsp";
    public static final String SUPPLIER_EDIT = "supplier/supplier-edit.jsp";
    public static final String SUPPLIER_DETAIL = "supplier/supplier-detail.jsp";

    // Invoice views
    public static final String INVOICE_LIST = "invoice/invoices.jsp";
    public static final String INVOICE_DETAIL = "invoice/invoice-detail.jsp";
    public static final String INVOICE_CREATE = "invoice/invoice-create.jsp";

    // POS views
    public static final String POS_SALE = "pos/sale.jsp";

    // Report views
    public static final String DASHBOARD = "report/dashboard.jsp";
    public static final String SALES_REPORT = "report/sales-report.jsp";

    // Redirect URLs
    public static final String REDIRECT_LOGIN = "/login";
    public static final String REDIRECT_DASHBOARD = "/admin/dashboard";
    public static final String REDIRECT_PRODUCTS = "/admin/products";
    public static final String REDIRECT_CATEGORIES = "/admin/categories";
    public static final String REDIRECT_CUSTOMERS = "/admin/customers";
    public static final String REDIRECT_EMPLOYEES = "/admin/employees";
    public static final String REDIRECT_BRANCHES = "/admin/branches";
    public static final String REDIRECT_SUPPLIERS = "/admin/suppliers";
    public static final String REDIRECT_INVOICES = "/admin/invoices";
    public static final String REDIRECT_REPORTS = "/admin/reports";
    public static final String REDIRECT_POS = "/pos/sale";
    public static final String REDIRECT_ROLE_SELECTION = "/role-selection";
}
