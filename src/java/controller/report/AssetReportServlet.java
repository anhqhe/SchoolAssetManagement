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

@WebServlet(name = "AssetReportServlet", urlPatterns = {"/asset-report"})
public class AssetReportServlet extends HttpServlet {

    private AssetReportDao reportDao = new AssetReportDao();

    // Số dòng mỗi trang - có thể thay đổi tùy ý
    private static final int PAGE_SIZE = 10;

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

    // ===================================================================
    // INVENTORY SUMMARY - ĐÃ THÊM: filter, search, phân trang, dữ liệu chart
    // ===================================================================
    private void showInventorySummary(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        // --- Bước 1: Đọc tham số từ request ---
        String categoryFilter = request.getParameter("categoryFilter"); // từ dropdown
        String search = request.getParameter("search");                 // từ ô tìm kiếm
        String pageStr = request.getParameter("page");                  // số trang

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr.trim());
            } catch (NumberFormatException e) {
                page = 1;
            }
            if (page < 1) page = 1;
        }

        // --- Bước 2: Lấy danh sách categories cho dropdown filter ---
        List<String[]> categories = reportDao.getAllCategories();
        request.setAttribute("categories", categories);

        // --- Bước 3: Lấy dữ liệu bảng (có filter + phân trang) ---
        List<InventorySummary> data = reportDao.getInventoryByCategoryAndStatus(
                categoryFilter, search, page, PAGE_SIZE);
        request.setAttribute("inventoryData", data);

        // --- Bước 4: Tính phân trang ---
        int totalRows = reportDao.countInventoryByCategoryAndStatus(categoryFilter, search);
        int totalPages = (int) Math.ceil((double) totalRows / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        // --- Bước 5: Truyền lại giá trị filter/search để giữ trên form ---
        request.setAttribute("categoryFilter", categoryFilter != null ? categoryFilter : "");
        request.setAttribute("search", search != null ? search : "");

        // --- Bước 6: Lấy TOÀN BỘ dữ liệu cho biểu đồ (không phân trang) ---
        List<InventorySummary> chartData = reportDao.getAllInventoryForChart();
        request.setAttribute("chartData", chartData);

        request.getRequestDispatcher("/views/report/asset-inventory.jsp").forward(request, response);
    }

    // ===================================================================
    // INVENTORY DETAIL - KHÔNG ĐỔI
    // ===================================================================
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

    // ===================================================================
    // USAGE SUMMARY - ĐÃ THÊM: filter theo phòng, search, phân trang, dữ liệu chart
    // ===================================================================
    private void showUsageSummary(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String roomFilter = request.getParameter("roomFilter");
        String search = request.getParameter("search");
        String pageStr = request.getParameter("page");

        int page = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr.trim());
            } catch (NumberFormatException e) {
                page = 1;
            }
            if (page < 1) page = 1;
        }

        // Lấy danh sách phòng cho dropdown
        List<String[]> rooms = reportDao.getAllRooms();
        request.setAttribute("rooms", rooms);

        // Dữ liệu bảng
        List<UsageSummary> data = reportDao.getCurrentUsageByRoomAndCategory(
                roomFilter, search, page, PAGE_SIZE);
        request.setAttribute("usageData", data);

        // Phân trang
        int totalRows = reportDao.countUsageByRoomAndCategory(roomFilter, search);
        int totalPages = (int) Math.ceil((double) totalRows / PAGE_SIZE);
        if (totalPages < 1) totalPages = 1;

        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("roomFilter", roomFilter != null ? roomFilter : "");
        request.setAttribute("search", search != null ? search : "");

        // Dữ liệu biểu đồ (toàn bộ)
        List<UsageSummary> chartData = reportDao.getAllUsageForChart();
        request.setAttribute("chartData", chartData);

        request.getRequestDispatcher("/views/report/asset-usage.jsp").forward(request, response);
    }

    // ===================================================================
    // USAGE DETAIL - KHÔNG ĐỔI
    // ===================================================================
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
