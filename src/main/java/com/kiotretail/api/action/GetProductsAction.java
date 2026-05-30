package com.kiotretail.api.action;

import com.kiotretail.api.ApiAction;
import com.kiotretail.api.ApiResponse;
import com.kiotretail.product.dao.ProductDAO;
import com.kiotretail.product.dto.ProductFilterDTO;
import com.kiotretail.product.model.Product;
import com.kiotretail.shared.base.Pagination;
import com.kiotretail.shared.constant.AppConstants;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

public class GetProductsAction implements ApiAction {

    private static final int API_DEFAULT_LIMIT = 10;
    private static final int API_MAX_LIMIT = 50;

    @Override
    public Object execute(HttpServletRequest request) throws Exception {
        String pageParam = request.getParameter(AppConstants.PARAM_PAGE);
        String limitParam = request.getParameter(AppConstants.PARAM_LIMIT);

        int page = AppConstants.DEFAULT_PAGE;
        int limit = API_DEFAULT_LIMIT;

        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
            if (page < 1) page = 1;
        }

        if (limitParam != null && !limitParam.isEmpty()) {
            int userLimit = Integer.parseInt(limitParam);
            if (userLimit > API_MAX_LIMIT) userLimit = API_MAX_LIMIT;
            else if (userLimit <= 0) userLimit = API_DEFAULT_LIMIT;
            limit = userLimit;
        }

        String keyword = request.getParameter(AppConstants.PARAM_KEYWORD);
        String status = request.getParameter(AppConstants.PARAM_STATUS);
        String categoryIdParam = request.getParameter(AppConstants.PARAM_CATEGORY_ID);
        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            categoryId = Integer.parseInt(categoryIdParam.trim());
        }

        ProductFilterDTO filter = new ProductFilterDTO();
        filter.setKeyword(keyword);
        filter.setStatus(status);
        filter.setCategoryId(categoryId);

        ProductDAO productDAO = new ProductDAO();
        Pagination pagination = Pagination.of(page, limit);
        List<Product> list = productDAO.getProducts(filter, pagination);

        List<com.kiotretail.api.dto.Product> dtoList = new ArrayList<>();
        for (Product p : list) {
            com.kiotretail.api.dto.Product dto = new com.kiotretail.api.dto.Product(
                    p.getProductId(),
                    p.getSku(),
                    p.getProductName(),
                    p.getCategoryId(),
                    p.getCategoryName(),
                    p.getUnit(),
                    p.getCostPrice(),
                    p.getPrice(),
                    p.getStockAlertQty(),
                    0,
                    0,
                    p.getImageUrl(),
                    p.getStatus()
            );
            dtoList.add(dto);
        }
        return new ApiResponse(AppConstants.HTTP_OK, "OK", dtoList);
    }
}
