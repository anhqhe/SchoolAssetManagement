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
import java.util.ArrayList;
import java.util.List;
import model.User;

@WebServlet(name = "AdminUserListServlet", urlPatterns = {"/admin/user"})
public class UserListServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User current = session != null ? (User) session.getAttribute("currentUser") : null;
        if (current == null || current.getRoles() == null || !current.getRoles().contains("ADMIN")) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        // Lấy giá trị filter từ query string
        String status = req.getParameter("status"); // null / "" / "active" / "banned"
        String role = req.getParameter("role");     // null / "" / mã role
        String q = req.getParameter("q");          // search theo username/fullName

        try {
            // Lấy toàn bộ user rồi filter bằng Java cho dễ hiểu
            List<User> allUsers = userDAO.getAllUsers();
            List<User> filtered = new ArrayList<>();

            for (User u : allUsers) {
                // Search theo username hoặc fullName
                if (q != null && !q.trim().isEmpty()) {
                    String needle = q.trim().toLowerCase();
                    String username = u.getUsername() != null ? u.getUsername().toLowerCase() : "";                   
                    String fullName = u.getFullName() != null ? u.getFullName().toLowerCase() : "";                      
                    if (!username.contains(needle) && !fullName.contains(needle)) {
                        continue;
                    }
                }

                // Lọc theo trạng thái
                if ("active".equalsIgnoreCase(status) && !u.isActive()) {
                    continue;
                }
                if ("banned".equalsIgnoreCase(status) && u.isActive()) {
                    continue;
                }

                // Lọc theo role
                if (role != null && !role.trim().isEmpty()) {
                    List<String> roles = u.getRoles();
                    if (roles == null || !roles.contains(role.trim())) {
                        continue;
                    }
                }

                filtered.add(u);
            }

            List<String> allRoles = userDAO.getAllRoleCodes();

            req.setAttribute("users", filtered);
            req.setAttribute("allRoles", allRoles);
            req.setAttribute("statusFilter", status);
            req.setAttribute("roleFilter", role);
            req.setAttribute("q", q);

        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải danh sách người dùng.");
        }

        req.getRequestDispatcher("/views/admin/user-list.jsp").forward(req, resp);
    }
}
