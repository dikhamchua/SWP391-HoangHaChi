package com.kiotretail.controller;

import com.kiotretail.dao.CategoryDAO;
import com.kiotretail.dao.ProductDAO;
import com.kiotretail.model.Category;
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
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
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
        List<Category> categories = categoryDAO.getActiveCategories();
        request.setAttribute("categories", categories);
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
            String sku = request.getParameter("sku");
            String name = request.getParameter("name");
            String categoryId = request.getParameter("categoryId");
            String price = request.getParameter("price");
            String costPrice = request.getParameter("costPrice");
            String stockAlertQty = request.getParameter("stockAlertQty");
            String status = request.getParameter("status");

            if (sku == null || sku.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                categoryId == null || categoryId.trim().isEmpty() ||
                price == null || price.trim().isEmpty()) {
                request.getSession().setAttribute("message", "Vui lòng nhập đầy đủ SKU, tên, nhóm hàng và giá bán.");
                request.getSession().setAttribute("messageType", "danger");
                response.sendRedirect(request.getContextPath() + "/admin/products");
                return;
            }

            product.setProductCode(sku.trim());
            product.setProductName(name.trim());
            product.setCategoryId(Integer.parseInt(categoryId));
            product.setCostPrice(new BigDecimal(costPrice == null || costPrice.trim().isEmpty() ? "0" : costPrice));
            product.setSellingPrice(new BigDecimal(price));
            product.setMinStock(Integer.parseInt(stockAlertQty == null || stockAlertQty.trim().isEmpty() ? "0" : stockAlertQty));
            product.setStatus(status == null || status.trim().isEmpty() ? "active" : status.trim());

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
            product.setProductName(request.getParameter("name"));
            product.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            product.setCostPrice(new BigDecimal(request.getParameter("costPrice")));
            product.setSellingPrice(new BigDecimal(request.getParameter("price")));
            product.setMinStock(Integer.parseInt(request.getParameter("stockAlertQty")));
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
