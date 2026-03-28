package controller.admin;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import model.User;

@WebServlet(name = "AdminUserCreateServlet", urlPatterns = {"/admin/user/create"})
public class UserCreateServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private static final int USERNAME_MIN_LEN = 3;
    private static final int USERNAME_MAX_LEN = 30;
    private static final int FULLNAME_MIN_LEN = 2;
    private static final int FULLNAME_MAX_LEN = 100;

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        User current = session != null ? (User) session.getAttribute("currentUser") : null;
        return current != null && current.getRoles() != null && current.getRoles().contains("ADMIN");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }
        try {
            req.setAttribute("allRoles", userDAO.getAllRoleCodes());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        req.getRequestDispatcher("/views/admin/user-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!isAdmin(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String username = req.getParameter("username") != null ? req.getParameter("username").trim() : "";
        String password = req.getParameter("password") != null ? req.getParameter("password").trim() : "";
        String fullName = req.getParameter("fullName") != null ? req.getParameter("fullName").trim() : "";
        String email    = req.getParameter("email") != null ? req.getParameter("email").trim() : "";
        String phone    = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";
        boolean active  = "true".equals(req.getParameter("active"));

        String selectedRole = req.getParameter("role") != null ? req.getParameter("role").trim() : "";

        // Load roles cho form
        try { req.setAttribute("allRoles", userDAO.getAllRoleCodes()); } catch (SQLException ignored) {}

        // Validate
        if (username.isEmpty()) {
            forwardError(req, resp, "Username không được để trống.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (username.length() < USERNAME_MIN_LEN || username.length() > USERNAME_MAX_LEN) {
            forwardError(req, resp,
                    "Username phải từ " + USERNAME_MIN_LEN + " đến " + USERNAME_MAX_LEN + " ký tự.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (password.isEmpty()) {
            forwardError(req, resp, "Mật khẩu không được để trống.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (!isValidPassword(password)) {
            forwardError(req, resp, "Mật khẩu phải có ít nhất 6 ký tự, chứa ít nhất 1 chữ hoa và 1 số.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (fullName.isEmpty()) {
            forwardError(req, resp, "Họ tên không được để trống.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (fullName.length() < FULLNAME_MIN_LEN || fullName.length() > FULLNAME_MAX_LEN) {
            forwardError(req, resp,
                    "Họ tên phải từ " + FULLNAME_MIN_LEN + " đến " + FULLNAME_MAX_LEN + " ký tự.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (email.isEmpty()) {
            forwardError(req, resp, "Email không được để trống.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        String normalizedPhone = normalizePhone(phone);
        if (!phone.isEmpty() && normalizedPhone == null) {
            forwardError(req, resp, "Số điện thoại không hợp lệ. Ví dụ: 0912345678 hoặc +84 912345678.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }
        if (selectedRole.isEmpty()) {
            forwardError(req, resp, "Vui lòng chọn một vai trò.",
                    username, fullName, email, phone, active, selectedRole);
            return;
        }

        try {
            if (userDAO.isUsernameTaken(username)) {
                forwardError(req, resp, "Username đã tồn tại.",
                        username, fullName, email, phone, active, selectedRole);
                return;
            }
            if (userDAO.isEmailTaken(email)) {
                forwardError(req, resp, "Email đã tồn tại.",
                        username, fullName, email, phone, active, selectedRole);
                return;
            }
            if (normalizedPhone != null && userDAO.isPhoneTaken(normalizedPhone, -1)) {
                forwardError(req, resp, "Số điện thoại đã tồn tại.",
                        username, fullName, email, phone, active, selectedRole);
                return;
            }

            long newId = userDAO.createUser(username, password, fullName,
                    email,
                    normalizedPhone == null ? null : normalizedPhone,
                    active, selectedRole);
            resp.sendRedirect(req.getContextPath() + "/admin/user?success=created");

        } catch (SQLException e) {
            e.printStackTrace();
            forwardError(req, resp, "Có lỗi xảy ra khi tạo tài khoản.",
                    username, fullName, email, phone, active, selectedRole);
        }
    }

    private void forwardError(HttpServletRequest req, HttpServletResponse resp,
                              String error, String username, String fullName,
                              String email, String phone, boolean active,
                              String selectedRole)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("f_username", username);
        req.setAttribute("f_fullName", fullName);
        req.setAttribute("f_email", email);
        req.setAttribute("f_phone", phone);
        req.setAttribute("f_active", active);
        req.setAttribute("selectedRole", selectedRole);
        req.getRequestDispatcher("/views/admin/user-form.jsp").forward(req, resp);
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUppercase && hasDigit;
    }

    /**
     * Chuẩn hoá số điện thoại về dạng 0XXXXXXXXX (10 số).
     * - Cho phép nhập có space/dash/dot.
     * - Cho phép +84XXXXXXXXX hoặc 84XXXXXXXXX (sẽ đổi thành 0XXXXXXXXX).
     * @return số đã chuẩn hoá, hoặc null nếu không hợp lệ
     */
    private String normalizePhone(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        // giữ lại số và dấu +
        s = s.replaceAll("[^0-9+]", "");

        if (s.startsWith("+84")) {
            s = "0" + s.substring(3);
        } else if (s.startsWith("84")) {
            s = "0" + s.substring(2);
        }

        // VN mobile: 10 digits starting with 0
        if (!s.matches("^0\\d{9}$")) {
            return null;
        }
        return s;
    }
}
