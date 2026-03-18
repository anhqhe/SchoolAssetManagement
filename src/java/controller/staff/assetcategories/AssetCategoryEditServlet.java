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

@WebServlet(name = "AssetCategoryEditServlet", urlPatterns = {"/admin/categories/edit"})
public class AssetCategoryEditServlet extends HttpServlet {

    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
         * Màn hình sửa danh mục tài sản:
         * - GET: load dữ liệu danh mục theo `id` và forward sang form dùng chung
         * - Quyền: ASSET_STAFF hoặc ADMIN
         */
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !(roles.contains("ASSET_STAFF") || roles.contains("ADMIN"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Thiếu id -> quay về danh sách
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            AssetCategory category = categoryDAO.getCategoryById(id);

            if (category == null) {
                // Không tìm thấy -> quay về danh sách (tránh lộ thông tin)
                resp.sendRedirect(req.getContextPath() + "/admin/categories");
                return;
            }

            req.setAttribute("category", category);

            // Dùng lại JSP form chung cho create/edit
            req.getRequestDispatcher("/views/allocation/staff/assetcategory-form.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } catch (SQLException e) {
            e.printStackTrace();
            // Không hiển thị chi tiết lỗi SQL ra UI
            req.setAttribute("error", "Không thể tải thông tin danh mục tài sản.");
            req.getRequestDispatcher("/views/allocation/staff/assetcategory-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
         * POST cập nhật danh mục tài sản:
         * - Validate tối thiểu (code/name không rỗng)
         * - active lấy từ checkbox
         *
         * Lưu ý: nếu triển khai production nên bổ sung CSRF token cho các form thay đổi dữ liệu.
         */
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !(roles.contains("ASSET_STAFF") || roles.contains("ADMIN"))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String idParam = req.getParameter("id");
        String code = req.getParameter("categoryCode");
        String name = req.getParameter("categoryName");
        String activeParam = req.getParameter("active");

        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        long id;
        try {
            id = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        AssetCategory category = new AssetCategory();
        category.setCategoryId(id);
        // Trim để tránh lỗi "trông giống nhau nhưng có khoảng trắng" và đồng nhất dữ liệu lưu DB
        category.setCategoryCode(code != null ? code.trim() : null);
        category.setCategoryName(name != null ? name.trim() : null);

        // Edit màn hình này hiện không cho đổi danh mục cha
        category.setParentCategoryId(null);

        // Checkbox HTML: thường gửi "on" khi checked
        category.setActive("on".equalsIgnoreCase(activeParam) || "true".equalsIgnoreCase(activeParam));

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

        req.setAttribute("category", category);
        req.getRequestDispatcher("/views/allocation/staff/assetcategory-form.jsp").forward(req, resp);
    }
}
