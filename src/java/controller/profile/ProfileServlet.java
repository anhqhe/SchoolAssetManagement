package controller.profile;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

@WebServlet(name = "ProfileServlet", urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            User freshUser = userDAO.findById(currentUser.getUserId());
            if (freshUser == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                request.getRequestDispatcher("/views/common/404.jsp").forward(request, response);
                return;
            }
            request.setAttribute("profileUser", freshUser);
            boolean editMode = "1".equals(request.getParameter("edit")) || "true".equalsIgnoreCase(request.getParameter("edit"));
            request.setAttribute("editMode", editMode);
            request.getRequestDispatcher("/views/profile/profile.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi hệ thống, vui lòng thử lại.");
            request.getRequestDispatcher("/views/profile/profile.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        String fullName = trimToNull(request.getParameter("fullName"));
        String email = trimToNull(request.getParameter("email"));
        String phone = trimToNull(request.getParameter("phone"));

        if (fullName == null) {
            request.setAttribute("error", "Họ và tên không được để trống.");
            request.setAttribute("editMode", true);
            doGet(request, response);
            return;
        }
        if (email == null) {
            request.setAttribute("error", "Email không được để trống.");
            request.setAttribute("editMode", true);
            doGet(request, response);
            return;
        }
        if (phone == null) {
            request.setAttribute("error", "Số điện thoại không được để trống.");
            request.setAttribute("editMode", true);
            doGet(request, response);
            return;
        }
        String normalizedPhone = normalizeVietnamPhone(phone);
        if (normalizedPhone == null) {
            request.setAttribute("error", "Số điện thoại không hợp lệ (VD: 0912345678 hoặc +84912345678).");
            request.setAttribute("editMode", true);
            doGet(request, response);
            return;
        }

        try {
            if (userDAO.isPhoneTaken(normalizedPhone, currentUser.getUserId())) {
                request.setAttribute("error", "Số điện thoại đã được sử dụng bởi tài khoản khác.");
                request.setAttribute("editMode", true);
                doGet(request, response);
                return;
            }

            boolean ok = userDAO.updateProfile(currentUser.getUserId(), fullName, email, normalizedPhone);
            if (!ok) {
                request.setAttribute("error", "Cập nhật thất bại. Vui lòng thử lại.");
                request.setAttribute("editMode", true);
                doGet(request, response);
                return;
            }

            // refresh session user display fields
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(normalizedPhone);
            session.setAttribute("currentUser", currentUser);

            session.setAttribute("successMessage", "Cập nhật hồ sơ thành công.");
            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi hệ thống, vui lòng thử lại.");
            request.setAttribute("editMode", true);
            doGet(request, response);
        }
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Normalize & validate Vietnam phone numbers.
     * Accepts input like: 0912345678, 09 1234 5678, +84 912345678, 028-1234-5678
     * Returns normalized string (digits only, starting with 0) or null if invalid.
     */
    private static String normalizeVietnamPhone(String input) {
        if (input == null) return null;
        String s = input.trim();
        if (s.isEmpty()) return null;

        // Keep only digits and leading '+'
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '+' && sb.length() == 0) sb.append(c);
            else if (c >= '0' && c <= '9') sb.append(c);
        }
        s = sb.toString();

        // Convert +84xxxxxxxxx to 0xxxxxxxxx
        if (s.startsWith("+84")) {
            s = "0" + s.substring(3);
        }

        // Now must be digits only
        if (!s.matches("^0\\d+$")) return null;

        // Mobile: 10 digits, starts with 03/05/07/08/09
        if (s.matches("^0[35789]\\d{8}$")) return s;

        // Landline (rough): 0 + 2xx(area) + 7-8 digits (total 10-11)
        if (s.matches("^0(2\\d{1,2})\\d{7,8}$")) return s;

        return null;
    }
}

