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
import java.util.Arrays;
import java.util.List;
import model.User;

@WebServlet(name = "AdminUserCreateServlet", urlPatterns = {"/admin/user/create"})
public class UserCreateServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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

        String[] rolesArr = req.getParameterValues("roles");
        List<String> selectedRoles = rolesArr != null ? Arrays.asList(rolesArr) : null;

        // Load roles cho form
        try { req.setAttribute("allRoles", userDAO.getAllRoleCodes()); } catch (SQLException ignored) {}

        // Validate
        if (username.isEmpty()) {
            forwardError(req, resp, "Username không được để trống.",
                    username, fullName, email, phone, active, selectedRoles);
            return;
        }
        if (password.isEmpty()) {
            forwardError(req, resp, "Mật khẩu không được để trống.",
                    username, fullName, email, phone, active, selectedRoles);
            return;
        }
        if (fullName.isEmpty()) {
            forwardError(req, resp, "Họ tên không được để trống.",
                    username, fullName, email, phone, active, selectedRoles);
            return;
        }
        if (email.isEmpty()) {
            forwardError(req, resp, "Email không được để trống.",
                    username, fullName, email, phone, active, selectedRoles);
            return;
        }
        if (selectedRoles == null || selectedRoles.isEmpty()) {
            forwardError(req, resp, "Vui lòng chọn ít nhất một vai trò.",
                    username, fullName, email, phone, active, selectedRoles);
            return;
        }

        try {
            if (userDAO.isUsernameTaken(username)) {
                forwardError(req, resp, "Username đã tồn tại.",
                        username, fullName, email, phone, active, selectedRoles);
                return;
            }
            if (userDAO.isEmailTaken(email)) {
                forwardError(req, resp, "Email đã tồn tại.",
                        username, fullName, email, phone, active, selectedRoles);
                return;
            }
            if (!phone.isEmpty() && userDAO.isPhoneTaken(phone, -1)) {
                forwardError(req, resp, "Số điện thoại đã tồn tại.",
                        username, fullName, email, phone, active, selectedRoles);
                return;
            }

            long newId = userDAO.createUser(username, password, fullName,
                    email,
                    phone.isEmpty() ? null : phone,
                    active, selectedRoles);
            resp.sendRedirect(req.getContextPath() + "/admin/user?success=created");

        } catch (SQLException e) {
            e.printStackTrace();
            forwardError(req, resp, "Có lỗi xảy ra khi tạo tài khoản.",
                    username, fullName, email, phone, active, selectedRoles);
        }
    }

    private void forwardError(HttpServletRequest req, HttpServletResponse resp,
                              String error, String username, String fullName,
                              String email, String phone, boolean active,
                              List<String> selectedRoles)
            throws ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("f_username", username);
        req.setAttribute("f_fullName", fullName);
        req.setAttribute("f_email", email);
        req.setAttribute("f_phone", phone);
        req.setAttribute("f_active", active);
        req.setAttribute("selectedRoles", selectedRoles);
        req.getRequestDispatcher("/views/admin/user-form.jsp").forward(req, resp);
    }
}
