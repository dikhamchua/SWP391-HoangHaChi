---
name: feedback-tomcat-deploy
description: How the user wants the app built and deployed to the locally installed Tomcat 10.1.17 — never edit pom.xml for this
metadata:
  type: feedback
---

Khi cần chạy/deploy app, build WAR rồi copy vào Tomcat đã cài sẵn — KHÔNG sửa `pom.xml` (cargo plugin), `server.xml`, hay file cấu hình nào khác.

**Why:** User nói rõ "đừng ghi vào trong các file pom.xml" — muốn quy trình deploy nằm hoàn toàn ở phía agent (build + copy + start), giữ file cấu hình dự án nguyên trạng. Trước đó tôi định đổi cargo plugin sang `type=installed/existing` trỏ vào Tomcat đã cài, user từ chối.

**How to apply:** Quy trình deploy mặc định:
1. `mvn clean package` (hoặc `-DskipTests` nếu user yêu cầu) -> ra `target/kiotretail.war`
2. Copy `target/kiotretail.war` vào `webapps/` của Tomcat đã cài
3. Start/restart Tomcat đó bằng `bin/startup.bat` / `bin/shutdown.bat` (hoặc `catalina.bat`)

**Tomcat đã cài:** `C:\Users\ADMIN\OneDrive - vinhdeptrai\Netbeans Workspace\Tutor\phan mem hoc PRJ\apache-tomcat-10.1.17-windows-x64\apache-tomcat-10.1.17`
- HTTP port = 9999, shutdown port = 8805 (đã set sẵn trong `conf/server.xml` của bản Tomcat này)
- App URL: http://localhost:9999/kiotretail

`pom.xml` vẫn giữ cargo embedded gốc — không phải nguồn deploy nữa, bỏ qua nó.
