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
 * API JSON: Trả về tổng số tài sản của TẤT CẢ các phòng.
 * GET /rooms/inventory/all
 * Response: [{"roomId":1,"roomName":"...","totalAssets":5}, ...]
 */
@WebServlet(name = "RoomInventoryAllServlet", urlPatterns = {"/rooms/inventory/all"})
public class RoomInventoryAllServlet extends HttpServlet {

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

        try {
            List<Room> rooms = roomDAO.getAllRooms();
            Map<Integer, Integer> countMap = roomDAO.getAssetCountPerRoom();

            StringBuilder json = new StringBuilder("[");
            boolean first = true;
            for (Room room : rooms) {
                if (!first) json.append(",");
                first = false;
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
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"Không thể tải dữ liệu kiểm kê\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
