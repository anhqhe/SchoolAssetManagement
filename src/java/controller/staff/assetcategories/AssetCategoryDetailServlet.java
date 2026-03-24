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

@WebServlet(name = "AssetCategoryDetailServlet", urlPatterns = {"/admin/categories/detail"})
public class AssetCategoryDetailServlet extends HttpServlet {

    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();
    private final AssetDAO assetDAO = new AssetDAO();

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

        String idParam = req.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
            return;
        }

        try {
            long categoryId = Long.parseLong(idParam.trim());
            AssetCategory category = categoryDAO.getCategoryById(categoryId);

            if (category == null) {
                req.setAttribute("error", "Không tìm thấy danh mục tài sản.");
            } else {
                req.setAttribute("category", category);
                req.setAttribute("assets", assetDAO.getAssetsByCategoryId(categoryId));
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "ID danh mục không hợp lệ.");
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "Không thể tải chi tiết danh mục tài sản. Vui lòng thử lại sau.");
        }

        req.getRequestDispatcher("/views/assetcategory/assetcategory-detail.jsp").forward(req, resp);
    }
}
