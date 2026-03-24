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
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

/**
 * JSON API trả về số lượng tài sản phân theo danh mục cho <strong>một phòng cụ thể</strong>.
 *
 * <p>URL: GET /rooms/inventory/detail?roomId={id}</p>
 *
 * <p>Response mẫu (HTTP 200, Content-Type: application/json):</p>
 * <pre>
 * {
 *   "roomId": 1,
 *   "roomName": "Phòng Lab 1",
 *   "location": "Tầng 2",
 *   "categories": [
 *     {"name": "Máy tính", "count": 5},
 *     {"name": "Máy chiếu", "count": 2}
 *   ],
 *   "totalAssets": 7
 * }
 * </pre>
 *
 * <p>Được gọi bằng AJAX khi người dùng click vào một phòng trong danh sách
 * để hiển thị popup kiểm kê chi tiết theo danh mục.</p>
 *
 * <p>Quyền truy cập: chỉ ADMIN (trả về 401/403 nếu không đủ quyền).</p>
 */
@WebServlet(name = "RoomInventoryDetailServlet", urlPatterns = {"/rooms/inventory/detail"})
public class RoomInventoryDetailServlet extends HttpServlet {

    /** DAO thao tác với bảng Room và truy vấn kiểm kê tài sản. */
    private final RoomDAO roomDAO = new RoomDAO();

    /**
     * Xử lý GET /rooms/inventory/detail?roomId={id} và trả về JSON.
     * <ol>
     *   <li>Kiểm tra session – 401 nếu chưa đăng nhập.</li>
     *   <li>Kiểm tra quyền ADMIN – 403 nếu không đủ quyền.</li>
     *   <li>Validate tham số {@code roomId} – 400 nếu thiếu hoặc không hợp lệ.</li>
     *   <li>Tải thông tin phòng – 404 nếu không tìm thấy.</li>
     *   <li>Lấy danh sách tài sản theo danh mục, tính tổng và trả về JSON.</li>
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

        // --- 3. Validate tham số roomId ---
        String roomIdParam = req.getParameter("roomId");
        if (roomIdParam == null || roomIdParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.print("{\"error\":\"Thiếu tham số roomId\"}");
            return;
        }

        try {
            long roomId = Long.parseLong(roomIdParam);

            // --- 4. Tải thông tin phòng ---
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                out.print("{\"error\":\"Không tìm thấy phòng\"}");
                return;
            }

            // --- 5. Lấy danh sách tài sản theo danh mục ---
            // Mỗi phần tử Object[]: [0] = tên danh mục (String), [1] = số lượng (Integer)
            List<Object[]> categoryData = roomDAO.getAssetCountByCategoryForRoom(roomId);

            // --- 6. Build JSON object thủ công ---
            StringBuilder json = new StringBuilder();
            json.append("{")
                .append("\"roomId\":").append(room.getRoomId()).append(",")
                .append("\"roomName\":\"").append(escapeJson(room.getRoomName())).append("\",")
                .append("\"location\":\"").append(escapeJson(room.getLocation() != null ? room.getLocation() : "")).append("\",")
                .append("\"categories\":[");

            boolean first = true;
            int total = 0; // Tổng tài sản (tính bằng cách cộng dồn từng danh mục)
            for (Object[] row : categoryData) {
                if (!first) json.append(",");
                first = false;

                String catName = (String) row[0];
                int count = (Integer) row[1];
                total += count; // Cộng dồn để tính totalAssets

                json.append("{")
                    .append("\"name\":\"").append(escapeJson(catName)).append("\",")
                    .append("\"count\":").append(count)
                    .append("}");
            }

            // Kết thúc mảng categories và thêm tổng tài sản
            json.append("],")
               .append("\"totalAssets\":").append(total)
               .append("}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            // roomId không phải số hợp lệ
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            out.print("{\"error\":\"roomId không hợp lệ\"}");
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
