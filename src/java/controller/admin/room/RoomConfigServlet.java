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

@WebServlet(name = "RoomConfigServlet", urlPatterns = {"/rooms/config"})
public class RoomConfigServlet extends HttpServlet {

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

        loadDataForForm(req, idParam);
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String roomIdParam = req.getParameter("roomId");
        String roomName = req.getParameter("roomName");
        String location = req.getParameter("location");

        if (roomIdParam == null || roomIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        long roomId;
        try {
            roomId = Long.parseLong(roomIdParam.trim());
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Room ID không hợp lệ.");
            loadDataForForm(req, roomIdParam);
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            req.setAttribute("error", "Tên phòng không được để trống.");
            loadDataForForm(req, String.valueOf(roomId));
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        String cleanName = roomName.trim();
        String cleanLocation = (location != null) ? location.trim() : null;

        try {
            boolean updated = roomDAO.updateRoomBasic(roomId, cleanName, cleanLocation);
            if (!updated) {
                req.setAttribute("error", "Không thể cập nhật thông tin phòng.");
            } else {
                req.setAttribute("success", "Cập nhật thông tin phòng thành công.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra khi cập nhật phòng. Vui lòng thử lại sau.");
        }

        loadDataForForm(req, String.valueOf(roomId));
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    private void loadDataForForm(HttpServletRequest req, String roomIdParam) {
        try {
            long roomId = Long.parseLong(roomIdParam.trim());
            Room room = roomDAO.getRoomById(roomId);
            req.setAttribute("room", room);
        } catch (Exception e) {
            req.setAttribute("room", null);
            if (req.getAttribute("error") == null) {
                req.setAttribute("error", "ID phòng không hợp lệ.");
            }
        }
    }
}

