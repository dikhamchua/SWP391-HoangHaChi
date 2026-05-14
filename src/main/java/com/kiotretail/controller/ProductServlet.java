package com.kiotretail.controller;

import com.kiotretail.dao.ProductDAO;
import com.kiotretail.model.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Product Servlet
 * Xử lý quản lý hàng hóa
 */
public class ProductServlet extends HttpServlet {

    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "list":
                listProducts(request, response);
                break;
            case "search":
                searchProducts(request, response);
                break;
            case "view":
                viewProduct(request, response);
                break;
            default:
                listProducts(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action) {
            case "add":
                addProduct(request, response);
                break;
            case "update":
                updateProduct(request, response);
                break;
            case "delete":
                deleteProduct(request, response);
                break;
            default:
                listProducts(request, response);
        }
    }

    private void listProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Product> products = productDAO.getAllProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
    }

    private void searchProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Product> products = productDAO.searchProducts(keyword);
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp").forward(request, response);
    }

    private void viewProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int productId = Integer.parseInt(request.getParameter("id"));
        Product product = productDAO.getProductById(productId);
        request.setAttribute("product", product);
        request.getRequestDispatcher("/WEB-INF/views/admin/product-detail.jsp").forward(request, response);
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Product product = new Product();
            product.setProductCode(request.getParameter("productCode"));
            product.setProductName(request.getParameter("productName"));
            product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            product.setUnit(request.getParameter("unit"));
            product.setCostPrice(new BigDecimal(request.getParameter("costPrice")));
            product.setSellingPrice(new BigDecimal(request.getParameter("sellingPrice")));
            product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            product.setMinStock(Integer.parseInt(request.getParameter("minStock")));
            product.setStatus("active");

            boolean success = productDAO.addProduct(product);

            if (success) {
                request.getSession().setAttribute("message", "Thêm sản phẩm thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Thêm sản phẩm thất bại!");
                request.getSession().setAttribute("messageType", "danger");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Product product = new Product();
            product.setProductId(Integer.parseInt(request.getParameter("productId")));
            product.setProductName(request.getParameter("productName"));
            product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            product.setUnit(request.getParameter("unit"));
            product.setCostPrice(new BigDecimal(request.getParameter("costPrice")));
            product.setSellingPrice(new BigDecimal(request.getParameter("sellingPrice")));
            product.setStockQuantity(Integer.parseInt(request.getParameter("stockQuantity")));
            product.setMinStock(Integer.parseInt(request.getParameter("minStock")));
            product.setStatus(request.getParameter("status"));

            boolean success = productDAO.updateProduct(product);

            if (success) {
                request.getSession().setAttribute("message", "Cập nhật sản phẩm thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Cập nhật sản phẩm thất bại!");
                request.getSession().setAttribute("messageType", "danger");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            boolean success = productDAO.deleteProduct(productId);

            if (success) {
                request.getSession().setAttribute("message", "Xóa sản phẩm thành công!");
                request.getSession().setAttribute("messageType", "success");
            } else {
                request.getSession().setAttribute("message", "Xóa sản phẩm thất bại!");
                request.getSession().setAttribute("messageType", "danger");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("message", "Lỗi: " + e.getMessage());
            request.getSession().setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }
}
