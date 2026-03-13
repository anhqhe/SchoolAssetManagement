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

@WebServlet(name = "AdminUserToggleActiveServlet", urlPatterns = {"/admin/user/toggle-active"})
public class UserToggleActiveServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User current = session != null ? (User) session.getAttribute("currentUser") : null;
        if (current == null || current.getRoles() == null || !current.getRoles().contains("ADMIN")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            boolean newActive = "true".equals(req.getParameter("active"));
            userDAO.setUserActive(id, newActive);
            String msg = newActive ? "unbanned" : "banned";
            resp.sendRedirect(req.getContextPath() + "/admin/user?success=" + msg);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/user");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/user?error=toggle");
        }
    }
}
