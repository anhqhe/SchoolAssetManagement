package controller.staff.categories;

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

@WebServlet(name = "CategoryCreateServlet", urlPatterns = {"/admin/categories/create"})
public class CategoryCreateServlet extends HttpServlet {

    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        try {
            List<AssetCategory> allCategories = categoryDAO.getAllCategories();
            req.setAttribute("allCategories", allCategories);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải danh sách danh mục cha.");
        }

        req.getRequestDispatcher("/views/admin/category-form.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        String code = req.getParameter("categoryCode");
        String name = req.getParameter("categoryName");
        String parentIdParam = req.getParameter("parentCategoryId");
        String activeParam = req.getParameter("active");

        AssetCategory category = new AssetCategory();
        category.setCategoryCode(code != null ? code.trim() : null);
        category.setCategoryName(name != null ? name.trim() : null);

        if (parentIdParam != null && !parentIdParam.trim().isEmpty()) {
            try {
                category.setParentCategoryId(Long.parseLong(parentIdParam));
            } catch (NumberFormatException e) {
                category.setParentCategoryId(null);
            }
        }

        category.setActive("on".equalsIgnoreCase(activeParam) || "true".equalsIgnoreCase(activeParam));

        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            req.setAttribute("error", "Mã danh mục không được để trống.");
        } else if (category.getCategoryName() == null || category.getCategoryName().isEmpty()) {
            req.setAttribute("error", "Tên danh mục không được để trống.");
        } else {
            try {
                boolean created = categoryDAO.createCategory(category);
                if (created) {
                    req.setAttribute("success", "Tạo danh mục tài sản thành công.");
                    category = null;
                } else {
                    req.setAttribute("error", "Không thể tạo danh mục tài sản.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                req.setAttribute("error", "Có lỗi xảy ra khi tạo danh mục. Kiểm tra lại mã danh mục có bị trùng không.");
            }
        }

        try {
            List<AssetCategory> allCategories = categoryDAO.getAllCategories();
            req.setAttribute("allCategories", allCategories);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (category != null) {
            req.setAttribute("category", category);
        }

        req.getRequestDispatcher("/views/admin/category-form.jsp").forward(req, resp);
    }
}

