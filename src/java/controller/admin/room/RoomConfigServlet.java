package controller.admin.room;

import dao.RoomDAO;
import dao.UserDAO;
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

/**
 * Servlet quản lý cấu hình (chỉnh sửa thông tin cơ bản) của một phòng.
 *
 * <p>URL:</p>
 * <ul>
 *   <li>GET  /rooms/config?id={roomId} – Hiển thị form với dữ liệu phòng hiện tại.</li>
 *   <li>POST /rooms/config             – Lưu cập nhật RoomName và Location.</li>
 * <strong>Lưu ý bảo mật:</strong> Hiện chưa có CSRF token cho form này.
 * Nếu triển khai production nên bổ sung CSRF protection.
 * </p>
 */
@WebServlet(name = "RoomConfigServlet", urlPatterns = {"/rooms/config"})
public class RoomConfigServlet extends HttpServlet {

    /** DAO thao tác với bảng Room. */
    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Hiển thị form cấu hình phòng (GET).
     * <ol>
     *   <li>Kiểm tra session và quyền ADMIN.</li>
     *   <li>Yêu cầu tham số {@code id} – nếu thiếu, redirect về danh sách phòng.</li>
     *   <li>Tải dữ liệu phòng và forward sang {@code room-config.jsp}.</li>
     * </ol>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // --- 1. Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        // --- 2. Kiểm tra quyền ADMIN ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ADMIN")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- 3. Validate tham số id ---
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Thiếu id → quay về danh sách để tránh hiển thị form rỗng
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        // Load dữ liệu phòng để điền vào form, rồi forward sang view
        loadDataForForm(req, idParam);
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    /**
     * Xử lý cập nhật cấu hình phòng (POST).
     * <ol>
     *   <li>Kiểm tra session và quyền ADMIN.</li>
     *   <li>Validate {@code roomId} (bắt buộc) và {@code roomName} (không được rỗng).</li>
     *   <li>Chuẩn hoá input (trim) và gọi DAO để cập nhật DB.</li>
     *   <li>Reload dữ liệu phòng và forward lại {@code room-config.jsp} với thông báo kết quả.</li>
     * </ol>
     *
     * <p>Form parameters dự kiến:</p>
     * <ul>
     *   <li>{@code roomId}  – ID phòng cần cập nhật (hidden field)</li>
     *   <li>{@code roomName} – Tên phòng mới (bắt buộc)</li>
     *   <li>{@code location} – Vị trí phòng (tuỳ chọn)</li>
     * </ul>
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // --- 1. Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        // --- 2. Kiểm tra quyền ADMIN ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ADMIN")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- 3. Đọc các tham số từ form ---
        String roomIdParam = req.getParameter("roomId");
        String roomName    = req.getParameter("roomName");
        String location    = req.getParameter("location");

        if (roomIdParam == null || roomIdParam.trim().isEmpty()) {
            // Không có roomId → không biết cập nhật phòng nào, quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        // --- 4. Parse roomId ---
        long roomId;
        try {
            roomId = Long.parseLong(roomIdParam.trim());
        } catch (NumberFormatException e) {
            // roomId bị sửa ở client hoặc sai định dạng → báo lỗi và giữ nguyên form
            req.setAttribute("error", "Room ID không hợp lệ.");
            loadDataForForm(req, roomIdParam);
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        // --- 5. Validate tên phòng (bắt buộc) ---
        if (roomName == null || roomName.trim().isEmpty()) {
            req.setAttribute("error", "Tên phòng không được để trống.");
            loadDataForForm(req, String.valueOf(roomId));
            req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
            return;
        }

        // Chuẩn hoá input: trim và cho phép location là null (không bắt buộc)
        String cleanName     = roomName.trim();
        String cleanLocation = (location != null) ? location.trim() : null;
        String teacherIdParam = req.getParameter("teacherId");
        Long teacherId = null;
        if (teacherIdParam != null && !teacherIdParam.trim().isEmpty()) {
            try {
                teacherId = Long.parseLong(teacherIdParam.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        // --- 6. Thực hiện cập nhật DB ---
        try {
            boolean updated = roomDAO.updateRoomBasic(roomId, cleanName, cleanLocation);
            if (!updated) {
                req.setAttribute("error", "Không thể cập nhật thông tin phòng.");
            } else {
                roomDAO.setPrimaryTeacherForRoom(roomId, teacherId);
                req.setAttribute("success", "Cập nhật thông tin phòng thành công.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra khi cập nhật phòng. Vui lòng thử lại sau.");
        }

        // Reload lại dữ liệu mới nhất từ DB để form hiển thị đúng sau khi update
        loadDataForForm(req, String.valueOf(roomId));
        req.getRequestDispatcher("/views/admin/room-config.jsp").forward(req, resp);
    }

    /**
     * Helper: tải thông tin phòng từ DB và đặt vào request attribute {@code "room"}.
     *
     * <p>
     * Dùng chung cho cả GET (hiển thị ban đầu) và POST (reload sau khi update).
     * Nếu có lỗi, đặt {@code room = null} và thiết lập thông báo lỗi chung
     * (chỉ khi chưa có lỗi nào khác được set trước đó).
     * </p>
     *
     * @param req        HttpServletRequest hiện tại
     * @param roomIdParam Chuỗi ID phòng (chưa parse)
     */
    private void loadDataForForm(HttpServletRequest req, String roomIdParam) {
        try {
            long roomId = Long.parseLong(roomIdParam.trim());
            Room room = roomDAO.getRoomById(roomId);
            req.setAttribute("room", room);
            
            List<User> teachers = userDAO.getAllTeachers();
            req.setAttribute("teachers", teachers);
            
            User primaryTeacher = roomDAO.getPrimaryTeacherByRoomId(roomId);
            req.setAttribute("primaryTeacher", primaryTeacher);
            
        } catch (Exception e) {
            // Fail-soft: nếu id lỗi hoặc DB lỗi, để room = null và báo lỗi chung
            req.setAttribute("room", null);
            if (req.getAttribute("error") == null) {
                // Không ghi đè thông báo lỗi đã được set trước đó (ví dụ: validate lỗi)
                req.setAttribute("error", "ID phòng không hợp lệ.");
            }
        }
    }
}
