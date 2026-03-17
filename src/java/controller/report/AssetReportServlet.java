/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.report;

import dao.asset.AssetReportDao;
import model.report.InventorySummary;
import model.report.InventoryDetailRow;
import model.report.UsageSummary;
import model.report.UsageDetailRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.report.InventoryDetailRow;
import model.report.InventorySummary;
import model.report.UsageDetailRow;
import model.report.UsageSummary;

@WebServlet(name = "AssetReportServlet", urlPatterns = {"/asset-report"})
public class AssetReportServlet extends HttpServlet {

    private AssetReportDao reportDao = new AssetReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null || type.isEmpty()) {
            type = "inventory";
        }

        try {
            switch (type) {
                case "inventory":
                    showInventorySummary(request, response);
                    break;
                case "inventoryDetail":
                    showInventoryDetail(request, response);
                    break;
                case "usage":
                    showUsageSummary(request, response);
                    break;
                case "usageDetail":
                    showUsageDetail(request, response);
                    break;
                default:
                    showInventorySummary(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showInventorySummary(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<InventorySummary> data = reportDao.getInventoryByCategoryAndStatus();
        request.setAttribute("inventoryData", data);
        request.getRequestDispatcher("/views/report/asset-inventory.jsp").forward(request, response);
    }

    private void showInventoryDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String categoryIdStr = request.getParameter("categoryId");
        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/asset-report?type=inventory");
            return;
        }
        long categoryId = Long.parseLong(categoryIdStr.trim());

        List<InventoryDetailRow> data = reportDao.getInventoryDetailByCategory(categoryId);
        request.setAttribute("detailData", data);
        request.setAttribute("categoryId", categoryId);
        request.getRequestDispatcher("/views/report/asset-inventory-detail.jsp").forward(request, response);
    }

    private void showUsageSummary(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<UsageSummary> data = reportDao.getCurrentUsageByRoomAndCategory();
        request.setAttribute("usageData", data);
        request.getRequestDispatcher("/views/report/asset-usage.jsp").forward(request, response);
    }

    private void showUsageDetail(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String roomIdStr = request.getParameter("roomId");
        String categoryIdStr = request.getParameter("categoryId");
        if (roomIdStr == null || categoryIdStr == null
                || roomIdStr.trim().isEmpty() || categoryIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/asset-report?type=usage");
            return;
        }
        long roomId = Long.parseLong(roomIdStr.trim());
        long categoryId = Long.parseLong(categoryIdStr.trim());

        List<UsageDetailRow> data = reportDao.getUsageDetailByRoomAndCategory(roomId, categoryId);
        request.setAttribute("detailData", data);
        request.setAttribute("roomId", roomId);
        request.setAttribute("categoryId", categoryId);
        request.getRequestDispatcher("/views/report/asset-usage-detail.jsp").forward(request, response);
    }
}