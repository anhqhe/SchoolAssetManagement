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

/**
 * Servlet hiển thị chi tiết một phòng cụ thể, bao gồm danh sách tài sản có phân trang.
 *
 * <p>URL: GET /rooms/detail?id={roomId}</p>
 * <p>
 * Tải toàn bộ tài sản của phòng từ DB, sau đó tự phân trang bằng {@code subList}
 * (phù hợp với số lượng tài sản vừa phải; nếu dữ liệu lớn nên chuyển sang phân trang DB).
 * </p>
 *
 * <p>Quyền truy cập: chỉ ADMIN.</p>
 */
@WebServlet(name = "RoomDetailServlet", urlPatterns = {"/rooms/detail"})
public class RoomDetailServlet extends HttpServlet {

    /** Số tài sản hiển thị trên mỗi trang. */
    private static final int PAGE_SIZE = 10;

    /** DAO thao tác với bảng Room. */
    private final RoomDAO roomDAO = new RoomDAO();

    /** DAO thao tác với bảng Asset. */
    private final AssetDAO assetDAO = new AssetDAO();

    /**
     * Xử lý GET /rooms/detail?id={roomId}.
     * <ol>
     *   <li>Kiểm tra session và quyền ADMIN.</li>
     *   <li>Đọc tham số {@code id} – nếu thiếu, redirect về danh sách phòng.</li>
     *   <li>Tải thông tin phòng và toàn bộ tài sản; tính toán phân trang.</li>
     *   <li>Đặt các attribute vào request rồi forward sang {@code room-detail.jsp}.</li>
     * </ol>
     *
     * <p>Request attributes được thiết lập khi thành công:</p>
     * <ul>
     *   <li>{@code room} – đối tượng {@link Room}</li>
     *   <li>{@code assetsInRoom} – danh sách tài sản của trang hiện tại</li>
     *   <li>{@code totalAssets} – tổng số tài sản trong phòng</li>
     *   <li>{@code totalPages} – tổng số trang</li>
     *   <li>{@code currentPage} – trang đang xem (bắt đầu từ 1)</li>
     * </ul>
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

        // --- 3. Đọc và validate tham số id ---
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // id không có → quay về danh sách thay vì hiển thị trang trống
            resp.sendRedirect(req.getContextPath() + "/rooms");
            return;
        }

        try {
            long roomId = Long.parseLong(idParam);

            // --- 4. Tải dữ liệu phòng ---
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                req.setAttribute("error", "Không tìm thấy phòng.");
            } else {
                req.setAttribute("room", room);

                // Lấy toàn bộ tài sản trong phòng (phân trang thực hiện in-memory)
                List<Asset> allAssets = assetDAO.getAssetsByRoomId(roomId);
                int totalAssets = (allAssets != null) ? allAssets.size() : 0;

                // Tổng số trang: tối thiểu là 1 để tránh chia cho 0
                int totalPages  = (totalAssets == 0) ? 1 : (int) Math.ceil((double) totalAssets / PAGE_SIZE);

                // --- 5. Đọc tham số page (mặc định = 1, không vượt quá totalPages) ---
                int currentPage = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null && !pageParam.isEmpty()) {
                    try {
                        currentPage = Integer.parseInt(pageParam);
                    } catch (NumberFormatException ignored) {
                        // Giữ nguyên currentPage = 1 nếu tham số không hợp lệ
                    }
                }
                // Clamp trang về khoảng [1, totalPages]
                if (currentPage < 1) currentPage = 1;
                if (currentPage > totalPages) currentPage = totalPages;

                // --- 6. Cắt danh sách tài sản theo trang ---
                int fromIndex = (currentPage - 1) * PAGE_SIZE;
                int toIndex   = Math.min(fromIndex + PAGE_SIZE, totalAssets);
                List<Asset> pagedAssets = (totalAssets > 0) ? allAssets.subList(fromIndex, toIndex) : allAssets;

                // Đặt kết quả vào request để JSP render
                req.setAttribute("assetsInRoom", pagedAssets);
                req.setAttribute("totalAssets",  totalAssets);
                req.setAttribute("totalPages",   totalPages);
                req.setAttribute("currentPage",  currentPage);
            }
        } catch (NumberFormatException e) {
            // id không phải số hợp lệ
            req.setAttribute("error", "ID phòng không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải thông tin phòng. Vui lòng thử lại sau.");
        }

        // Forward sang view để render HTML
        req.getRequestDispatcher("/views/admin/room-detail.jsp").forward(req, resp);
    }
}
