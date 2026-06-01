# Design — HRS-001 Secrets Out of web.xml

## Approach

Dùng JNDI Resource trong Tomcat (ưu tiên 1) hoặc System property/env var (fallback). Lựa chọn dựa trên discussion `docs/decisions/0006-db-credentials-jndi.md`.

## Code Surface

1. **DatabaseUtil** đọc theo thứ tự ưu tiên:
   - JNDI lookup `java:comp/env/jdbc/KiotRetailDS` (DataSource)
   - Fallback System.getenv("DB_URL", "DB_USER", "DB_PASSWORD") + DriverManager
   - Cuối cùng throw RuntimeException nếu cả 2 không có (không silently fallback default)

2. **web.xml** xoá 3 context-param `db.url`, `db.username`, `db.password`. Thêm `<resource-ref>` cho JNDI:
   ```xml
   <resource-ref>
       <res-ref-name>jdbc/KiotRetailDS</res-ref-name>
       <res-type>javax.sql.DataSource</res-type>
       <res-auth>Container</res-auth>
   </resource-ref>
   ```

3. **Tomcat config ngoài repo** — `apache-tomcat/conf/Catalina/localhost/kiotretail.xml`:
   ```xml
   <Context docBase="..." path="/kiotretail">
       <Resource name="jdbc/KiotRetailDS"
                 auth="Container"
                 type="javax.sql.DataSource"
                 driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver"
                 url="jdbc:sqlserver://localhost:1433;databaseName=DBFinora;trustServerCertificate=true"
                 username="${env:DB_USER}"
                 password="${env:DB_PASSWORD}"
                 maxTotal="20" maxIdle="5"/>
   </Context>
   ```

4. **Local dev** — `.env.example` ở repo root:
   ```
   DB_URL=jdbc:sqlserver://localhost:1433;databaseName=DBFinora;trustServerCertificate=true
   DB_USER=sa
   DB_PASSWORD=changeme
   ```

5. **CI guard** — script `scripts/check-secrets.sh` grep `db\.password` trong web.xml, exit 1 nếu thấy.

## Migration Steps

1. Rotate password SA hoặc tạo user app `kiotretail_app` quyền hạn vừa đủ
2. Update local Tomcat `kiotretail.xml` thêm Resource block (ngoài repo)
3. PR đổi DatabaseUtil → JNDI lookup
4. PR xoá 3 context-param khỏi web.xml + thêm resource-ref
5. Update README setup
6. Add CI check

## Risks

- JNDI thiếu → fallback env var: nếu cả 2 thiếu, app fail-fast (không silent default credentials)
- Test local cần config Tomcat đúng → README mô tả chi tiết
- Connection pool config trong context.xml khác với DriverManager hiện tại — cần verify số connection ổn định
