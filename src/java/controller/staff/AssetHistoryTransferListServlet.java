
package controller.staff;

import dao.AssetDAO;
import dao.TransferDAO;
import model.Asset;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import model.Room;
import model.Transfer;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@WebServlet(name = "AssetHistoryTransferListServlet", urlPatterns = {"/asset-history-transfer/list"})
public class AssetHistoryTransferListServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
User currentUser = (User) request.getSession().getAttribute("currentUser");

boolean isAssetStaff = false;
boolean canApprove = false;

if (currentUser != null) {
    List<String> roles = currentUser.getRoles();

    isAssetStaff = roles != null &&
            (roles.contains("ASSET_STAFF") || roles.contains("BOARD") || roles.contains("ADMIN"));

    canApprove = roles != null &&
            (roles.contains("BOARD") || roles.contains("ADMIN"));
}

// ===== PAGINATION =====
int page = 1;
int pageSize = 10;

// page
try {
    page = Integer.parseInt(request.getParameter("page"));
    if (page <= 0) page = 1;
} catch (Exception e) {
    page = 1;
}

// pageSize (🔥 FIX QUAN TRỌNG)
try {
    pageSize = Integer.parseInt(request.getParameter("pageSize"));
    if (pageSize != 10 && pageSize != 50 && pageSize != 100) {
        pageSize = 10;
    }
} catch (Exception e) {
    pageSize = 10;
}

int offset = (page - 1) * pageSize;

// ===== FILTER =====
String keyword = request.getParameter("keyword");
String fromDate = request.getParameter("fromDate");
String toDate = request.getParameter("toDate");

// null-safe (tránh lỗi query)
if (keyword == null) keyword = "";
if (fromDate == null) fromDate = "";
if (toDate == null) toDate = "";

try {

    // ===== QUERY =====
    List<Transfer> historyList = assetDAO.getAssetTransferHistoryPaging(
            keyword, fromDate, toDate, offset, pageSize
    );

    // ===== GROUP DATA =====
    Map<String, List<Transfer>> groupedHistory =
            historyList.stream()
                    .collect(Collectors.groupingBy(
                            t -> t.getAssetNames() != null ? t.getAssetNames() : "Unknown",
                            LinkedHashMap::new, // giữ thứ tự
                            Collectors.toList()
                    ));

    // ===== COUNT =====
    int totalItems = assetDAO.countAssetTransferHistory(
            keyword, fromDate, toDate
    );

    int totalPages = (int) Math.ceil((double) totalItems / pageSize);

    // ===== SET ATTRIBUTE =====
    request.setAttribute("groupedHistory", groupedHistory);

    request.setAttribute("currentPage", page);
    request.setAttribute("pageSize", pageSize);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("totalItems", totalItems);

    request.setAttribute("keyword", keyword);
    request.setAttribute("fromDate", fromDate);
    request.setAttribute("toDate", toDate);

    request.setAttribute("isAssetStaff", isAssetStaff);
    request.setAttribute("canApprove", canApprove);

} catch (Exception e) {
    throw new ServletException(e);
}

// ===== FORWARD =====
request.getRequestDispatcher("/views/asset_transfer/asset-history-transfer-list.jsp")
        .forward(request, response);

}
}
