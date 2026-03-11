package controller.admin.room;

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

@WebServlet(name = "RoomDetailServlet", urlPatterns = {"/rooms/detail"})
public class RoomDetailServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        try {
            long roomId = Long.parseLong(idParam);
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                req.setAttribute("error", "Không tìm thấy phòng.");
            } else {
                req.setAttribute("room", room);
                try {
                    req.setAttribute("roomHead", roomDAO.getPrimaryTeacherByRoomId(roomId));
                } catch (SQLException ignored) {
                    req.setAttribute("roomHead", null);
                }
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID phòng không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải thông tin phòng. Vui lòng thử lại sau.");
        }

        req.getRequestDispatcher("/views/admin/room-detail.jsp").forward(req, resp);
    }
}

