# HRS-001 Move Database Credentials Out of web.xml

## Current Behavior

`src/main/webapp/WEB-INF/web.xml` (line 38-45) chứa credential database plaintext:

```xml
<context-param>
    <param-name>db.url</param-name>
    <param-value>jdbc:sqlserver://localhost:1433;databaseName=DBFinora;trustServerCertificate=true</param-value>
</context-param>
<context-param>
    <param-name>db.username</param-name>
    <param-value>sa</param-value>
</context-param>
<context-param>
    <param-name>db.password</param-name>
    <param-value>123</param-value>
</context-param>
```

`shared/util/DatabaseUtil` đọc 3 context-param này tại runtime để mở JDBC connection.

## Target Behavior

- web.xml KHÔNG chứa giá trị credential
- App đọc DB credentials từ biến môi trường HOẶC JNDI Resource định nghĩa trong Tomcat `context.xml` ngoài repo
- Credential cũ rotate (đổi mật khẩu SA / dùng user app riêng)
- Local dev có file `.env.example` template; thực tế đọc qua `System.getenv()`
- CI lint check chặn pattern `db.password` trong web.xml

## Affected Users

- Backend developer (đổi cách config local)
- Ops/Tomcat admin (cấu hình JNDI hoặc env)
- Security/Audit reviewer (verify không còn secret trong git)

## Affected Product Docs

- `AGENTS.md` Engineering Rules (rule "No hardcoded secrets" được thực thi)
- `docs/security/` (TODO: tạo doc auth-data-protection)
- `README.md` setup instructions

## Non-Goals

- Không migrate sang DB engine khác
- Không thay đổi connection pool implementation
- Không add Vault/Secret Manager (cho sau)

## Implementation Status (2026-06-01)

Phase 2-3-5-6 đã apply. Phase 1 (rotate password) + Phase 4 (Tomcat config với env vars hoặc JNDI) cần ops thực hiện trước khi đóng issue #16.

Code changes shipped:
- `DatabaseUtil` resolution chain: JNDI → env vars → `.env` → fail-fast IllegalStateException
- `web.xml`: xoá 3 db.* context-param, thêm `<resource-ref>` JNDI
- `.gitignore`: block `.env`, `kiotretail.xml` Tomcat context khỏi commit
- `.env.example`: dev template
- `scripts/check-secrets.sh`: CI guard chạy pass
- `docs/stories/HRS-001-secrets-out-of-webxml/sample-tomcat-context.xml`: template Resource block
- `docs/stories/HRS-001-secrets-out-of-webxml/sample-setenv.bat`: dev setenv template

Outstanding (cần human):
- Rotate SQL Server SA password hoặc tạo user app `kiotretail_app`
- Chỉnh `apache-tomcat-10.1.17/conf/Catalina/localhost/kiotretail.xml` thêm Resource block
- Set `DB_USER` / `DB_PASSWORD` env vars trước khi start Tomcat (hoặc dùng JNDI Resource)
- Smoke test login + product list sau khi env vars set
- Sign-off table trong `validation.md`
