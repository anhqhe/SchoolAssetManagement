package controller.staff.assetcategories;

import dao.AssetCategoryDAO;
import dao.AssetDAO;
import model.Asset;
import model.AssetCategory;
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
 * Servlet hiển thị chi tiết một danh mục tài sản, kèm danh sách các tài sản thuộc danh mục đó.
 *
 * <p>URL: GET /admin/categories/detail?id={categoryId}</p>
 *
 * <p>Request attributes được thiết lập khi thành công:</p>
 * <ul>
 *   <li>{@code category} – đối tượng {@link AssetCategory}</li>
 *   <li>{@code assets}   – danh sách {@link Asset} thuộc danh mục này</li>
 * </ul>
 *
 * <p>Quyền truy cập: ASSET_STAFF hoặc ADMIN.</p>
 */
@WebServlet(name = "AssetCategoryDetailServlet", urlPatterns = {"/admin/categories/detail"})
public class AssetCategoryDetailServlet extends HttpServlet {

    /** DAO thao tác với bảng AssetCategory. */
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    /** DAO thao tác với bảng Asset (để lấy danh sách tài sản theo danh mục). */
    private final AssetDAO assetDAO = new AssetDAO();

    /**
     * Xử lý GET /admin/categories/detail?id={categoryId}.
     * <ol>
     *   <li>Kiểm tra session và quyền (ASSET_STAFF hoặc ADMIN).</li>
     *   <li>Đọc và validate tham số {@code id} – redirect về danh sách nếu thiếu.</li>
     *   <li>Tải thông tin danh mục và danh sách tài sản của danh mục đó.</li>
     *   <li>Forward sang {@code assetcategory-detail.jsp}.</li>
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

        // --- 2. Kiểm tra quyền ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- 3. Đọc và validate tham số id ---
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Thiếu id → quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long categoryId = Long.parseLong(idParam.trim());

            // --- 4. Tải thông tin danh mục ---
            AssetCategory category = categoryDAO.getCategoryById(categoryId);

            if (category == null) {
                // Không tìm thấy → báo lỗi, vẫn forward để JSP hiển thị thông báo
                req.setAttribute("error", "Không tìm thấy danh mục tài sản.");
            } else {
                req.setAttribute("category", category);
                // Lấy toàn bộ tài sản thuộc danh mục này để hiển thị bảng
                req.setAttribute("assets", assetDAO.getAssetsByCategoryId(categoryId));
            }

        } catch (NumberFormatException e) {
            // id không phải số hợp lệ
            req.setAttribute("error", "ID danh mục không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải chi tiết danh mục tài sản. Vui lòng thử lại sau.");
        }

        // Forward sang view để render HTML
        req.getRequestDispatcher("/views/assetcategory/assetcategory-detail.jsp").forward(req, resp);
    }
}
