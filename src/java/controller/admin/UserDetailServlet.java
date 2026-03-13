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

@WebServlet(name = "AdminUserDetailServlet", urlPatterns = {"/admin/user/detail"})
public class UserDetailServlet extends HttpServlet {

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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            User u = userDAO.findById(id);
            if (u == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/user");
                return;
            }
            req.setAttribute("viewUser", u);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải thông tin người dùng.");
        }

        req.getRequestDispatcher("/views/admin/user-detail.jsp").forward(req, resp);
    }
}
