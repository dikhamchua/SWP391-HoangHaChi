package com.kiotretail.employee.dto;

import jakarta.servlet.http.HttpServletRequest;

public class EmployeeFilterDTO {
    private String keyword;
    private Integer roleId;
    private String status;

    public EmployeeFilterDTO() {}

    public static EmployeeFilterDTO from(HttpServletRequest req) {
        EmployeeFilterDTO f = new EmployeeFilterDTO();
        f.setKeyword(req.getParameter("keyword"));
        String roleIdStr = req.getParameter("roleId");
        if (roleIdStr != null && !roleIdStr.trim().isEmpty()) {
            try { f.setRoleId(Integer.parseInt(roleIdStr)); } catch (NumberFormatException ignored) {}
        }
        f.setStatus(req.getParameter("status"));
        return f;
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}