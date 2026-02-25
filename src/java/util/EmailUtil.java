package util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    
    // CẤU HÌNH EMAIL - Thay đổi theo email thật của bạn
    private static final String FROM_EMAIL = "quanganhsk1720@gmail.com"; // Thay bằng email thật
    private static final String PASSWORD = "upyb clhw mmsk sziz";      // Thay bằng App Password của Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    /**
     * Gửi email reset password
     * @param toEmail Email người nhận
     * @param resetLink Link reset password
     * @return true nếu gửi thành công
     */
    public static boolean sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            // Cấu hình properties cho Gmail SMTP
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            // Tạo session với authentication
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });
            
            // Tạo message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Reset Your Password - School Asset Management");
            
            // Tạo nội dung email HTML
            String htmlContent = createResetEmailTemplate(resetLink);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Gửi email
            Transport.send(message);
            
            System.out.println("Password reset email sent successfully to: " + toEmail);
            return true;
            
        } catch (MessagingException e) {
            System.err.println("Failed to send email to: " + toEmail);
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tạo template HTML cho email reset password
     */
    private static String createResetEmailTemplate(String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "        .content { background: #f8f9fa; padding: 30px; border-radius: 0 0 10px 10px; }" +
                "        .button { display: inline-block; padding: 12px 30px; background: #4e73df; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }" +
                "        .button:hover { background: #2e59d9; }" +
                "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #6c757d; }" +
                "        .warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <h1>🔐 Password Reset Request</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <h2>Hello!</h2>" +
                "            <p>We received a request to reset your password for your School Asset Management account.</p>" +
                "            <p>Click the button below to reset your password:</p>" +
                "            <div style='text-align: center;'>" +
                "                <a href='" + resetLink + "' class='button'>Reset Password</a>" +
                "            </div>" +
                "            <div class='warning'>" +
                "                <strong>⚠️ Important:</strong> This link will expire in 1 hour for security reasons." +
                "            </div>" +
                "            <p>If you didn't request a password reset, please ignore this email or contact support if you have concerns.</p>" +
                "            <p style='font-size: 12px; color: #6c757d;'>If the button doesn't work, copy and paste this link into your browser:<br>" +
                "            <a href='" + resetLink + "'>" + resetLink + "</a></p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>© 2024 School Asset Management System. All rights reserved.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }
    
    /**
     * Gửi email thông báo password đã được đổi thành công
     */
    public static boolean sendPasswordChangedNotification(String toEmail, String fullName) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Password Changed Successfully - School Asset Management");
            
            String htmlContent = "<!DOCTYPE html>" +
                    "<html><body style='font-family: Arial, sans-serif;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                    "<h2 style='color: #28a745;'>✅ Password Changed Successfully</h2>" +
                    "<p>Hi " + (fullName != null ? fullName : "User") + ",</p>" +
                    "<p>Your password has been changed successfully.</p>" +
                    "<p>If you did not make this change, please contact support immediately.</p>" +
                    "<hr>" +
                    "<p style='font-size: 12px; color: #6c757d;'>© 2024 School Asset Management System</p>" +
                    "</div></body></html>";
            
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
            
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}

