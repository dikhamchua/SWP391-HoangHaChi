package com.kiotretail.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

import com.kiotretail.api.action.GetProductsAction;
import com.kiotretail.shared.constant.AppConstants;
import com.kiotretail.shared.constant.ErrorMessages;

public class BaseController extends HttpServlet {

    private final Map<String, ApiAction> routes = new HashMap<>();

    @Override
    public void init() throws ServletException {
        routes.put("/api/products", new GetProductsAction());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        ApiAction action = routes.get(path);
        if (action == null) {
            resp.sendError(AppConstants.HTTP_NOT_FOUND, AppConstants.API_NOT_FOUND);
            return;
        }

        try {
            Object result = action.execute(req);
            resp.setContentType("application/json;charset=UTF-8");
            String json = new Gson().toJson(result);
            resp.getWriter().print(json);
        } catch (Exception e) {
            resp.setStatus(AppConstants.HTTP_SERVER_ERROR);
            e.printStackTrace();
            ApiResponse errorResponse = new ApiResponse(AppConstants.HTTP_SERVER_ERROR,
                    String.format(ErrorMessages.SYSTEM_ERROR, e.getMessage()), null);
            String errorJson = new Gson().toJson(errorResponse);
            resp.getWriter().print(errorJson);
        }
    }
}
