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
 * Report Servlet
 * Renders the sales report page with aggregated revenue, order counts,
 * revenue chart and top selling products for the selected period.
 */
@WebServlet("/admin/reports")
public class ReportServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW_REPORT = ViewPaths.SALES_REPORT;
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

        String dateFromParam = getStringParam(request, "dateFrom", null);
        String dateToParam = getStringParam(request, "dateTo", null);

        LocalDate today = LocalDate.now();
        LocalDate fromDate = dateFromParam != null ? parseDate(dateFromParam, today.minusDays(30)) : today.minusDays(30);
        LocalDate toDate = dateToParam != null ? parseDate(dateToParam, today) : today;

        String dateFrom = fromDate.format(DATE_FORMATTER) + " 00:00:00";
        String dateTo = toDate.format(DATE_FORMATTER) + " 23:59:59";

        Map<String, Object> report = dashboardService.getSalesReport(dateFrom, dateTo, branchId);
        List<Map<String, Object>> revenueChart = dashboardService.getRevenueChart(dateFrom, dateTo, branchId);
        List<Map<String, Object>> topProducts = dashboardService.getTopProducts(10);

        request.setAttribute(AppConstants.ATTR_REPORT, report);
        request.setAttribute(AppConstants.ATTR_REVENUE_DATA, revenueChart);
        request.setAttribute(AppConstants.ATTR_TOP_PRODUCTS, topProducts);
        request.setAttribute(AppConstants.ATTR_DATE_FROM, fromDate.format(DATE_FORMATTER));
        request.setAttribute(AppConstants.ATTR_DATE_TO, toDate.format(DATE_FORMATTER));

        forward(request, response, VIEW_REPORT);
    }

    private LocalDate parseDate(String raw, LocalDate fallback) {
        try {
            return LocalDate.parse(raw, DATE_FORMATTER);
        } catch (Exception ex) {
            return fallback;
        }
    }
}
