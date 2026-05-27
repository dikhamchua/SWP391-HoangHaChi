<%-- 
    Document   : AdminDashboard
    Created on : 24 May 2026, 11:33:48
    Author     : PCQN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="model.Employee"%>
<%@page import="model.Branch"%>

<%
    List<Employee> owners =
            (List<Employee>) request.getAttribute("owners");

    List<Branch> branches =
            (List<Branch>) request.getAttribute("branches");

    Employee editingOwner =
            (Employee) request.getAttribute("editingOwner");

    String formMode =
            (String) request.getAttribute("formMode");

    boolean isAddOwner =
            "addOwner".equals(formMode);

    boolean isEditOwner =
            "editOwner".equals(formMode)
            && editingOwner != null;

    boolean noBranch =
            branches == null || branches.isEmpty();
%>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>System Admin - Shop Owners</title>

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Manrope:wght@600;700;800&display=swap"
          rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
          rel="stylesheet">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/view/css/base.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/view/css/admin.css">
</head>

<body>

    <div class="layout">

        <aside class="sidebar">

            <div class="sidebar-top">

                <div class="logo">
                    A
                </div>

                <div>
                    <h2>System Admin</h2>
                    <p>Quản lý hệ thống</p>
                </div>

            </div>

            <ul class="sidebar-menu">

                <li class="active">

                    <a href="${pageContext.request.contextPath}/admin">

                        <span class="material-symbols-outlined">
                            admin_panel_settings
                        </span>

                        <span>Shop Owners</span>

                    </a>

                </li>

            </ul>

        </aside>

        <main class="main-content">

            <header class="topbar">

                <div>
                    <h1>Shop Owner Management</h1>
                    <p>
                        Admin manages Shop Owner accounts only
                        and cannot access internal sales data.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/admin?action=addOwner"
                   class="btn-primary">

                    <span class="material-symbols-outlined">
                        add
                    </span>

                    Add Owner

                </a>

            </header>

            <section class="stats-grid">

                <div class="card">
                    <p>Total Shop Owners</p>

                    <h3>
                        <%= owners == null ? 0 : owners.size() %>
                    </h3>
                </div>

                <div class="card">
                    <p>Access Rule</p>
                    <h3>No internal sales access</h3>
                </div>

            </section>

            <section class="table-wrapper">

                <div class="table-header">
                    <h2>Shop Owner List</h2>
                </div>

                <div class="table-scroll">

                    <table>

                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Owner Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Branch</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                        </thead>

                        <tbody>

                        <%
                            if (owners != null && !owners.isEmpty()) {

                                for (Employee e : owners) {
                        %>

                        <tr>
                            <td><%= e.getEmployeeID()%></td>

                            <td><%= e.getFullName()%></td>

                            <td><%= e.getEmail()%></td>

                            <td><%= e.getPhone()%></td>

                            <td><%= e.getBranchName()%></td>

                            <td>
                                <span class="<%= "active".equalsIgnoreCase(e.getStatus())
                                        ? "status-active"
                                        : "status-locked"%>">

                                    <%= e.getStatus()%>
                                </span>
                            </td>

                            <td>
                                <div class="action-group">

                                    <a class="edit-btn"
                                        href="${pageContext.request.contextPath}/admin?action=editOwner&name=<%= URLEncoder.encode(e.getFullName(), "UTF-8") %>">
                                        Edit
                                    </a>

                                    <% if ("active".equalsIgnoreCase(e.getStatus())) {%>

                                    <form action="admin" method="post">

                                        <input type="hidden"
                                               name="action"
                                               value="lockOwner">

                                        <input type="hidden"
                                               name="employeeID"
                                               value="<%= e.getEmployeeID()%>">

                                        <button class="lock-btn">
                                            Lock
                                        </button>

                                    </form>

                                    <% } else {%>

                                    <form action="admin" method="post">

                                        <input type="hidden"
                                               name="action"
                                               value="unlockOwner">

                                        <input type="hidden"
                                               name="employeeID"
                                               value="<%= e.getEmployeeID()%>">

                                        <button class="unlock-btn">
                                            Unlock
                                        </button>

                                    </form>

                                    <% }%>

                                </div>
                            </td>

                        </tr>

                        <%
                                }

                            } else {
                        %>

                        <tr>
                            <td colspan="7" class="empty-row">
                                Chưa có Shop Owner nào.
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

    <% if (isAddOwner || isEditOwner) { %>

    <div class="modal-overlay">

        <div class="modal-box">

            <div class="modal-header">

                <div>
                    <h2>
                        <%= isEditOwner ? "Edit Shop Owner" : "Add Shop Owner" %>
                    </h2>

                    <p>
                        Admin creates or updates Shop Owner account only.
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/admin">
                    <span class="material-symbols-outlined">
                        close
                    </span>
                </a>

            </div>

            <form class="modal-form"
                  action="${pageContext.request.contextPath}/admin"
                  method="post">

                <input type="hidden"
                       name="action"
                       value="<%= isEditOwner ? "updateOwner" : "createOwner" %>">

                <% if (isEditOwner) { %>

                <input type="hidden"
                       name="employeeID"
                       value="<%= editingOwner.getEmployeeID() %>">

                <% } %>

                <div class="full-width">

                    <label>Owner Name</label>

                    <input type="text"
                           name="fullName"
                           required
                           value="<%= isEditOwner ? editingOwner.getFullName() : "" %>">

                </div>

                <div>
                    <label>Email</label>

                    <input type="email"
                           name="email"
                           value="<%= isEditOwner ? editingOwner.getEmail() : "" %>">
                </div>

                <div>
                    <label>Phone</label>

                    <input type="text"
                           name="phone"
                           value="<%= isEditOwner ? editingOwner.getPhone() : "" %>">
                </div>

                <% if (!isEditOwner) { %>

                <div class="full-width">
                    <label>Password</label>

                    <input type="password"
                           name="password"
                           required>
                </div>

                <% } %>

                <div>

                    <label>Branch</label>

                    <% if (!noBranch) { %>

                    <select name="branchID" required>

                        <%
                            for (Branch b : branches) {
                        %>

                        <option value="<%= b.getBranchID() %>"
                            <%= isEditOwner
                                    && editingOwner.getBranchID() == b.getBranchID()
                                    ? "selected"
                                    : "" %>>

                            <%= b.getName() %>

                        </option>

                        <%
                            }
                        %>

                    </select>

                    <% } else { %>

                    <div class="empty-branch-box">

                        <p>No branch available.</p>

                        <a href="${pageContext.request.contextPath}/branch?action=add"
                           class="create-branch-btn">

                            <span class="material-symbols-outlined">
                                add_business
                            </span>

                            Create New Branch

                        </a>

                    </div>

                    <% } %>

                </div>

                <div>

                    <label>Status</label>

                    <select name="status">

                        <option value="active"
                            <%= isEditOwner
                                    && "active".equalsIgnoreCase(editingOwner.getStatus())
                                    ? "selected"
                                    : "" %>>

                            active

                        </option>

                        <option value="locked"
                            <%= isEditOwner
                                    && "locked".equalsIgnoreCase(editingOwner.getStatus())
                                    ? "selected"
                                    : "" %>>

                            locked

                        </option>

                    </select>

                </div>

                <div class="modal-actions full-width">

                    <a href="${pageContext.request.contextPath}/admin"
                       class="btn-cancel">
                        Cancel
                    </a>

                    <button type="submit"
                            class="btn-primary"
                            <%= noBranch ? "disabled" : "" %>>

                        <%= isEditOwner ? "Update Owner" : "Create Owner" %>

                    </button>

                </div>

            </form>

        </div>

    </div>

    <% } %>

</body>

</html>
