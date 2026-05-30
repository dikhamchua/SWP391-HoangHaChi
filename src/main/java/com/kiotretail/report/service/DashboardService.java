package com.kiotretail.report.service;

import com.kiotretail.report.dao.ReportDAO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service providing dashboard summary data and report aggregations.
 */
public class DashboardService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ReportDAO reportDAO = new ReportDAO();

    /**
     * Returns today's high-level dashboard summary.
     *
     * @param branchId optional branch filter, may be {@code null} for all branches
     * @return map with keys: todayRevenue, todayOrders, totalCustomers, totalProducts
     */
    public Map<String, Object> getDashboardSummary(Integer branchId) {
        LocalDate today = LocalDate.now();
        String startOfDay = today.format(DATE_FORMATTER) + " 00:00:00";
        String now = today.format(DATE_FORMATTER) + " 23:59:59";

        Map<String, Object> summary = new HashMap<>();
        try {
            BigDecimal todayRevenue = reportDAO.getTotalRevenue(startOfDay, now, branchId);
            int todayOrders = reportDAO.getTotalOrders(startOfDay, now, branchId);
            int totalCustomers = reportDAO.getTotalCustomers();
            int totalProducts = reportDAO.getTotalProducts();

            summary.put("todayRevenue", todayRevenue != null ? todayRevenue : BigDecimal.ZERO);
            summary.put("todayOrders", todayOrders);
            summary.put("totalCustomers", totalCustomers);
            summary.put("totalProducts", totalProducts);
        } catch (Exception e) {
            summary.put("todayRevenue", BigDecimal.ZERO);
            summary.put("todayOrders", 0);
            summary.put("totalCustomers", 0);
            summary.put("totalProducts", 0);
            summary.put("error", e.getMessage());
        }
        return summary;
    }

    /**
     * Returns top selling products over the last 30 days.
     */
    public List<Map<String, Object>> getTopProducts(int limit) {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysAgo = today.minusDays(30);
        String dateFrom = thirtyDaysAgo.format(DATE_FORMATTER) + " 00:00:00";
        String dateTo = today.format(DATE_FORMATTER) + " 23:59:59";
        return reportDAO.getTopProducts(limit, dateFrom, dateTo);
    }

    /**
     * Returns revenue chart data grouped by date.
     */
    public List<Map<String, Object>> getRevenueChart(String dateFrom, String dateTo, Integer branchId) {
        return reportDAO.getRevenueByDate(dateFrom, dateTo, branchId);
    }

    /**
     * Returns aggregated sales report for the given period.
     *
     * @return map with keys: revenue, orderCount, averageOrderValue
     */
    public Map<String, Object> getSalesReport(String dateFrom, String dateTo, Integer branchId) {
        Map<String, Object> report = new HashMap<>();
        try {
            BigDecimal revenue = reportDAO.getTotalRevenue(dateFrom, dateTo, branchId);
            int orderCount = reportDAO.getTotalOrders(dateFrom, dateTo, branchId);
            BigDecimal safeRevenue = revenue != null ? revenue : BigDecimal.ZERO;

            BigDecimal averageOrderValue = BigDecimal.ZERO;
            if (orderCount > 0) {
                averageOrderValue = safeRevenue.divide(
                        BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP);
            }

            report.put("revenue", safeRevenue);
            report.put("orderCount", orderCount);
            report.put("averageOrderValue", averageOrderValue);
        } catch (Exception e) {
            report.put("revenue", BigDecimal.ZERO);
            report.put("orderCount", 0);
            report.put("averageOrderValue", BigDecimal.ZERO);
            report.put("error", e.getMessage());
        }
        return report;
    }
}
