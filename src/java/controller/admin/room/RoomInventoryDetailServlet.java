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
 * API JSON: Trả về số lượng tài sản theo DANH MỤC cho 1 phòng cụ thể.
 * GET /rooms/inventory/detail?roomId=X
 * Response: {"roomId":1,"roomName":"...","categories":[{"name":"Máy tính","count":5},...]}
 */
@WebServlet(name = "RoomInventoryDetailServlet", urlPatterns = {"/rooms/inventory/detail"})
public class RoomInventoryDetailServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("ADMIN")) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String roomIdParam = req.getParameter("roomId");
        if (roomIdParam == null || roomIdParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"Thiếu tham số roomId\"}");
            return;
        }

        try {
            long roomId = Long.parseLong(roomIdParam);
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Không tìm thấy phòng\"}");
                return;
            }

            List<Object[]> categoryData = roomDAO.getAssetCountByCategoryForRoom(roomId);

            StringBuilder json = new StringBuilder();
            json.append("{")
                .append("\"roomId\":").append(room.getRoomId()).append(",")
                .append("\"roomName\":\"").append(escapeJson(room.getRoomName())).append("\",")
                .append("\"location\":\"").append(escapeJson(room.getLocation() != null ? room.getLocation() : "")).append("\",")
                .append("\"categories\":[");

            boolean first = true;
            int total = 0;
            for (Object[] row : categoryData) {
                if (!first) json.append(",");
                first = false;
                String catName = (String) row[0];
                int count = (Integer) row[1];
                total += count;
                json.append("{")
                    .append("\"name\":\"").append(escapeJson(catName)).append("\",")
                    .append("\"count\":").append(count)
                    .append("}");
            }
            json.append("],")
               .append("\"totalAssets\":").append(total)
               .append("}");

            out.print(json.toString());

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\":\"roomId không hợp lệ\"}");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Không thể tải dữ liệu kiểm kê\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
