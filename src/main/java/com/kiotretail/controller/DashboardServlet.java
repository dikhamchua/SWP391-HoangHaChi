package com.kiotretail.controller;

import com.kiotretail.dao.ProductDAO;
import com.kiotretail.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Dashboard Servlet
 * Hiển thị trang tổng quan
 */
public class DashboardServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get statistics (simplified for demo)
        List<Product> products = productDAO.getAllProducts();
        int totalProducts = products.size();
        int lowStockProducts = (int) products.stream().filter(Product::isLowStock).count();
        int outOfStockProducts = (int) products.stream().filter(Product::isOutOfStock).count();

        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("lowStockProducts", lowStockProducts);
        request.setAttribute("outOfStockProducts", outOfStockProducts);
        request.setAttribute("recentProducts", products.stream().limit(5).toArray());

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }
}
