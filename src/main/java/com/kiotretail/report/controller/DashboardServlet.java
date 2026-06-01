package com.kiotretail.report.controller;

import com.kiotretail.report.service.DashboardService;
import com.kiotretail.shared.base.BaseServlet;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ViewPaths;
import com.kiotretail.shared.util.SessionUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Servlet
 * Renders the admin dashboard with summary metrics, top selling products
 * and revenue chart over the last 7 days.
 */
@WebServlet("/admin/dashboard")
public class DashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW_DASHBOARD = ViewPaths.DASHBOARD;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {
        this.dashboardService = new DashboardService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer branchId = SessionUtil.getBranchId(request.getSession(false));

        Map<String, Object> summary = dashboardService.getDashboardSummary(branchId);
        List<Map<String, Object>> topProducts = dashboardService.getTopProducts(5);

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        String dateFrom = sevenDaysAgo.format(DATE_FORMATTER) + " 00:00:00";
        String dateTo = today.format(DATE_FORMATTER) + " 23:59:59";

        List<Map<String, Object>> revenueChart =
                dashboardService.getRevenueChart(dateFrom, dateTo, branchId);

        request.setAttribute(AppConstants.ATTR_SUMMARY, summary);
        request.setAttribute(AppConstants.ATTR_TOP_PRODUCTS, topProducts);
        request.setAttribute(AppConstants.ATTR_REVENUE_DATA, revenueChart);

        forward(request, response, VIEW_DASHBOARD);
    }
}
