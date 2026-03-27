package controller.staff.assetcategories;

import dao.AssetCategoryDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.AssetCategory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet xử lý xoá một danh mục tài sản.
 *
 * <p>URL: POST /admin/categories/delete</p>
 * <p>
 * Chỉ hỗ trợ POST (không có GET) để tránh vô tình kích hoạt xoá qua đường dẫn.
 * Sau khi xoá (thành công hay thất bại), servlet reload danh sách danh mục
 * và forward về {@code assetcategory-list.jsp} với thông báo kết quả.
 * </p>
 *
 * <p>Quyền truy cập: ASSET_STAFF.</p>
 */
@WebServlet(name = "AssetCategoryDeleteServlet", urlPatterns = {"/admin/categories/delete"})
public class AssetCategoryDeleteServlet extends HttpServlet {

    /** DAO thao tác với bảng AssetCategory. */
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();
    
    private static final String DELETE_BLOCKED_MESSAGE =
            "Không thể xóa danh mục này vì mã danh mục đang được sử dụng hoặc vẫn còn tài sản thuộc danh mục.";

    /**
     * Xử lý xoá danh mục tài sản (POST).
     * <ol>
     *   <li>Kiểm tra session và quyền.</li>
     *   <li>Đọc và validate tham số {@code id} – redirect về danh sách nếu thiếu/sai.</li>
     *   <li>Gọi DAO xoá bản ghi; set thông báo thành công hoặc lỗi.</li>
     *   <li>Reload danh sách danh mục và forward về {@code assetcategory-list.jsp}.</li>
     * </ol>
     *
     * <p>Form parameter dự kiến:</p>
     * <ul>
     *   <li>{@code id} – ID danh mục cần xoá (hidden field)</li>
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

        // --- 2. Kiểm tra quyền: Chỉ ASSET_STAFF được phép xóa ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- 3. Đọc và validate tham số id ---
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Không có id → không xoá gì cả, quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            AssetCategory category = categoryDAO.getCategoryById(id);
            String categoryCode = (category != null) ? category.getCategoryCode() : null;
            String blockedMessage = (categoryCode != null && !categoryCode.trim().isEmpty())
                    ? "Không thể xóa danh mục '" + categoryCode
                    + "' vì mã danh mục đang được sử dụng hoặc vẫn còn tài sản thuộc danh mục."
                    : DELETE_BLOCKED_MESSAGE;

            // --- 4. Validate: kiểm tra tài sản còn tồn tại trong danh mục ---
            // Quy tắc: không được xoá danh mục khi còn bất kỳ tài sản nào thuộc về nó,
            // dù danh mục đó đang active hay inactive.
            int assetCount = categoryDAO.countAssetsByCategoryId(id);
            if (assetCount > 0) {
                req.setAttribute("error", blockedMessage);
            } else {
                // --- 5. Thực hiện xoá ---
                boolean deleted = categoryDAO.deleteCategory(id);
                if (deleted) {
                    req.setAttribute("success", "Đã xóa danh mục tài sản.");
                } else {
                    req.setAttribute("error", blockedMessage);
                }
            }
        } catch (NumberFormatException e) {
            // id không phải số hợp lệ → quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", DELETE_BLOCKED_MESSAGE);
        }

        // --- 5. Reload danh sách để hiển thị trạng thái mới nhất sau khi xoá ---
        try {
            req.setAttribute("categories", categoryDAO.getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace();
            // Không có danh sách cũng vẫn forward về list; JSP sẽ hiển thị lỗi riêng
        }

        req.getRequestDispatcher("/views/assetcategory/assetcategory-list.jsp").forward(req, resp);
    }
}
