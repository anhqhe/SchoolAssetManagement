package controller.auth;

import dao.UserDAO;
import model.User;
import util.EmailUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hiển thị trang forgot password
        request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String email = request.getParameter("email");
        
        // Validate email
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        email = email.trim().toLowerCase();
        
        // Validate email format
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Địa chỉ email không hợp lệ.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Tìm user theo email
            User user = userDAO.findByEmail(email);
            
            // Để bảo mật, luôn hiển thị thông báo thành công dù email có tồn tại hay không
            // Điều này ngăn attacker biết được email nào có trong hệ thống
            if (user != null && user.isActive()) {
                // Tạo reset token (UUID ngẫu nhiên)
                String resetToken = UUID.randomUUID().toString();
                
                // Token hết hạn sau 1 giờ
                Timestamp expiryTime = new Timestamp(System.currentTimeMillis() + (60 * 60 * 1000));
                
                // Lưu token vào database
                boolean tokenSaved = userDAO.saveResetToken(user.getUserId(), resetToken, expiryTime);
                
                if (tokenSaved) {
                    // Tạo reset link
                    String resetLink = getResetLink(request, resetToken);
                    
                    // Gửi email
                    boolean emailSent = EmailUtil.sendPasswordResetEmail(email, resetLink);
                    
                    if (emailSent) {
                        System.out.println("Password reset email sent to: " + email);
                    } else {
                        System.err.println("Failed to send email to: " + email);
                    }
                }
            }
            
            // Luôn hiển thị thông báo thành công
            request.setAttribute("successMessage", 
                "Nếu email của bạn tồn tại trong hệ thống, chúng tôi đã gửi link reset password. " +
                "Vui lòng kiểm tra hộp thư của bạn (kể cả thư mục spam).");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
        }
    }

    /**
     * Tạo reset link từ token
     */
    private String getResetLink(HttpServletRequest request, String token) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        
        StringBuilder resetLink = new StringBuilder();
        resetLink.append(scheme).append("://").append(serverName);
        
        if ((scheme.equals("http") && serverPort != 80) || 
            (scheme.equals("https") && serverPort != 443)) {
            resetLink.append(":").append(serverPort);
        }
        
        resetLink.append(contextPath).append("/reset-password?token=").append(token);
        
        return resetLink.toString();
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}

