package controller.admin.room;

import dao.RoomDAO;
import dao.AssetDAO;
import model.Room;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * JSON API trả về tổng số tài sản của <strong>tất cả</strong> các phòng.
 *
 * <p>URL: GET /rooms/inventory/all</p>
 *
 * <p>Response mẫu (HTTP 200, Content-Type: application/json):</p>
 * <pre>
 * [
 *   {"roomId": 1, "roomName": "Phòng Lab 1", "location": "Tầng 2", "totalAssets": 12},
 *   {"roomId": 2, "roomName": "Phòng Hội Đồng", "location": "Tầng 1", "totalAssets": 4},
 *   ...
 * ]
 * </pre>
 *
 * <p>Được gọi bằng AJAX từ {@code room-list.jsp} để hiển thị số lượng tài sản
 * ngay trên bảng danh sách phòng mà không cần tải lại trang.</p>
 *
 * <p>Quyền truy cập: chỉ ADMIN (trả về 401/403 nếu không đủ quyền).</p>
 */
@WebServlet(name = "RoomInventoryAllServlet", urlPatterns = {"/rooms/inventory/all"})
public class RoomInventoryAllServlet extends HttpServlet {

    /** DAO thao tác với bảng Room và truy vấn thống kê tài sản. */
    private final RoomDAO roomDAO = new RoomDAO();

    /**
     * Xử lý GET /rooms/inventory/all và trả về JSON.
     * <ol>
     *   <li>Kiểm tra session – 401 nếu chưa đăng nhập.</li>
     *   <li>Kiểm tra quyền ADMIN – 403 nếu không đủ quyền.</li>
     *   <li>Lấy danh sách phòng + map số lượng tài sản, build JSON thủ công và trả về.</li>
     * </ol>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // --- 1. Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return;
        }

        // --- 2. Kiểm tra quyền ADMIN ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("ADMIN")) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
            return;
        }

        // Đặt Content-Type trước khi ghi response body
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            // Lấy danh sách tất cả phòng
            List<Room> rooms = roomDAO.getAllRooms();

            // Map<roomId, totalAssets> – truy vấn một lần để tránh N+1
            Map<Integer, Integer> countMap = roomDAO.getAssetCountPerRoom();

            // --- 3. Build JSON array thủ công (không dùng thư viện ngoài) ---
            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Room room : rooms) {
                if (!first) json.append(",");
                first = false;

                // Lấy số lượng tài sản; nếu phòng không có tài sản nào → mặc định 0
                int count = countMap.getOrDefault(room.getRoomId(), 0);

                json.append("{")
                    .append("\"roomId\":").append(room.getRoomId()).append(",")
                    .append("\"roomName\":\"").append(escapeJson(room.getRoomName())).append("\",")
                    .append("\"location\":\"").append(escapeJson(room.getLocation() != null ? room.getLocation() : "")).append("\",")
                    .append("\"totalAssets\":").append(count)
                    .append("}");
            }
            json.append("]");

            out.print(json.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            out.print("{\"error\":\"Không thể tải dữ liệu kiểm kê\"}");
        }
    }

    /**
     * Escape các ký tự đặc biệt trong chuỗi để nhúng an toàn vào JSON string.
     * Xử lý: dấu {@code \} và dấu {@code "}.
     *
     * @param s Chuỗi cần escape (có thể null)
     * @return Chuỗi đã được escape, hoặc chuỗi rỗng nếu {@code s == null}
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
