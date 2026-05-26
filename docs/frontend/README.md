# Frontend Standards

## Current Frontend

The frontend is server-rendered JSP with CSS and JavaScript assets under `src/main/webapp/assets`.

## JSP Rules

- Keep pages under `WEB-INF/views` so they are rendered through controllers.
- Use `common` fragments for shared header, footer, and sidebar markup.
- Keep business rules out of JSP files.
- Prefer request attributes for page data and session attributes for authenticated user/permission state.

## Asset Rules

- Reuse `theme.css`, `components.css`, `style.css`, and page-specific files before adding new CSS.
- Keep JavaScript in `assets/js`.
- Do not hardcode API base URLs; respect the servlet context path when possible.
