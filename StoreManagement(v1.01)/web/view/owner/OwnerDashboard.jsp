<%-- 
    Document   : OwnerDashboard
    Created on : 24 May 2026, 13:25:16
    Author     : PCQN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.Employee"%>
<%@page import="model.Role"%>
<%@page import="model.Branch"%>

<%
    List<Employee> employees =
            (List<Employee>) request.getAttribute("employees");

    List<Role> roles =
            (List<Role>) request.getAttribute("roles");

    List<Branch> branches =
            (List<Branch>) request.getAttribute("branches");

    Employee editingEmployee =
            (Employee) request.getAttribute("editingEmployee");

    Employee resetEmployee =
            (Employee) request.getAttribute("resetEmployee");

    String formMode =
            (String) request.getAttribute("formMode");

    boolean isAdd =
            "add".equals(formMode);

    boolean isEdit =
            "edit".equals(formMode)
            && editingEmployee != null;

    boolean isReset =
            "reset".equals(formMode)
            && resetEmployee != null;
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Owner Employee Management</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/view/css/base.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/view/css/owner.css">

</head>

<body>
    <% if (isReset) { %>

    <div class="modal-overlay">

        <div class="modal-box small-modal">

            <div class="modal-header">

                <div>
                    <h2>Reset Password</h2>

                    <p>
                        Reset password for
                        <b><%= resetEmployee.getFullName() %></b>.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/owner">
                    <span class="material-symbols-outlined">close</span>
                </a>

            </div>

            <form class="modal-form"
                  action="${pageContext.request.contextPath}/owner"
                  method="post">

                <input type="hidden"
                       name="action"
                       value="resetPassword">

                <input type="hidden"
                       name="employeeID"
                       value="<%= resetEmployee.getEmployeeID() %>">

                <div class="full-width">
                    <label>New Password</label>

                    <input type="password"
                           name="newPassword"
                           required>
                </div>

                <div class="modal-actions full-width">

                    <a href="${pageContext.request.contextPath}/owner"
                       class="btn-cancel">
                        Cancel
                    </a>

                    <button type="submit"
                            class="btn-primary">
                        Reset Password
                    </button>

                </div>

            </form>

        </div>

    </div>

    <% } %>
    <% if (isAdd || isEdit) { %>

    <div class="modal-overlay">

        <div class="modal-box">

            <div class="modal-header">

                <div>
                    <h2>
                        <%= isEdit ? "Edit Employee" : "Add Employee" %>
                    </h2>

                    <p>
                        Owner can manage employee account information and permissions.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/owner">
                    <span class="material-symbols-outlined">close</span>
                </a>

            </div>

            <form class="modal-form"
                  action="${pageContext.request.contextPath}/owner"
                  method="post">

                <input type="hidden"
                       name="action"
                       value="<%= isEdit ? "update" : "create" %>">

                <% if (isEdit) { %>
                    <input type="hidden"
                           name="employeeID"
                           value="<%= editingEmployee.getEmployeeID() %>">
                <% } %>

                <div class="full-width">
                    <label>Full Name</label>
                    <input type="text"
                           name="fullName"
                           required
                           value="<%= isEdit ? editingEmployee.getFullName() : "" %>">
                </div>

                <div>
                    <label>Email</label>
                    <input type="email"
                           name="email"
                           value="<%= isEdit ? editingEmployee.getEmail() : "" %>">
                </div>

                <div>
                    <label>Phone</label>
                    <input type="text"
                           name="phone"
                           value="<%= isEdit ? editingEmployee.getPhone() : "" %>">
                </div>

                <% if (!isEdit) { %>
                <div class="full-width">
                    <label>Password</label>
                    <input type="password"
                           name="password"
                           required>
                </div>
                <% } %>

                <div>
                    <label>Role</label>

                    <select name="roleID" required>
                        <%
                            if (roles != null) {
                                for (Role r : roles) {
                        %>

                        <option value="<%= r.getRoleID() %>"
                            <%= isEdit && editingEmployee.getRoleID() == r.getRoleID()
                                    ? "selected"
                                    : "" %>>

                            <%= r.getName() %>

                        </option>

                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div>
                    <label>Branch</label>

                    <select name="branchID" required>
                        <%
                            if (branches != null) {
                                for (Branch b : branches) {
                        %>

                        <option value="<%= b.getBranchID() %>"
                            <%= isEdit && editingEmployee.getBranchID() == b.getBranchID()
                                    ? "selected"
                                    : "" %>>

                            <%= b.getName() %>

                        </option>

                        <%
                                }
                            }
                        %>
                    </select>
                </div>

                <div class="full-width">
                    <label>Status</label>

                    <select name="status">
                        <option value="active"
                            <%= isEdit && "active".equalsIgnoreCase(editingEmployee.getStatus())
                                    ? "selected"
                                    : "" %>>
                            active
                        </option>

                        <option value="locked"
                            <%= isEdit && "locked".equalsIgnoreCase(editingEmployee.getStatus())
                                    ? "selected"
                                    : "" %>>
                            locked
                        </option>
                    </select>
                </div>

                <div class="modal-actions full-width">

                    <a href="${pageContext.request.contextPath}/owner"
                       class="btn-cancel">
                        Cancel
                    </a>

                    <button type="submit"
                            class="btn-primary">
                        <%= isEdit ? "Update Employee" : "Create Employee" %>
                    </button>

                </div>

            </form>

        </div>

    </div>

    <% } %>

    <div class="layout">

        <aside class="sidebar">

            <div class="sidebar-top">

                <div class="logo">
                    O
                </div>

                <div>
                    <h2>Shop Owner</h2>
                    <p>Employee Management</p>
                </div>

            </div>

            <ul class="sidebar-menu">

                <li class="active">
                    <a href="${pageContext.request.contextPath}/owner">
                        Employees
                    </a>
                </li>

            </ul>

        </aside>

        <main class="main-content">

            <header class="topbar">

                <div>
                    <h1>Employee Management</h1>

                    <p>
                        Manage Store Manager, Sales Staff
                        and Warehouse Staff accounts.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/owner?action=add"
                   class="btn-primary">

                    Add Employee

                </a>

            </header>

            <section class="table-wrapper">

                <div class="table-header">
                    <h2>Employee List</h2>
                </div>

                <div class="table-scroll">

                    <table>

                        <thead>

                        <tr>
                            <th>ID</th>
                            <th>Full Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Role</th>
                            <th>Branch</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>

                        </thead>

                        <tbody>

                        <%
                            if (employees != null
                                    && !employees.isEmpty()) {

                                for (Employee e : employees) {
                        %>

                        <tr>

                            <td>
                                EMP<%= e.getEmployeeID()%>
                            </td>

                            <td>
                                <%= e.getFullName()%>
                            </td>

                            <td>
                                <%= e.getEmail()%>
                            </td>

                            <td>
                                <%= e.getPhone()%>
                            </td>

                            <td>
                                <%= e.getRoleName()%>
                            </td>

                            <td>
                                <%= e.getBranchName()%>
                            </td>

                            <td>

                                <span class="<%= "active".equalsIgnoreCase(
                                        e.getStatus())
                                        ? "status-active"
                                        : "status-locked"%>">

                                    <%= e.getStatus()%>

                                </span>

                            </td>

                            <td>

                                <div class="action-group">

                                    <a class="edit-btn"
                                       href="${pageContext.request.contextPath}/owner?action=edit&id=<%= e.getEmployeeID()%>">

                                        Edit

                                    </a>

                                    <a class="edit-btn"
                                       href="${pageContext.request.contextPath}/owner?action=reset&id=<%= e.getEmployeeID()%>">

                                        Reset

                                    </a>

                                    <% if ("active".equalsIgnoreCase(
                                            e.getStatus())) {%>

                                    <form action="${pageContext.request.contextPath}/owner"
                                          method="post">

                                        <input type="hidden"
                                               name="action"
                                               value="lock">

                                        <input type="hidden"
                                               name="employeeID"
                                               value="<%= e.getEmployeeID()%>">

                                        <button class="lock-btn"
                                                type="submit">

                                            Lock

                                        </button>

                                    </form>

                                    <% } else {%>

                                    <form action="${pageContext.request.contextPath}/owner"
                                          method="post">

                                        <input type="hidden"
                                               name="action"
                                               value="unlock">

                                        <input type="hidden"
                                               name="employeeID"
                                               value="<%= e.getEmployeeID()%>">

                                        <button class="unlock-btn"
                                                type="submit">

                                            Unlock

                                        </button>

                                    </form>

                                    <% }%>

                                    <form action="${pageContext.request.contextPath}/owner"
                                          method="post">

                                        <input type="hidden"
                                               name="action"
                                               value="delete">

                                        <input type="hidden"
                                               name="employeeID"
                                               value="<%= e.getEmployeeID()%>">

                                        <button class="lock-btn"
                                                type="submit">

                                            Delete

                                        </button>

                                    </form>

                                </div>

                            </td>

                        </tr>

                        <%
                                }

                            } else {
                        %>

                        <tr>

                            <td colspan="8"
                                class="empty-row">

                                No employees found.

                            </td>

                        </tr>

                        <%
                            }
                        %>

                        </tbody>

                    </table>

                </div>

            </section>

        </main>

    </div>

</body>

</html>
