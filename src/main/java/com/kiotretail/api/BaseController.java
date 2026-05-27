package com.kiotretail.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

import com.kiotretail.api.ApiAction;
import com.kiotretail.api.action.GetProductsAction;

@WebServlet("/api/*")
public class BaseController extends HttpServlet {

    private final Map<String, ApiAction> routes = new HashMap<>();

    @Override
    public void init() throws ServletException {
        routes.put("/api/products", new GetProductsAction());
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());;
        ApiAction action = routes.get(path);
        //check if api had been registered or not
        if (action == null){
            resp.sendError(404,"Not Found");
            return;
        }
            else {
            try {
                Object result = action.execute(req);
                resp.setContentType("application/json;charset= UTF-8");
                String json = new Gson().toJson(result);
                resp.getWriter().print(json);
            } catch (Exception e) {
                resp.setStatus(500);
                e.printStackTrace();
                ApiResponse errorRespone = new ApiResponse(500, "System error " + e.getMessage(),null);
                String errrJson = new Gson().toJson(errorRespone);
                resp.getWriter().print(errrJson);
            }

        }


    }
}