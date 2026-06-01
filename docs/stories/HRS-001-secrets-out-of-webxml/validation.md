# Validation — HRS-001

## Status

PASS — Phase 2-3-5-6 + smoke test xong 2026-06-01. Phase 1 (rotate SA password) chưa làm — quyết định scope đợt sau.

## Acceptance Tests

| Layer | Test | Expected | Actual |
| --- | --- | --- | --- |
| Static | grep `db.password` web.xml | 0 match | 0 match (PASS) |
| Static | hardcoded `PASSWORD/USERNAME/URL =` trong DatabaseUtil | không có | không có (PASS) |
| Static | scripts/check-secrets.sh trên branch fix | exit 0 | exit 0 (PASS) |
| Unit | `mvn test` BranchServiceTest | 6/6 pass | 6/6 pass, 0.064s (PASS) |
| Integration | Tomcat reload sau touch web.xml | OK, no exception | OK (PASS) |
| Integration | Login owner@retail.com qua Tomcat | 200 + redirect /admin/dashboard | OK qua JNDI DataSource (PASS) |
| Integration | GET /admin/products có data | List sản phẩm thật từ DB | 9 sản phẩm hiển thị (PASS) |
| Integration | GET /admin/branches có data | List branches | 2 branches hiển thị (PASS) |
| E2E | Console errors trên /admin/* | 0 errors | 0 errors (PASS) |
| Security | Login với email không tồn tại | "Email hoặc mật khẩu không đúng" (no enumeration) | PASS (verified earlier) |

## Manual Verification Checklist

- [x] Tomcat start không error JNDI sau khi config Resource trong `kiotretail.xml`
- [x] App login OK với JNDI DataSource pool
- [x] Product list load với data thật từ DB
- [x] Branch list load với data thật
- [x] Console clean (0 errors)
- [ ] Logout + login lại OK (test connection pool reuse) — chưa test
- [ ] Stress test 10 concurrent request /admin/products — chưa test (low priority cho dev)
- [ ] Xoá Resource khỏi `kiotretail.xml` → start Tomcat → app fail-fast với log rõ ràng — chưa test (regression scenario)

## Evidence

- Screenshot login + dashboard sau migration: `20-hrs001-branches-jndi.png`, `21-hrs001-products-jndi.png`
- Output `mvn test`: BUILD SUCCESS, 6/6 pass
- Output `scripts/check-secrets.sh`: exit 0
- Tomcat config: `apache-tomcat-10.1.17/conf/Catalina/localhost/kiotretail.xml` thêm `<Resource>` jdbc/KiotRetailDS (ngoài repo)
- web.xml: `<resource-ref>` jdbc/KiotRetailDS (trong repo, không có credential)
- DatabaseUtil resolution chain: JNDI → env vars → .env file → fail-fast IllegalStateException

## Outstanding (Phase 1)

- [ ] Rotate SQL Server SA password hoặc tạo user app `kiotretail_app` quyền vừa đủ
- [ ] Sau rotation, update `kiotretail.xml` với credential mới
- [ ] Audit git history (decision: accept rotation là đủ, không filter-repo vì project sinh viên)

## Rollback Trigger

- Nếu hơn 5% request fail JDBC connection trong 1h sau deploy → rollback (revert PR + dùng env var fallback)
- Nếu Tomcat restart fail JNDI lookup → rollback

## Sign-off

| Role | Signed | Date | Note |
| --- | --- | --- | --- |
| Backend lead | Claude (AI dev) | 2026-06-01 | Phase 2-3-5-6 implemented + smoke test PASS |
| Security reviewer | (pending human) | — | Cần review trước khi rotate prod password |
| Ops/Tomcat admin | Claude (AI dev) | 2026-06-01 | Tomcat Resource config applied dev env |
