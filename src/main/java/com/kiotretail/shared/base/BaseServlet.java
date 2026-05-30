package com.kiotretail.shared.base;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base servlet that centralizes common helpers used across the application:
 * JSP forwarding, redirects, JSON responses, and parameter parsing.
 *
 * Concrete servlets should extend this class instead of {@link HttpServlet}
 * directly to keep request handling concise and consistent.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Shared Gson instance. Gson is thread-safe once configured. */
    protected static final Gson GSON = new Gson();

    /** Root prefix for JSP views. */
    private static final String VIEW_PREFIX = "/WEB-INF/views/";

    /**
     * Forward the request to a JSP under {@code /WEB-INF/views/}.
     *
     * @param jspPath path relative to the views folder (e.g. {@code "product/list.jsp"}).
     */
    protected void forward(HttpServletRequest req, HttpServletResponse resp, String jspPath)
            throws ServletException, IOException {
        String target = VIEW_PREFIX + (jspPath == null ? "" : jspPath);
        req.getRequestDispatcher(target).forward(req, resp);
    }

    /**
     * Redirect to a path relative to the current context.
     *
     * @param path application path (e.g. {@code "/products"}); leading slash is optional.
     */
    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path)
            throws IOException {
        String safePath = path == null ? "" : path;
        if (!safePath.isEmpty() && !safePath.startsWith("/")) {
            safePath = "/" + safePath;
        }
        resp.sendRedirect(req.getContextPath() + safePath);
    }

    /**
     * Serialize the given object as JSON and write it to the response body.
     */
    protected void sendJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.write(GSON.toJson(data));
            writer.flush();
        }
    }

    /**
     * Send a JSON error response with the given HTTP status and message.
     */
    protected void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setStatus(status);
        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", true);
        body.put("message", message == null ? "" : message);
        sendJson(resp, body);
    }

    /**
     * Parse an integer request parameter, returning {@code defaultValue} when
     * the parameter is missing or not a valid integer.
     */
    protected int getIntParam(HttpServletRequest req, String name, int defaultValue) {
        String raw = req.getParameter(name);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    /**
     * Read a string request parameter, trimming whitespace and falling back to
     * {@code defaultValue} when the parameter is missing or blank.
     */
    protected String getStringParam(HttpServletRequest req, String name, String defaultValue) {
        String raw = req.getParameter(name);
        if (raw == null) {
            return defaultValue;
        }
        String trimmed = raw.trim();
        return trimmed.isEmpty() ? defaultValue : trimmed;
    }
}
