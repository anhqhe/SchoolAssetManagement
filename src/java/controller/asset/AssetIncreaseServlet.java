package controller.asset;

import dao.asset.AssetDao;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import model.User;
import model.asset.AssetIncreaseRecord;
import model.asset.AssetIncreaseItem;

@WebServlet(name = "AssetIncreaseServlet", urlPatterns = {"/asset-increase"})
public class AssetIncreaseServlet extends HttpServlet {

    private final AssetDao assetDao = new AssetDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra authentication
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/views/auth/login.jsp");
            return;
        }

        // Kiểm tra authorization: chỉ ADMIN + ASSET_STAFF
        List<String> roles = currentUser.getRoles();
        if (roles == null || (!roles.contains("ADMIN") && !roles.contains("ASSET_STAFF"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập.");
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action.trim()) {
                case "detail":
                    showDetail(request, response);
                    break;
                default:
                    showList(request, response);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String keyword = request.getParameter("keyword");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        // Phân trang
        int page = 1;
        int pageSize = 10;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageParam.trim());
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int totalRecords = assetDao.countIncreaseRecords(keyword, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page > totalPages) page = totalPages;
        int offset = (page - 1) * pageSize;

        List<AssetIncreaseRecord> records = assetDao.getIncreaseRecords(
                keyword, fromDate, toDate, offset, pageSize);

        request.setAttribute("records", records);
        request.setAttribute("keyword", keyword);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.getRequestDispatcher("/views/asset/asset-increase-list.jsp")
                .forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/asset-increase");
            return;
        }

        try {
            long increaseId = Long.parseLong(idStr.trim());
            AssetIncreaseRecord record = assetDao.getIncreaseRecordById(increaseId);
            if (record == null) {
                response.sendRedirect(request.getContextPath() + "/asset-increase");
                return;
            }

            List<AssetIncreaseItem> items = assetDao.getIncreaseItems(increaseId);

            request.setAttribute("record", record);
            request.setAttribute("items", items);

            request.getRequestDispatcher("/views/asset/asset-increase-detail.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/asset-increase");
        }
    }
}
