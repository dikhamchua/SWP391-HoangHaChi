package com.kiotretail.api;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

public interface ApiAction {
    Object execute(HttpServletRequest request) throws Exception;
}
