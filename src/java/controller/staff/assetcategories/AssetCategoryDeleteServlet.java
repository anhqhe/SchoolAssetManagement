package controller.staff.assetcategories;

import dao.AssetCategoryDAO;
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

@WebServlet(name = "AssetCategoryDeleteServlet", urlPatterns = {"/admin/categories/delete"})
public class AssetCategoryDeleteServlet extends HttpServlet {

    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();

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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long id = Long.parseLong(idParam);
            boolean deleted = categoryDAO.deleteCategory(id);
            if (deleted) {
                req.setAttribute("success", "Đã xóa danh mục tài sản.");
            } else {
                req.setAttribute("error", "Không thể xóa danh mục tài sản.");
            }
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi xảy ra khi xóa danh mục. Có thể đang được sử dụng ở tài sản khác.");
        }

        try {
            req.setAttribute("categories", categoryDAO.getAllCategories());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/views/allocation/staff/assetcategory-list.jsp").forward(req, resp);
    }
}
