package controller.admin;

import dao.AssetDAO;
import dao.TransferDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.NotificationDAO;
import dto.AssetRequestDTO;
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
import model.Transfer;
import model.User;
import model.allocation.Notification;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final AssetDAO assetDAO = new AssetDAO();
    private final AssetRequestDAO assetRequestDAO = new AssetRequestDAO();
    private final TransferDAO transferDAO = new TransferDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

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
            // ---------- Thống kê tài sản ----------
            List<Asset> assets = assetDAO.getAllAssets();

            int totalAssets = 0;
            int inStockAssets = 0;
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
                if ("IN_STOCK".equals(status)) {
                    inStockAssets++;
                } else if ("IN_USE".equals(status)) {
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
            request.setAttribute("inStockAssets", inStockAssets);
            request.setAttribute("inUseAssets", inUseAssets);
            request.setAttribute("maintenanceAssets", maintenanceAssets);
            request.setAttribute("damagedAssets", damagedAssets);
            request.setAttribute("recentAssets", recentAssets);

            // ---------- Thống kê phiếu yêu cầu ----------
            List<AssetRequestDTO> allRequests = assetRequestDAO.getRequestsAdvanced(null, null, "CreatedAt DESC");

            int totalRequests = allRequests.size();
            int pendingRequests = 0;
            int waitingBoardRequests = 0;
            int approvedRequests = 0;
            int completedRequests = 0;
            int outOfStockRequests = 0;

            List<AssetRequestDTO> recentRequests = new ArrayList<>();

            for (AssetRequestDTO req : allRequests) {
                String status = req.getStatus();
                if ("PENDING".equals(status)) {
                    pendingRequests++;
                } else if ("WAITING_BOARD".equals(status)) {
                    waitingBoardRequests++;
                } else if ("APPROVED_BY_BOARD".equals(status)) {
                    approvedRequests++;
                } else if ("COMPLETED".equals(status)) {
                    completedRequests++;
                } else if ("OUT_OF_STOCK".equals(status)) {
                    outOfStockRequests++;
                }

                if (recentRequests.size() < 5) {
                    recentRequests.add(req);
                }
            }

            request.setAttribute("totalRequests", totalRequests);
            request.setAttribute("pendingRequests", pendingRequests);
            request.setAttribute("waitingBoardRequests", waitingBoardRequests);
            request.setAttribute("approvedRequests", approvedRequests);
            request.setAttribute("completedRequests", completedRequests);
            request.setAttribute("outOfStockRequests", outOfStockRequests);
            request.setAttribute("recentRequests", recentRequests);

            // ---------- Thống kê điều chuyển tài sản ----------
            List<Transfer> transfers = transferDAO.getTransfers(null, null);

            int totalTransfers = transfers.size();
            int pendingTransfers = 0;
            int approvedTransfers = 0;
            int completedTransfers = 0;
            int rejectedTransfers = 0;

            List<Transfer> recentTransfers = new ArrayList<>();

            for (Transfer transfer : transfers) {
                String status = transfer.getStatus();
                if (status != null) {
                    switch (status.toUpperCase()) {
                        case "PENDING":
                            pendingTransfers++;
                            break;
                        case "APPROVED":
                            approvedTransfers++;
                            break;
                        case "COMPLETED":
                            completedTransfers++;
                            break;
                        case "REJECTED":
                            rejectedTransfers++;
                            break;
                        default:
                            break;
                    }
                }

                if (recentTransfers.size() < 5) {
                    recentTransfers.add(transfer);
                }
            }

            request.setAttribute("totalTransfers", totalTransfers);
            request.setAttribute("pendingTransfers", pendingTransfers);
            request.setAttribute("approvedTransfers", approvedTransfers);
            request.setAttribute("completedTransfers", completedTransfers);
            request.setAttribute("rejectedTransfers", rejectedTransfers);
            request.setAttribute("recentTransfers", recentTransfers);

            // ---------- Thông báo ----------
            List<Notification> latestNotifications = notificationDAO.getTop10ByUserId(currentUser.getUserId());
            List<Notification> unreadNotifications = notificationDAO.getUnreadByUserId(currentUser.getUserId());

            int unreadNotificationCount = unreadNotifications != null ? unreadNotifications.size() : 0;

            request.setAttribute("unreadNotificationCount", unreadNotificationCount);
            request.setAttribute("recentNotifications", latestNotifications);

            request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải dữ liệu dashboard: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
        }
    }
}

