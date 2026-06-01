# Execution Plan — HRS-001

## Phase 1: Audit & Rotate (1 day)

- [ ] Scan toàn repo + git history tìm secret leak khác (`git log -p | rg "password|secret"`)
- [ ] Rotate password SQL Server SA (hoặc tạo user app `kiotretail_app` minimum privileges)
- [ ] Document rotation trong `docs/decisions/0006-db-credentials-jndi.md`

## Phase 2: DatabaseUtil refactor (0.5 day)

- [ ] Đọc `DatabaseUtil` hiện tại
- [ ] Implement JNDI lookup `java:comp/env/jdbc/KiotRetailDS`
- [ ] Implement env-var fallback (DB_URL, DB_USER, DB_PASSWORD)
- [ ] Throw `IllegalStateException` nếu cả 2 không có
- [ ] Compile pass

## Phase 3: web.xml cleanup (0.25 day)

- [ ] Xoá 3 context-param db.* khỏi web.xml
- [ ] Thêm `<resource-ref>` cho JNDI DataSource
- [ ] Update existing context-param consumers nếu có

## Phase 4: Tomcat config ngoài repo (0.5 day)

- [ ] Update `apache-tomcat-10.1.17/conf/Catalina/localhost/kiotretail.xml` thêm `<Resource>`
- [ ] Verify env DB_USER + DB_PASSWORD set khi start Tomcat
- [ ] Smoke test login + product list trên port 9999

## Phase 5: Dev experience + CI (0.5 day)

- [ ] Tạo `.env.example` ở repo root
- [ ] Update `README.md` mục "Local Setup"
- [ ] Tạo `scripts/check-secrets.sh` grep blacklist trong web.xml + commit hook
- [ ] Verify CI fail nếu cố add lại password

## Phase 6: Document & close (0.25 day)

- [ ] Cập nhật `docs/security/` (tạo nếu chưa có)
- [ ] Cập nhật `validation.md` của story này
- [ ] Đóng issue #16 + comment evidence

## Total: ~3 days

## Rollback Plan

Nếu sau deploy phát hiện connection issue:
1. Tomcat reload với context-param cũ tạm thời (revert PR Phase 3 + Phase 4)
2. Investigate JNDI config riêng
3. Re-deploy fix

## Approval Gate

Cần human confirmation trước khi:
- Bắt đầu Phase 1 (rotation timing)
- Merge PR Phase 3 (xoá secret khỏi web.xml — point of no return)
