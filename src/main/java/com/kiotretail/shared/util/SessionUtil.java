package com.kiotretail.shared.util;

import jakarta.servlet.http.HttpSession;

/**
 * Session helper utility for managing user login session attributes.
 */
public final class SessionUtil {

    public static final String EMPLOYEE_KEY = "employee";
    public static final String ROLE_KEY = "roleName";
    public static final String BRANCH_KEY = "branchId";

    private SessionUtil() {
    }

    public static Object getEmployee(HttpSession session) {
        if (session == null) {
            return null;
        }
        return session.getAttribute(EMPLOYEE_KEY);
    }

    public static String getRoleName(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(ROLE_KEY);
    }

    public static Integer getBranchId(HttpSession session) {
        if (session == null) {
            return null;
        }
        return (Integer) session.getAttribute(BRANCH_KEY);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session != null && getEmployee(session) != null;
    }

    public static void setLoginSession(HttpSession session, Object employee, String roleName, Integer branchId) {
        if (session == null) {
            return;
        }
        session.setAttribute(EMPLOYEE_KEY, employee);
        session.setAttribute(ROLE_KEY, roleName);
        session.setAttribute(BRANCH_KEY, branchId);
    }

    public static void clearSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.invalidate();
    }
}
