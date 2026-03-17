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

@WebServlet(name = "AdminUserUpdateServlet", urlPatterns = {"/admin/user/update"})
public class UserUpdateServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    private boolean isAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        User current = session != null ? (User) session.getAttribute("currentUser") : null;
        return current != null && current.getRoles() != null && current.getRoles().contains("ADMIN");
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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
            return;
        }

        String username = req.getParameter("username") != null ? req.getParameter("username").trim() : "";
        String fullName = req.getParameter("fullName") != null ? req.getParameter("fullName").trim() : "";
        String email = req.getParameter("email") != null ? req.getParameter("email").trim() : "";
        String phone = req.getParameter("phone") != null ? req.getParameter("phone").trim() : "";

        // Validate giống create (trừ password)
        if (username.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=username_empty");
            return;
        }
        if (username.length() < 3 || username.length() > 30) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=username_len");
            return;
        }
        if (fullName.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=fullname_empty");
            return;
        }
        if (fullName.length() < 2 || fullName.length() > 100) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=fullname_len");
            return;
        }
        if (email.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=email_empty");
            return;
        }

        String normalizedPhone = null;
        if (!phone.isEmpty()) {
            normalizedPhone = normalizePhone(phone);
            if (normalizedPhone == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=phone_invalid");
                return;
            }
        }

        try {
            if (userDAO.isUsernameTaken(username, id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=username_taken");
                return;
            }
            if (userDAO.isEmailTaken(email, id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=email_taken");
                return;
            }
            if (normalizedPhone != null && userDAO.isPhoneTaken(normalizedPhone, id)) {
                resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=phone_taken");
                return;
            }

            boolean ok = userDAO.updateUserAdmin(id, username, fullName, email, normalizedPhone);
            if (!ok) {
                resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=update_failed");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&success=updated");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/user/detail?id=" + id + "&error=update_failed");
        }
    }

    private String normalizePhone(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        s = s.replaceAll("[^0-9+]", "");

        if (s.startsWith("+84")) {
            s = "0" + s.substring(3);
        } else if (s.startsWith("84")) {
            s = "0" + s.substring(2);
        }

        if (!s.matches("^0\\d{9}$")) {
            return null;
        }
        return s;
    }
}

