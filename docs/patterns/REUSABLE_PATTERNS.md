# Reusable Patterns

## Servlet POST Redirect Pattern

Use POST for mutations, set a session flash message, then redirect to avoid duplicate form submission.

```java
request.getSession().setAttribute("message", "Operation completed.");
request.getSession().setAttribute("messageType", "success");
response.sendRedirect(request.getContextPath() + "/admin/example");
```

## Servlet GET Forward Pattern

Use GET methods to load view data and forward to JSPs under `WEB-INF/views`.

```java
request.setAttribute("items", items);
request.getRequestDispatcher("/WEB-INF/views/admin/example.jsp").forward(request, response);
```

## DAO Query Pattern

Use try-with-resources, `PreparedStatement`, and private mapper methods.

```java
try (Connection conn = DatabaseUtil.getConnection();
     PreparedStatement stmt = conn.prepareStatement(sql)) {
    stmt.setInt(1, id);
    try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return extractEntity(rs);
        }
    }
}
```

## Role Check Pattern

Use `RolePermissionUtil` rather than string comparisons inside JSPs or controllers.

```java
Object role = request.getSession().getAttribute("roleName");
boolean allowed = role != null && RolePermissionUtil.canManageCategory(role.toString());
```

## API Action Pattern

Implement `ApiAction`, parse request parameters defensively, call DAO/service code, and return `ApiResponse`.

```java
public class GetExampleAction implements ApiAction {
    @Override
    public Object execute(HttpServletRequest request) throws Exception {
        return new ApiResponse(200, "OK", data);
    }
}
```
