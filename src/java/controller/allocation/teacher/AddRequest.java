/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AssetCategoryDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Allocation.User;
import util.DBUtil;
import java.sql.Connection;
import model.AssetRequest;
import model.AssetRequestItem;
import java.sql.SQLException;

/**
 *
 * @author Leo
 */
@WebServlet(name = "AddRequest", urlPatterns = {"/teacher/add-request"})
public class AddRequest extends HttpServlet {

    private RoomDAO roomDAO = new RoomDAO();
    private AssetCategoryDAO assetCategoryDAO = new AssetCategoryDAO();
    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO requestItemDAO = new AssetRequestItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("rooms", roomDAO.getAllActiveRooms());
        request.setAttribute("categories", assetCategoryDAO.getAllActiveCategories());

        request.getRequestDispatcher("/views/allocation/teacher/add-request.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //        // Get user information from session
//        HttpSession session = request.getSession();
//        User currentUser = (User) session.getAttribute("user");

        //DEMO
        List<String> roles = List.of("ADMIN", "TEACHER", "STAFF");
        User currentUser = new User(1, "admin", "admin", true, roles);
        //Demo end

        try {
            // Get data from Request Form
            long roomId = Long.parseLong(request.getParameter("requestedRoomId"));
            String purpose = request.getParameter("purpose");

            // Get list data
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            // Save to database
            boolean success = createAssetRequest(
                    currentUser.getUserId(),
                    roomId,
                    purpose,
                    categoryIds,
                    quantities,
                    notes);

            // Return result
            if (success) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?msg=success");
            } else {
                request.setAttribute("error", "Không thể tạo yêu cầu. Vui lòng thử lại!");
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Dữ liệu nhập vào không hợp lệ!");
            doGet(request, response);
        }
    }

    public boolean createAssetRequest(long userId, long roomId, String purpose,
            String[] catIds, String[] qtys, String[] notes) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // Start Transaction
            conn.setAutoCommit(false);

            // Create AssetRequest
            AssetRequest request = new AssetRequest();
            request.setTeacherId(userId);
            request.setRequestedRoomId(roomId);
            request.setPurpose(purpose);
            request.setStatus("WAITING_BOARD"); // Default Status

            // Create RequestCode
            String requestCode = "REQ-" + System.currentTimeMillis();
            request.setRequestCode(requestCode);

            // Insert to table AssetRequest
            long requestId = requestDAO.insert(conn,request);

            // Insert table AssetRequestItems
            if (requestId > 0 && catIds != null) {
                for (int i = 0; i < catIds.length; i++) {
                    AssetRequestItem item = new AssetRequestItem();
                    item.setRequestId(requestId);
                    item.setCategoryId(Long.parseLong(catIds[i]));
                    item.setQuantity(Integer.parseInt(qtys[i]));
                    item.setNote(notes[i]);

                    // Lưu từng dòng item
                    requestItemDAO.insert(conn, item);
                }
            }

            conn.commit(); // End Transaction
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();    //Rollback if have error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
