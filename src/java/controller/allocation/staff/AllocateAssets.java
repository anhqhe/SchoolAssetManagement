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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
            return;
        }

        try {
            long requestId = Long.parseLong(request.getParameter("requestId"));

            AssetRequestDTO requestDetail = requestDAO.findById(requestId);
            if (requestDetail == null) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Không thể tải yêu cầu cấp phát.");
                response.sendRedirect("request-list");
                return;
            }
            
            //Only request approved_by_board can allocate asset
            if (!requestDetail.getStatus().equals("APPROVED_BY_BOARD")) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Yêu cầu chưa được phê duyệt để cấp phát.");
                response.sendRedirect("request-list");
                return;
            }

            List<AssetRequestItemDTO> neededItems = reqItemDAO.findByRequestId(requestId);

            //Get available assets
            List<AssetDTO> availableAssets = assetDAO.findAvailableAssets();

            request.setAttribute("requestDetail", requestDetail);
            request.setAttribute("neededItems", neededItems);
            request.setAttribute("availableAssets", availableAssets);

            request.getRequestDispatcher("/views/allocation/staff/allocate-assets.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("type", "error");
            session.setAttribute("message", "Không thể tải yêu cầu cấp phát.");
            response.sendRedirect("request-list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User staff = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (staff == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        List<String> roles = staff.getRoles();
        if (roles == null || !roles.contains("ASSET_STAFF")) {
            response.sendRedirect(request.getContextPath() + "/staff/request-list");
            return;
        }

        String action = request.getParameter("action");
        // Get RequestId, list asset, note
        long requestId = Long.parseLong(request.getParameter("requestId"));

        if ("notify_out_of_stock".equals(action)) {
            AssetRequestDTO reqDTO = requestDAO.findById(requestId);
            if (reqDTO != null) {
                NotificationEndPoint.sendToUser(reqDTO.getTeacherId(),
                        "Kho hàng không đủ tài sản",
                        "Kho hàng không đủ tài sản cấp phát cho phiếu " + reqDTO.getRequestCode() + ".",
                        "ASSET_REQUEST",
                        requestId);
            }
            session.setAttribute("type", "info");
            session.setAttribute("message", "Đã gửi thông báo hết tài sản cho giáo viên.");
            response.sendRedirect("allocate-assets?requestId=" + requestId);
            return;
        }

        String note = request.getParameter("note");
        String[] selectedAssetIds = request.getParameterValues("selectedAssetIds");

        if (selectedAssetIds == null || selectedAssetIds.length == 0) {
            session.setAttribute("type", "warning");
            session.setAttribute("message", "Vui lòng chọn ít nhất một tài sản để cấp phát.");
            response.sendRedirect("allocate-assets?requestId=" + requestId);
            return;
        }

        // Chuyển mảng String[] sang List<Long>
        List<Long> assetIds = new java.util.ArrayList<>();
        for (String id : selectedAssetIds) {
            assetIds.add(Long.parseLong(id));
        }
             

        // Save data to database
        // Insert AssetAllocations, AssetAllocationItems & Update Status Assets + Request
        boolean success = processAllocation(requestId, staff.getUserId(), note, assetIds);
        
        if (!success) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra khi cấp phát tài sản.");
            response.sendRedirect("allocate-assets?requestId=" + requestId);
            return;
        }
        
        //Save to database sucessfully
        
        //Send notification to teacher
        AssetRequestDTO reqDTO = requestDAO.findById(requestId);
        NotificationEndPoint.sendToUser(reqDTO.getTeacherId(), 
                "Yêu cầu cấp phát đã hoàn thành",
                "Tài sản của phiếu " + reqDTO.getRequestCode() + " đã được chuẩn bị xong. Hãy đến nhận!",
                "ASSET_REQUEST",
                requestId);

        session.setAttribute("type", "success");
        session.setAttribute("message", "Cấp phát tài sản thành công!");
        response.sendRedirect("request-list");
    }

    // Save data to database
    public boolean processAllocation(long requestId, long staffId, String note, List<Long> assetIds) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Create new AssetAllocation
            AssetAllocation alloc = new AssetAllocation();
            alloc.setRequestId(requestId);
            alloc.setAllocatedById(staffId);
            alloc.setNote(note);
            
            AssetRequestDTO req = requestDAO.findById(requestId);
            alloc.setToRoomId(req.getRequestedRoomId());
            alloc.setReceiverId(req.getTeacherId());

            // Create AllocationCode
            String timeStamp = String.valueOf(System.currentTimeMillis()).substring(7);
            alloc.setAllocationCode("ALC-" + timeStamp);

            // Save data to table AssetAllocation
            long allocationId = allocationDAO.insertAllocation(conn, alloc);

            if (allocationId <= 0) {
                conn.rollback();
                return false;
            }

            // 3. Duyệt danh sách AssetId để chèn vào AllocationItems
            for (Long assetId : assetIds) {

                // Save data to table AssetAllocationItem
                boolean itemSuccess = allocItemDAO.insertAllocationItem(conn, allocationId, assetId);

                // Update Status in table Asset
                boolean assetSuccess = assetDAO.updateAssetStatus(conn, assetId, "IN_USE");

                // Save data to table AssetStatusHistory
                statusHistoryDAO.insertStatusHistory(conn, assetId, "IN_USE", "Cấp phát cho yêu cầu #" + requestId, staffId);

                if (!itemSuccess || !assetSuccess) {
                    conn.rollback();
                    return false;
                }
            }

            // Update status in table AssetRequest
            requestDAO.updateStatus(conn, requestId, "COMPLETED");

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try {
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }
}
