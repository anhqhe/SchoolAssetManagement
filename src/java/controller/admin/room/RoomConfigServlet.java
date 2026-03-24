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
        /*
         * Trang cấu hình phòng (ADMIN):
         * - GET: hiển thị form với dữ liệu phòng hiện tại
         * - POST: cập nhật RoomName/Location
         *
         * Quyền truy cập phải được kiểm soát ở servlet (không chỉ ẩn nút ở UI).
         */
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
            // Thiếu id -> quay về list để tránh form rỗng
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        // Load dữ liệu phòng để fill vào form
        loadDataForForm(req, idParam);
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
         * Cập nhật thông tin cơ bản của phòng.
         * Lưu ý: hiện chưa có CSRF token cho form này; nếu triển khai production nên bổ sung.
         */
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
            // roomId bị sửa trên client hoặc sai định dạng
            req.setAttribute("error", "Room ID không hợp lệ.");
            loadDataForForm(req, roomIdParam);
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        if (roomName == null || roomName.trim().isEmpty()) {
            // Validate tối thiểu: không cho tên phòng rỗng
            req.setAttribute("error", "Tên phòng không được để trống.");
            loadDataForForm(req, String.valueOf(roomId));
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        // Chuẩn hoá input: trim, và cho phép location null
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

        // Reload lại dữ liệu sau khi update để form hiển thị dữ liệu mới nhất
        loadDataForForm(req, String.valueOf(roomId));
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    private void loadDataForForm(HttpServletRequest req, String roomIdParam) {
        try {
            long roomId = Long.parseLong(roomIdParam.trim());
            Room room = roomDAO.getRoomById(roomId);
            req.setAttribute("room", room);
        } catch (Exception e) {
            // Fail-soft cho form: nếu id lỗi thì để room=null và báo error chung
            req.setAttribute("room", null);
            if (req.getAttribute("error") == null) {
                req.setAttribute("error", "ID phòng không hợp lệ.");
            }
        }
    }
}

