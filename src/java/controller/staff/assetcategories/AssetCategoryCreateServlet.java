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
 * Servlet xử lý tạo mới danh mục tài sản.
 *
 * <p>
 * URL:
 * </p>
 * <ul>
 * <li>GET /admin/categories/create – Hiển thị form tạo mới (trống).</li>
 * <li>POST /admin/categories/create – Nhận dữ liệu form, validate và lưu vào
 * DB.</li>
 * </ul>
 *
 * <p>
 * Form dùng chung với màn hình Edit ({@code assetcategory-form.jsp}).
 * Khi GET, không đặt attribute {@code "category"} → JSP biết đây là form tạo
 * mới.
 * Khi POST lỗi validation, đặt lại attribute {@code "category"} để giữ dữ liệu
 * người dùng đã nhập.
 * </p>
 *
 * <p>
 * Quyền truy cập: ASSET_STAFF.
 * </p>
 */
@WebServlet(name = "AssetCategoryCreateServlet", urlPatterns = { "/admin/categories/create" })
public class AssetCategoryCreateServlet extends HttpServlet {

    /** DAO thao tác với bảng AssetCategory. */
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    /**
     * Hiển thị form tạo danh mục mới (GET).
     * <ol>
     * <li>Kiểm tra session và quyền.</li>
     * <li>Forward thẳng sang {@code assetcategory-form.jsp} (không cần load dữ
     * liệu).</li>
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

        // --- 2. Kiểm tra quyền: Chỉ ASSET_STAFF mới được phép tạo danh mục ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Form tạo mới: không đặt attribute "category" → JSP hiểu là create mode
        req.getRequestDispatcher("/views/assetcategory/assetcategory-form.jsp").forward(req, resp);
    }

    /**
     * Xử lý tạo danh mục tài sản mới (POST).
     * <ol>
     * <li>Kiểm tra session và quyền.</li>
     * <li>Đọc và trim các tham số: {@code categoryCode}, {@code categoryName},
     * {@code active}.</li>
     * <li>Validate: cả code và name đều không được rỗng.</li>
     * <li>Gọi DAO tạo mới; nếu thành công, xoá object {@code category} để form
     * reset về trống.</li>
     * <li>Forward lại form với thông báo thành công hoặc lỗi.</li>
     * </ol>
     *
     * <p>
     * Form parameters dự kiến:
     * </p>
     * <ul>
     * <li>{@code categoryCode} – Mã danh mục (bắt buộc, unique)</li>
     * <li>{@code categoryName} – Tên danh mục (bắt buộc)</li>
     * <li>{@code active} – Trạng thái kích hoạt (checkbox: "on" hoặc "true")</li>
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

        // --- 2. Kiểm tra quyền: Chỉ ASSET_STAFF được phép tạo ---
        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // --- 3. Đọc tham số từ form ---
        String code = req.getParameter("categoryCode");
        String name = req.getParameter("categoryName");
        String activeParam = req.getParameter("active");

        // --- 4. Tạo object và trim input ---
        AssetCategory category = new AssetCategory();
        category.setCategoryCode(code != null ? code.trim() : null);
        category.setCategoryName(name != null ? name.trim() : null);

        // Checkbox HTML: gửi "on" khi được check; form có thể gửi "true" từ AJAX
        category.setActive("on".equalsIgnoreCase(activeParam) || "true".equalsIgnoreCase(activeParam));

        // --- 5. Validate tối thiểu ---
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            req.setAttribute("error", "Mã danh mục không được để trống.");
        } else if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
            req.setAttribute("error", "Tên danh mục không được để trống.");
        } else {
            // --- 6. Lưu vào DB ---
            try {
                boolean created = categoryDAO.createCategory(category);
                if (created) {
                    req.setAttribute("success", "Tạo danh mục tài sản thành công.");
                    // Xóa object để form reset về trống (không giữ dữ liệu vừa nhập)
                    category = null;
                } else {
                    req.setAttribute("error", "Không thể tạo danh mục tài sản.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Lỗi SQL phổ biến nhất ở đây: categoryCode vi phạm unique constraint
                req.setAttribute("error",
                        "Có lỗi xảy ra khi tạo danh mục. Kiểm tra lại mã danh mục có bị trùng không.");
            }
        }

        // Nếu có lỗi, giữ lại dữ liệu người dùng đã nhập để họ không phải nhập lại
        if (category != null) {
            req.setAttribute("category", category);
        }

        // Forward lại form (dùng chung) với thông báo kết quả
        req.getRequestDispatcher("/views/assetcategory/assetcategory-form.jsp").forward(req, resp);
    }
}
