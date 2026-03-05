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

@WebServlet(name = "AssetCategoryListServlet", urlPatterns = {"/admin/categories"})
public class AssetCategoryListServlet extends HttpServlet {

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
            List<AssetCategory> categories = categoryDAO.getAllCategories();
            req.setAttribute("categories", categories);
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải danh sách danh mục tài sản. Vui lòng thử lại sau.");
        }

        req.getRequestDispatcher("/views/allocation/staff/assetcategory-list.jsp").forward(req, resp);
    }
}
