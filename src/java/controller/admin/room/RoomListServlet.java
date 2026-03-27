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
 * Servlet xử lý trang danh sách phòng dành cho ADMIN.
 *
 * <p>URL: GET /rooms</p>
 * <p>
 * Lấy toàn bộ danh sách phòng từ DB rồi forward sang JSP.
 * Việc sort/search/filter được thực hiện phía client bởi DataTables,
 * nên servlet không cần xử lý phân trang hay lọc.
 * </p>
 *
 * <p>Quyền truy cập: chỉ ADMIN.</p>
 */
@WebServlet(name = "RoomListServlet", urlPatterns = {"/rooms"})
public class RoomListServlet extends HttpServlet {

    /** DAO thao tác với bảng Room trong database. */
    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Xử lý GET /rooms.
     * <ol>
     *   <li>Kiểm tra session hợp lệ – nếu không, chuyển hướng về trang đăng nhập.</li>
     *   <li>Kiểm tra quyền ADMIN – nếu không đủ quyền, trả về 403 Forbidden.</li>
     *   <li>Tải toàn bộ danh sách phòng và đặt vào request attribute "rooms".</li>
     *   <li>Forward sang {@code /views/admin/room-list.jsp} để render.</li>
     * </ol>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // --- 1. Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null) {
            // Chưa đăng nhập → chuyển về trang login
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

        try {
            // Lấy toàn bộ phòng để hiển thị bảng
            // (DataTables đang làm sort/search phía client – không cần giới hạn ở đây)
            List<Room> rooms = roomDAO.getAllRooms();
            req.setAttribute("rooms", rooms);
            req.setAttribute("teachers", userDAO.getAllTeachers());
            setAssignMessage(req);
        } catch (SQLException e) {
            e.printStackTrace();
            // Không leak thông tin lỗi SQL ra UI; chỉ trả thông báo chung cho người dùng
            req.setAttribute("error", "Không thể tải danh sách phòng. Vui lòng thử lại sau.");
        }

        // Forward sang view để render HTML
        req.getRequestDispatcher("/views/admin/room-list.jsp").forward(req, resp);
    }

    private void setAssignMessage(HttpServletRequest req) {
        String assign = req.getParameter("assign");
        if ("success".equals(assign)) {
            req.setAttribute("success", "Gán giáo viên cho phòng thành công.");
            return;
        }

        String assignError = req.getParameter("assignError");
        if (assignError == null || assignError.isEmpty()) {
            return;
        }

        String message;
        switch (assignError) {
            case "invalid_room":
                message = "Phòng không hợp lệ.";
                break;
            case "invalid_teacher":
                message = "Giáo viên không hợp lệ.";
                break;
            case "room_not_found":
                message = "Không tìm thấy phòng cần gán.";
                break;
            case "teacher_not_found":
                message = "Không tìm thấy giáo viên để gán.";
                break;
            default:
                message = "Không thể gán giáo viên cho phòng. Vui lòng thử lại.";
                break;
        }
        req.setAttribute("error", message);
    }
}
