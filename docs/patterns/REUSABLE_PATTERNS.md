# Reusable Patterns

## Servlet POST Redirect Pattern

Use POST for mutations, set a session flash message, then redirect to avoid duplicate form submission.

```java
request.getSession().setAttribute("flashMessage", "Operation completed.");
request.getSession().setAttribute("messageType", "success"); // success | danger | warning | info
response.sendRedirect(request.getContextPath() + "/admin/example");
```

## Toast Notification Pattern (react-hot-toast style)

Toast notifications use a custom implementation that replicates react-hot-toast's visual design. **Never use alert(), iziToast, or other toast libraries.**

### Server-side (Servlet → redirect → toast)

Set session attributes before redirect. The `toast.jsp` include reads and clears them automatically.

```java
session.setAttribute("flashMessage", "Thêm sản phẩm thành công!");
session.setAttribute("messageType", "success");
response.sendRedirect(request.getContextPath() + "/products");
```

### Client-side (JavaScript)

```javascript
showToast('Lưu thành công!', 'success');        // auto-dismiss 4s
showToast('Có lỗi xảy ra', 'danger');           // red X icon
showToast('Cảnh báo', 'warning');                // yellow ! icon
showToast('Thông tin', 'info');                  // blue i icon
showToast('Lâu hơn', 'success', 6000);          // custom duration

// Promise style (loading spinner → success/error)
toastPromise(fetch('/api/save'), {
    loading: 'Đang lưu...',
    success: 'Đã lưu!',
    error: 'Lưu thất bại!'
});

// Dismiss manually
var t = showToast('Loading...', 'loading');
dismissToast(t);
```

### JSP include (required in every page layout)

```jsp
<jsp:include page="/WEB-INF/views/common/toast.jsp" />
```

### Rules

- Session attributes: `flashMessage` (message text) + `messageType` (success|danger|warning|info)
- Toast auto-dismisses after 4 seconds (pauses on hover)
- CSS: `assets/css/kr-common.css` (search `.kr-toast`)
- JS: `assets/js/main.js` (search `showToast`)
- Do NOT use `alert()`, `confirm()` for success/error feedback — use toast instead
- Do NOT add third-party toast libraries (iziToast, toastr, SweetAlert, etc.)

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
