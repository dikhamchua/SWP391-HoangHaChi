package com.kiotretail.api.action;

import com.kiotretail.api.ApiAction;
import com.kiotretail.api.ApiResponse;
import com.kiotretail.dao.ProductDAO;
import jakarta.servlet.http.HttpServletRequest;
import com.kiotretail.model.Product;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class GetProductsAction implements ApiAction {
    @Override
    public Object execute(HttpServletRequest request) throws Exception {
        ProductDAO productDAO = new ProductDAO();
        List<Product> list = productDAO.getAllProducts();
        return new ApiResponse(200,"Test", list);
    }
}
