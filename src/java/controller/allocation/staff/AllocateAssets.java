/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.staff;

import controller.allocation.notification.NotificationEndPoint;
import dao.allocation.AllocationDAO;
import dao.allocation.AllocationItemDAO;
import dao.allocation.AssetDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.AssetStatusHistoryDAO;
import dto.AssetDTO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import util.DBUtil;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;
import model.allocation.AssetAllocation;

/**
 *
 * @author Leo
 */
@WebServlet(name = "AllocateAsset", urlPatterns = {"/staff/allocate-assets"})
public class AllocateAssets extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO reqItemDAO = new AssetRequestItemDAO();
    private AssetDAO assetDAO = new AssetDAO();
    private AllocationDAO allocationDAO = new AllocationDAO();
    private AllocationItemDAO allocItemDAO = new AllocationItemDAO();
    private AssetStatusHistoryDAO statusHistoryDAO = new AssetStatusHistoryDAO();

    private static final Logger LOGGER = Logger.getLogger(AllocateAssets.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        try {
            long requestId = validateId(request.getParameter("requestId"),
                    "ID yêu cầu");

            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu không tồn tại.");
                response.sendRedirect("request-list");
                return;
            }

            //Only request approved_by_board, out_of_stock can allocate asset
            if (!(req.getStatus().equals("APPROVED_BY_BOARD")
                    || req.getStatus().equals("OUT_OF_STOCK")
                    || req.getStatus().equals("INCOMPLETE"))) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Không thể cấp phát yêu cầu này.");
                response.sendRedirect("request-list");
                return;
            }

            List<AssetRequestItemDTO> neededItems = reqItemDAO.findByRequestId(requestId);
            for (AssetRequestItemDTO item : neededItems) {

                int allocated = allocItemDAO.countAllocatedByCategory(
                        requestId,
                        item.getCategoryId()
                );

                int remaining = item.getQuantity() - allocated;
                if (remaining < 0) {
                    remaining = 0;
                }

                item.setAllocatedQuantity(allocated);
                item.setRemainingQuantity(remaining);
            }

            //Get available assets
            List<AssetDTO> availableAssets = assetDAO.findAvailableAssets();
            List<AssetDTO> allocatedAssets = allocationDAO.getAllocatedAssetsByRequestId(requestId);
            Map<Long, List<AssetDTO>> allocatedAssetsByCategory = new LinkedHashMap<>();
            for (AssetDTO asset : allocatedAssets) {
                allocatedAssetsByCategory
                        .computeIfAbsent(asset.getCategoryId(), k -> new ArrayList<>())
                        .add(asset);
            }

            request.setAttribute("req", req);
            request.setAttribute("neededItems", neededItems);
            request.setAttribute("availableAssets", availableAssets);
            request.setAttribute("allocatedAssetsByCategory", allocatedAssetsByCategory);

            request.getRequestDispatcher("/views/allocation/staff/allocate-assets.jsp").forward(request, response);

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error: {0}", e.getMessage());

            session.setAttribute("type", "error");
            session.setAttribute("message", e.getMessage());
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading allocate assets page", e);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Không thể tải yêu cầu cấp phát.");
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User staff = (User) session.getAttribute("currentUser");

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        long requestId = 0;
        try {
            requestId = validateId(request.getParameter("requestId"),
                    "ID yêu cầu");
            AssetRequestDTO reqDTO = validateAllocationRequest(requestId);

            String action = request.getParameter("action");

            //out of stock
            if ("notify_out_of_stock".equals(action)) {

                if (reqDTO != null) {
                    NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                            "Kho hàng không đủ tài sản",
                            "Kho hàng không đủ tài sản cấp phát cho phiếu " + reqDTO.getRequestCode() + ".",
                            "ASSET_REQUEST",
                            requestId);
                    try {
                        requestDAO.updateStatus(requestId, "OUT_OF_STOCK");
                    } catch (SQLException ex) {
                        LOGGER.log(Level.SEVERE, "Error updating request status", ex);
                        throw new RuntimeException("Không thể cập nhật trạng thái yêu cầu.");
                    }
                }
                session.setAttribute("type", "success");
                session.setAttribute("message", "Đã gửi thông báo hết tài sản cho giáo viên.");
                response.sendRedirect("request-list");
                return;
            }

            //Allocate
            String[] selectedAssetIds = request.getParameterValues("selectedAssetIds");
            if (selectedAssetIds == null || selectedAssetIds.length == 0) {
                throw new IllegalArgumentException(
                        "Vui lòng chọn ít nhất một tài sản.");
            }

            List<Long> assetIds = new ArrayList<>();
            for (String id : selectedAssetIds) {
                assetIds.add(validateId(id, "Tài sản"));
            }
            validateSelectedAssets(requestId, assetIds);
            String note = request.getParameter("note");

            // Save data to database
            // Insert AssetAllocations, AssetAllocationItems & Update Status Assets + Request
            boolean success = processAllocation(requestId, staff.getUserId(), note, assetIds);

            if (!success) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Có lỗi xảy ra khi cấp phát.");
                response.sendRedirect("allocate-assets?requestId=" + requestId);
                return;
            }

            //Save to database sucessfully
            boolean isComplete = checkRequestCompleted(requestId);
            String message, title;
            if (isComplete) {
                title = "Yêu cầu cấp phát hoàn thành";
                message = "Tài sản của phiếu " + reqDTO.getRequestCode() + " đã được chuẩn bị xong. Hãy đến nhận!";
            } else {
                title = "Yêu cầu cấp phát một phần";
                message = "Yêu cầu " + reqDTO.getRequestCode() + " đã được cấp phát một phần.";
            }
            //Send notification to teacher
            NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                    title,
                    message,
                    "ASSET_REQUEST",
                    requestId);

            session.setAttribute("type", "success");
            session.setAttribute("message", "Cấp phát tài sản thành công!");
            response.sendRedirect("request-list");

        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING, "Validation error in allocation", e);

            session.setAttribute("type", "error");
            session.setAttribute("message", e.getMessage());

            if (requestId > 0) {
                response.sendRedirect("allocate-assets?requestId=" + requestId);
            } else {
                response.sendRedirect(request.getContextPath() + "/staff/request-list");
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error in AllocateAssets", e);

            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
        }
    }

    private long validateId(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " không hợp lệ.");
        }
        try {
            long id = Long.parseLong(value);
            if (id <= 0) {
                throw new IllegalArgumentException(field + " phải lớn hơn 0.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(field + " phải là số.");
        }
    }

    private AssetRequestDTO validateAllocationRequest(long requestId)
            throws IllegalArgumentException, SQLException {

        AssetRequestDTO req = requestDAO.findById(requestId);

        if (req == null) {
            throw new IllegalArgumentException("Yêu cầu không tồn tại.");
        }

        if (!("APPROVED_BY_BOARD".equals(req.getStatus())
                || "OUT_OF_STOCK".equals(req.getStatus())
                || "INCOMPLETE".equals(req.getStatus()))) {
            throw new IllegalArgumentException("Yêu cầu không thể cấp phát.");
        }

        return req;
    }

    private void validateSelectedAssets(long requestId, List<Long> assetIds)
            throws Exception {

        List<AssetRequestItemDTO> neededItems = reqItemDAO.findByRequestId(requestId);
        if (neededItems == null || neededItems.isEmpty()) {
            throw new IllegalArgumentException("Yêu cầu không có danh sách loại tài sản hợp lệ.");
        }

        List<AssetDTO> selectedAssets = new ArrayList<>();
        Set<Long> uniqueAssetIds = new HashSet<>();

        for (Long assetId : assetIds) {
            if (!uniqueAssetIds.add(assetId)) {
                throw new IllegalArgumentException("Danh sách tài sản có dữ liệu trùng lặp.");
            }

            AssetDTO asset = assetDAO.findById(assetId);

            if (asset == null) {
                throw new IllegalArgumentException("Một hoặc nhiều tài sản không tồn tại.");
            }

            if (!"IN_STOCK".equals(asset.getStatus())) {
                throw new IllegalArgumentException("Tài sản không còn khả dụng.");
            }
            selectedAssets.add(asset);
        }

        // Gom required, allocated, remaining theo category
        Map<Long, Integer> requiredByCategory = new HashMap<>();
        Map<Long, Integer> allocatedByCategory = new HashMap<>();
        Map<Long, Integer> remainingByCategory = new HashMap<>();
        for (AssetRequestItemDTO item : neededItems) {
            requiredByCategory.put(item.getCategoryId(), item.getQuantity());
            int allocated = allocItemDAO.countAllocatedByCategory(requestId, item.getCategoryId());
            allocatedByCategory.put(item.getCategoryId(), allocated);
            int remaining = item.getQuantity() - allocated;
            if (remaining < 0) {
                remaining = 0;
            }
            remainingByCategory.put(item.getCategoryId(), remaining);
        }

        // Gom selected theo category
        Map<Long, Integer> selectedByCategory = new HashMap<>();
        for (AssetDTO asset : selectedAssets) {
            selectedByCategory.put(asset.getCategoryId(),
                    selectedByCategory.getOrDefault(asset.getCategoryId(), 0) + 1);
        }

        // Category selected khong co trong yeu cau
        for (Long categoryId : selectedByCategory.keySet()) {
            if (!requiredByCategory.containsKey(categoryId)) {
                throw new IllegalArgumentException(
                        "Bạn đã chọn tài sản từ loại không được yêu cầu.");
            }
        }

        // Khong duoc cap qua phan con thieu theo category
        for (Long categoryId : selectedByCategory.keySet()) {
            int selected = selectedByCategory.getOrDefault(categoryId, 0);
            int remaining = remainingByCategory.getOrDefault(categoryId, 0);
            if (selected > remaining) {
                throw new IllegalArgumentException(
                        "Số lượng tài sản được chọn vượt quá số lượng còn thiếu.");
            }
        }

        // Bao ve bo sung: allocated + selected khong vuot required
        for (Long categoryId : requiredByCategory.keySet()) {
            int required = requiredByCategory.getOrDefault(categoryId, 0);
            int allocated = allocatedByCategory.getOrDefault(categoryId, 0);
            int selected = selectedByCategory.getOrDefault(categoryId, 0);
            if (allocated + selected > required) {
                throw new IllegalArgumentException(
                        "Tổng số lượng tài sản được cấp vượt quá số lượng yêu cầu.");
            }
        }
    }

    // Save data to database
    public boolean processAllocation(long requestId, long staffId, String note, List<Long> assetIds) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Create new AssetAllocation
            AssetRequestDTO req = requestDAO.findById(conn, requestId);

            AssetAllocation alloc = new AssetAllocation();
            alloc.setRequestId(requestId);
            alloc.setAllocatedById(staffId);
            alloc.setToRoomId(req.getRequestedRoomId());
            alloc.setReceiverId(req.getTeacherId());
            alloc.setNote(note);

            // Create AllocationCode
            String code = String.valueOf(System.currentTimeMillis());
            alloc.setAllocationCode("ALC-" + code);

            // Save data to table AssetAllocation
            long allocationId = allocationDAO.insertAllocation(conn, alloc);

            if (allocationId <= 0) {
                conn.rollback();
                return false;
            }

            // Save data to table AllocationItems
            for (Long assetId : assetIds) {

                // Save data to table AssetAllocationItem
                boolean itemSuccess = allocItemDAO.insertAllocationItem(conn, allocationId, assetId);

                Long oldRoomId = assetDAO.findById(assetId).getCurrentRoomId();
                String oldSatus = assetDAO.findById(assetId).getStatus();
                // Save data to table AssetStatusHistory
                statusHistoryDAO.insertStatusHistory(
                        conn,
                        assetId,
                        oldSatus,
                        "IN_USE",
                        "Cấp phát cho yêu cầu #" + requestId,
                        staffId,
                        "ALLOCATION",
                        oldRoomId,
                         req.getRequestedRoomId());

                // update table Asset
                boolean assetSuccess = assetDAO.updateAsset(
                        conn,
                        assetId,
                        req.getRequestedRoomId(),
                        req.getTeacherId(),
                        "IN_USE");

                if (!itemSuccess || !assetSuccess) {
                    conn.rollback();
                    return false;
                }
            }

            // Update status in table AssetRequest
            updateRequestStatus(conn, requestId);

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback failed while allocating assets", ex);
            }
            LOGGER.log(Level.SEVERE, "Error processing allocation", e);
            return false;
        } finally {

            if (conn != null) try {
                conn.close();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE,
                        "Error closing connection in AllocateAssset", ex);
            }
        }
    }

    private void updateRequestStatus(Connection conn, long requestId) throws Exception {

        List<AssetRequestItemDTO> items = reqItemDAO.findByRequestId(conn, requestId);

        boolean isComplete = true;

        for (AssetRequestItemDTO item : items) {

            int required = item.getQuantity();

            int allocated = allocItemDAO.countAllocatedByCategory(
                    conn,
                    requestId,
                    item.getCategoryId()
            );

            if (allocated < required) {
                isComplete = false;
                break;
            }
        }

        if (isComplete) {
            requestDAO.updateStatus(conn, requestId, "COMPLETED");
        } else {
            requestDAO.updateStatus(conn, requestId, "INCOMPLETE");
        }
    }

    private boolean checkRequestCompleted(long requestId) throws Exception {

        List<AssetRequestItemDTO> items = reqItemDAO.findByRequestId(requestId);

        for (AssetRequestItemDTO item : items) {

            int required = item.getQuantity();

            int allocated = allocItemDAO.countAllocatedByCategory(
                    requestId,
                    item.getCategoryId());

            if (allocated < required) {
                return false;
            }
        }

        return true;
    }
}
