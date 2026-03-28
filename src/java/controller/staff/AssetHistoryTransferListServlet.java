package controller.staff;

import dao.AssetDAO;
import model.User;
import model.asset.AssetTransferHistory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AssetHistoryTransferListServlet", urlPatterns = {"/asset-history-transfer/list"})
public class AssetHistoryTransferListServlet extends HttpServlet {

    private final AssetDAO assetDAO = new AssetDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ===== AUTHORIZE =====
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        boolean isAssetStaff = false;
        boolean canApprove   = false;
        if (currentUser != null) {
            List<String> roles = currentUser.getRoles();
            isAssetStaff = roles != null &&
                    (roles.contains("ASSET_STAFF") || roles.contains("BOARD") || roles.contains("ADMIN"));
            canApprove = roles != null &&
                    (roles.contains("BOARD") || roles.contains("ADMIN"));
        }

        // ===== PAGINATION =====
        int page     = 1;
        int pageSize = 10;
        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page <= 0) page = 1;
        } catch (Exception ignored) {}

        try {
            int ps = Integer.parseInt(request.getParameter("pageSize"));
            if (ps == 5 || ps == 10 || ps == 20 || ps == 50) pageSize = ps;
        } catch (Exception ignored) {}

        int offset = (page - 1) * pageSize;

        // ===== FILTER =====
        String keyword  = request.getParameter("keyword");
        String fromDate = request.getParameter("fromDate");
        String toDate   = request.getParameter("toDate");
        if (keyword  == null) keyword  = "";
        if (fromDate == null) fromDate = "";
        if (toDate   == null) toDate   = "";

        // ===== QUERY =====
        try {
            List<AssetTransferHistory> historyList = assetDAO.getAssetTransferHistoryGrouped(
                    keyword, fromDate, toDate, offset, pageSize
            );
            int totalItems = assetDAO.countDistinctAssets(keyword, fromDate, toDate);
            int totalPages = (int) Math.ceil((double) totalItems / pageSize);
            if (totalPages < 1) totalPages = 1;

            request.setAttribute("historyList",  historyList);
            request.setAttribute("currentPage",  page);
            request.setAttribute("pageSize",     pageSize);
            request.setAttribute("totalPages",   totalPages);
            request.setAttribute("totalItems",   totalItems);
            request.setAttribute("keyword",      keyword);
            request.setAttribute("fromDate",     fromDate);
            request.setAttribute("toDate",       toDate);
            request.setAttribute("isAssetStaff", isAssetStaff);
            request.setAttribute("canApprove",   canApprove);

        } catch (Exception e) {
            throw new ServletException(e);
        }

        // ===== FORWARD =====
        request.getRequestDispatcher("/views/asset_transfer/asset-history-transfer-list.jsp")
                .forward(request, response);
    }
}