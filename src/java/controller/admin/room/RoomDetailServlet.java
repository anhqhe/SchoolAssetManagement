package controller.admin.room;

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

@WebServlet(name = "RoomDetailServlet", urlPatterns = {"/rooms/detail"})
public class RoomDetailServlet extends HttpServlet {

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

                // Lấy toàn bộ tài sản trong phòng
                List<Asset> allAssets = assetDAO.getAssetsByRoomId(roomId);
                int totalAssets = (allAssets != null) ? allAssets.size() : 0;
                int totalPages  = (totalAssets == 0) ? 1 : (int) Math.ceil((double) totalAssets / PAGE_SIZE);

                // Đọc tham số page (mặc định = 1)
                int currentPage = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        currentPage = Integer.parseInt(pageParam);
                    } catch (NumberFormatException ignored) {}
                }
                if (currentPage < 1) currentPage = 1;
                if (currentPage > totalPages) currentPage = totalPages;

                // Cắt danh sách theo trang
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

        req.getRequestDispatcher("/views/admin/room-detail.jsp").forward(req, resp);
    }
}
