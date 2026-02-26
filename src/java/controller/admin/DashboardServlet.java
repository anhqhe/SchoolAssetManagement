package controller.admin;

import dao.AssetDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Asset;
import model.User;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final AssetDAO assetDAO = new AssetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            List<Asset> assets = assetDAO.getAllAssets();

            int totalAssets = 0;
            int inUseAssets = 0;
            int maintenanceAssets = 0;
            int damagedAssets = 0;

            List<Asset> recentAssets = new ArrayList<>();

            for (Asset asset : assets) {
                if (!asset.isActive()) {
                    continue;
                }

                totalAssets++;

                String status = asset.getStatus();
                if ("IN_USE".equals(status)) {
                    inUseAssets++;
                } else if ("MAINTENANCE".equals(status)) {
                    maintenanceAssets++;
                } else if ("DAMAGED".equals(status)) {
                    damagedAssets++;
                }

                if (recentAssets.size() < 5) {
                    recentAssets.add(asset);
                }
            }

            request.setAttribute("totalAssets", totalAssets);
            request.setAttribute("inUseAssets", inUseAssets);
            request.setAttribute("maintenanceAssets", maintenanceAssets);
            request.setAttribute("damagedAssets", damagedAssets);
            request.setAttribute("recentAssets", recentAssets);

            request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải dữ liệu dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
        }
    }
}

