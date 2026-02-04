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

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        // Validate token parameter
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Link reset password không hợp lệ.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra token có hợp lệ không
            User user = userDAO.findByResetToken(token);
            
            if (user == null) {
                request.setAttribute("errorMessage", 
                    "Link reset password không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu reset password lại.");
                request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Token hợp lệ, chuyển đến trang reset password
            request.setAttribute("token", token);
            request.setAttribute("email", user.getEmail());
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validate inputs
        if (token == null || token.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Token không hợp lệ.");
            request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
            return;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu mới.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        // Validate password strength
        if (newPassword.length() < 6) {
            request.setAttribute("errorMessage", "Mật khẩu phải có ít nhất 6 ký tự.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra token và lấy user
            User user = userDAO.findByResetToken(token);
            
            if (user == null) {
                request.setAttribute("errorMessage", 
                    "Link reset password không hợp lệ hoặc đã hết hạn. Vui lòng yêu cầu reset password lại.");
                request.getRequestDispatcher("/views/auth/forgot-password.jsp").forward(request, response);
                return;
            }
            
            // Reset password và xóa token
            boolean success = userDAO.resetPassword(user.getUserId(), newPassword);
            
            if (success) {
                // Gửi email thông báo password đã được thay đổi
                EmailUtil.sendPasswordChangedNotification(user.getEmail(), user.getFullName());
                
                // Chuyển về trang login với thông báo thành công
                request.getSession().setAttribute("successMessage", 
                    "Mật khẩu đã được đặt lại thành công! Vui lòng đăng nhập với mật khẩu mới.");
                response.sendRedirect(request.getContextPath() + "/auth/login");
            } else {
                request.setAttribute("errorMessage", "Có lỗi xảy ra khi đặt lại mật khẩu. Vui lòng thử lại.");
                request.setAttribute("token", token);
                request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/views/auth/reset-password.jsp").forward(request, response);
        }
    }
}

