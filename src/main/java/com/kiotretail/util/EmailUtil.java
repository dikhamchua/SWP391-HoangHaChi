package com.kiotretail.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class EmailUtil {

    // 1. Hàm sinh mật khẩu ngẫu nhiên bảo mật cao đầy đủ ký tự
    public static String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$*";
        StringBuilder sb = new StringBuilder();
        Random rd = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 2. Hàm gửi email mật khẩu tự động
    public static boolean sendPasswordEmail(String toEmail, String employeeName, String autoPassword) {
        // Cấu hình tài khoản gửi thư hệ thống (Sử dụng App Password của Gmail)
        final String fromEmail = "hoanghachi12082005@gmail.com"; 
        final String appPassword = "kzud qllx uklc bfnd"; // Thay thế bằng mật khẩu ứng dụng Gmail của bạn

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("[Finora] Thông báo cấp tài khoản nhân viên mới");
            
            String content = "<h3>Xin chào " + employeeName + ",</h3>"
                    + "<p>Tài khoản nội bộ của bạn trên hệ thống quản lý chuỗi cửa hàng Finora đã được khởi tạo thành công bởi Owner.</p>"
                    + "<p>Mật khẩu đăng nhập tạm thời của bạn là: <strong style='color:#93000b; font-size:16px;'>" + autoPassword + "</strong></p>"
                    + "<p>Vui lòng đăng nhập bằng Email/Số điện thoại cá nhân và thực hiện <strong>đổi mật khẩu ngay lập tức</strong> tại hệ thống để đảm bảo tính an toàn bảo mật.</p>"
                    + "<br><p>Trân trọng,<br>Ban quản trị Finora.</p>";
                    
            message.setContent(content, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendOTP(String toEmail, String otp) {

        final String fromEmail = "hoanghachi12082005@gmail.com"; 
        final String appPassword = "kzud qllx uklc bfnd"; // Thay thế bằng mật khẩu ứng dụng Gmail của bạn

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(fromEmail));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Mã OTP đặt lại mật khẩu");

            message.setText(
                    "Mã OTP của bạn là: " + otp
                    + "\n\nKhông chia sẻ mã này cho bất kỳ ai."
            );

            Transport.send(message);

            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return false;
    }
}