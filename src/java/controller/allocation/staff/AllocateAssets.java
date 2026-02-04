/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.staff;

import controller.allocation.websocket.NotificationEndPoint;
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
        try {
            long requestId = Long.parseLong(request.getParameter("requestId"));

            AssetRequestDTO requestDetail = requestDAO.findById(requestId);
            
            //Only request approved_by_board can allocate asset
            if (!requestDetail.getStatus().equals("APPROVED_BY_BOARD")) {
                response.sendRedirect("allocation-list?error=true");
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
            response.sendRedirect("allocation-list?error=true");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        // Get Staff data
        User staff = (User) request.getSession().getAttribute("currentUser");

        // Get RequestId, list asset, note
        long requestId = Long.parseLong(request.getParameter("requestId"));
        String note = request.getParameter("note");
        String[] selectedAssetIds = request.getParameterValues("selectedAssetIds");

        if (selectedAssetIds == null || selectedAssetIds.length == 0) {
            response.sendRedirect("allocate-assets?requestId=" + requestId + "&msg=no_selection");
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
            response.sendRedirect("allocate-assets?requestId=" + requestId + "&msg=error");
            return;
        }
        
        //Save to database sucessfully
        
        //Send notification to teacher
        AssetRequestDTO reqDTO = requestDAO.findById(requestId);
        NotificationEndPoint.sendToUser(reqDTO.getTeacherId(), 
                "Tài sản của phiếu " + reqDTO.getRequestCode() + " đã được chuẩn bị xong. Hãy đến nhận!");

        response.sendRedirect("allocation-list?msg=success");
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
