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
        String pageParam = request.getParameter("page");
        String limitParam = request.getParameter("limit");

        int page = 1;
        int limit = 10;

        //check if page param not null or not empty
        if (pageParam != null && !pageParam.isEmpty()){
            page = Integer.parseInt(pageParam);
            //check if page small than 1
            if (page < 1) page = 1;
        }

        //check if limit param not null or not empty
        if (limitParam != null && !limitParam.isEmpty()){
            int userlimit = Integer.parseInt(limitParam);

            //check user limit bigger than accept
            if (userlimit > 50) userlimit = 50;
            //check if user limit too small , default to 10
            else if (userlimit <= 0) {
                userlimit = 10;
            }
            else { limit = userlimit; }
        }
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String categoryIdParam = request.getParameter("categoryId");
        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            categoryId = Integer.parseInt(categoryIdParam.trim());
        }

        ProductDAO productDAO = new ProductDAO();
        List<Product> list = productDAO.getProducts(page, limit, keyword, categoryId, status);
        List<com.kiotretail.api.dto.Product> dtoList = new ArrayList<>();
        for(com.kiotretail.model.Product l: list){
            com.kiotretail.api.dto.Product productdto = new com.kiotretail.api.dto.Product(
                    l.getProductId(),
                    l.getProductCode(),
                    l.getProductName(),
                    l.getCategoryId(),
                    l.getCategoryName(),
                    l.getUnit(),
                    null,
                    l.getSellingPrice(),
                    l.getStockQuantity(),
                    l.getMinStock(),
                    l.getMaxStock(),
                    l.getImageUrl(),
                    l.getStatus()
            );
            dtoList.add(productdto);
        }
        return new ApiResponse(200,"Get okay "+ limit + " product/page ", dtoList);
    }
}
