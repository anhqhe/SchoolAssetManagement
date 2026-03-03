package controller;

import dao.RoomDAO;
import model.Room;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "RoomListServlet", urlPatterns = {"/rooms"})
public class RoomListServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Chỉ cho phép ADMIN truy cập
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ADMIN")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            List<Room> rooms = roomDAO.getAllRooms();
            req.setAttribute("rooms", rooms);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải danh sách phòng. Vui lòng thử lại sau.");
        }

        req.getRequestDispatcher("/views/admin/room-list.jsp").forward(req, resp);
    }
}

