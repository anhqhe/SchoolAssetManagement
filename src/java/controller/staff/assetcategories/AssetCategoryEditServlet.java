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
 * Servlet xử lý chỉnh sửa danh mục tài sản.
 *
 * <p>URL:</p>
 * <ul>
 *   <li>GET  /admin/categories/edit?id={id} – Load dữ liệu danh mục vào form dùng chung.</li>
 *   <li>POST /admin/categories/edit         – Lưu thông tin đã sửa vào DB.</li>
 * </ul>
 *
 * <p>Form JSP dùng chung với Create ({@code assetcategory-form.jsp}).
 * Khi có attribute {@code "category"} → JSP hiểu là edit mode.</p>
 *
 * <p>Quyền truy cập: ASSET_STAFF hoặc ADMIN.</p>
 */
@WebServlet(name = "AssetCategoryEditServlet", urlPatterns = {"/admin/categories/edit"})
public class AssetCategoryEditServlet extends HttpServlet {

    /** DAO thao tác với bảng AssetCategory. */
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    /**
     * Hiển thị form chỉnh sửa danh mục (GET).
     * <p>
     * Màn hình sửa danh mục tài sản:
     * Load dữ liệu danh mục theo {@code id} và forward sang form dùng chung.
     * Quyền: ASSET_STAFF hoặc ADMIN.
     * </p>
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // --- Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        // --- Kiểm tra quyền ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Thiếu id → quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            AssetCategory category = categoryDAO.getCategoryById(id);

            if (category == null) {
                // Không tìm thấy → quay về danh sách (tránh lộ thông tin)
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            req.setAttribute("category", category);
            // Dùng lại JSP form chung cho create/edit
            req.getRequestDispatcher("/views/assetcategory/assetcategory-form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } catch (SQLException e) {
            e.printStackTrace();
            // Không hiển thị chi tiết lỗi SQL ra UI
            req.setAttribute("error", "Không thể tải thông tin danh mục tài sản.");
            req.getRequestDispatcher("/views/assetcategory/assetcategory-list.jsp").forward(req, resp);
        }
    }

    /**
     * Lưu chỉnh sửa danh mục tài sản (POST).
     * <p>
     * POST cập nhật danh mục tài sản:
     * Validate tối thiểu (code/name không rỗng), active lấy từ checkbox.
     * Lưu ý: nếu triển khai production nên bổ sung CSRF token.
     * </p>
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // --- Kiểm tra session ---
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        // --- Kiểm tra quyền ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- Đọc tham số từ form ---
        String idParam     = req.getParameter("id");
        String code        = req.getParameter("categoryCode");
        String name        = req.getParameter("categoryName");
        String activeParam = req.getParameter("active");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        // --- Parse id ---
        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        // --- Xây dựng object từ dữ liệu form ---
        AssetCategory category = new AssetCategory();
        category.setCategoryId(id);
        // Trim để tránh lỗi "trông giống nhau nhưng có khoảng trắng" và đồng nhất dữ liệu lưu DB
        category.setCategoryCode(code != null ? code.trim() : null);
        category.setCategoryName(name != null ? name.trim() : null);

        // Checkbox HTML: thường gửi "on" khi checked, không gửi gì khi bỏ check
        category.setActive("on".equalsIgnoreCase(activeParam) || "true".equalsIgnoreCase(activeParam));

        // --- Validate ---
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            req.setAttribute("error", "Mã danh mục không được để trống.");
        } else if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
            req.setAttribute("error", "Tên danh mục không được để trống.");
        } else {
            try {
                boolean updated = categoryDAO.updateCategory(category);
                if (updated) {
                    req.setAttribute("success", "Cập nhật danh mục tài sản thành công.");
                } else {
                    req.setAttribute("error", "Không thể cập nhật danh mục tài sản.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Trường hợp phổ biến: categoryCode bị unique constraint
                req.setAttribute("error", "Có lỗi xảy ra khi cập nhật danh mục. Kiểm tra lại mã danh mục có bị trùng không.");
            }
        }

        // Forward lại form với dữ liệu hiện tại và thông báo kết quả
        req.setAttribute("category", category);
        req.getRequestDispatcher("/views/assetcategory/assetcategory-form.jsp").forward(req, resp);
    }
}
