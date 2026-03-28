package controller.staff.assetcategories;

import dao.AssetCategoryDAO;
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
 * Servlet hiển thị danh sách tất cả danh mục tài sản.
 *
 * <p>URL: GET /admin/categories</p>
 * <p>
 * Lấy toàn bộ danh mục từ DB và forward sang JSP để render bảng.
 * </p>
 *
 * <p>Quyền truy cập: ASSET_STAFF.</p>
 */
@WebServlet(name = "AssetCategoryListServlet", urlPatterns = {"/admin/categories"})
public class AssetCategoryListServlet extends HttpServlet {

    /** DAO thao tác với bảng AssetCategory trong database. */
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    /**
     * Xử lý GET /admin/categories.
     * <ol>
     *   <li>Kiểm tra session hợp lệ – nếu không, redirect về trang đăng nhập.</li>
     *   <li>Kiểm tra quyền (ASSET_STAFF) – trả về 403 nếu không đủ quyền.</li>
     *   <li>Tải danh sách tất cả danh mục và đặt vào request attribute {@code "categories"}.</li>
     *   <li>Forward sang {@code /views/assetcategory/assetcategory-list.jsp}.</li>
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

        // --- 2. Kiểm tra quyền: Chỉ ASSET_STAFF mới được phép xem ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // Tải toàn bộ danh mục tài sản để hiển thị bảng
            List<AssetCategory> categories = categoryDAO.getAllCategories();
            req.setAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải danh sách danh mục tài sản. Vui lòng thử lại sau.");
        }

        // Forward sang view để render HTML
        req.getRequestDispatcher("/views/assetcategory/assetcategory-list.jsp").forward(req, resp);
    }
}
