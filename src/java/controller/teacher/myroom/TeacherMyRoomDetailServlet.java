package controller.teacher.myroom;

import dao.AssetDAO;
import dao.RoomDAO;
import model.Asset;
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

@WebServlet(name = "TeacherMyRoomDetailServlet", urlPatterns = {"/teacher/myrooms/detail"})
public class TeacherMyRoomDetailServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private final RoomDAO roomDAO = new RoomDAO();
    private final AssetDAO assetDAO = new AssetDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("TEACHER")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/teacher/myrooms");
            return;
        }

        try {
            long roomId = Long.parseLong(idParam);

            // Verify if this teacher actually manages this room
            User primaryTeacher = roomDAO.getPrimaryTeacherByRoomId(roomId);
            if (primaryTeacher == null || primaryTeacher.getUserId() != currentUser.getUserId()) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền quản lý phòng này.");
                return;
            }

            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                req.setAttribute("error", "Không tìm thấy phòng.");
            } else {
                req.setAttribute("room", room);

                List<Asset> allAssets = assetDAO.getAssetsByRoomId(roomId);
                int totalAssets = (allAssets != null) ? allAssets.size() : 0;
                int totalPages  = (totalAssets == 0) ? 1 : (int) Math.ceil((double) totalAssets / PAGE_SIZE);

                int currentPage = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        currentPage = Integer.parseInt(pageParam);
                    } catch (NumberFormatException ignored) {}
                }
                
                if (currentPage < 1) currentPage = 1;
                if (currentPage > totalPages) currentPage = totalPages;

                int fromIndex = (currentPage - 1) * PAGE_SIZE;
                int toIndex   = Math.min(fromIndex + PAGE_SIZE, totalAssets);
                List<Asset> pagedAssets = (totalAssets > 0) ? allAssets.subList(fromIndex, toIndex) : allAssets;

                req.setAttribute("assetsInRoom", pagedAssets);
                req.setAttribute("totalAssets",  totalAssets);
                req.setAttribute("totalPages",   totalPages);
                req.setAttribute("currentPage",  currentPage);
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID phòng không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải thông tin phòng. Vui lòng thử lại sau.");
        }

        req.getRequestDispatcher("/views/teacher/myroom/myroom-detail.jsp").forward(req, resp);
    }
}
