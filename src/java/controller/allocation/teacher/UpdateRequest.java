/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AssetCategoryDAO;
import controller.allocation.websocket.NotificationEndPoint;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dao.allocation.UserDAO;
import dao.allocation.RoomDAO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.User;
import model.allocation.AssetRequestItem;
import util.DBUtil;

/**
 *
 * @author Leo
 */
@WebServlet(name = "UpdateRequest", urlPatterns = {"/teacher/update-request"})
public class UpdateRequest extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final AssetCategoryDAO categoryDAO = new AssetCategoryDAO();
    private final AssetRequestDAO requestDAO = new AssetRequestDAO();
    private final AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("TEACHER")) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=invalid_id");
            return;
        }

        try {
            long requestId = Long.parseLong(idParam);
            AssetRequestDTO req = requestDAO.findById(requestId);

            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=request_not_found");
                return;
            }

            if (req.getTeacherId() != currentUser.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=not_allowed");
                return;
            }

            if (!"WAITING_BOARD".equals(req.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId + "&error=not_editable");
                return;
            }

            List<AssetRequestItemDTO> itemList = itemDAO.findByRequestId(requestId);

            request.setAttribute("req", req);
            request.setAttribute("itemList", itemList);
            request.setAttribute("rooms", roomDAO.getAllActiveRooms());
            request.setAttribute("categories", categoryDAO.getAllActiveCategories());

            request.getRequestDispatcher("/views/allocation/teacher/update-request.jsp")
                    .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=invalid_id");
        } catch (Exception e) {
            e.printStackTrace(System.err);
            response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=load_failed");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        List<String> roles = currentUser.getRoles();
        if (roles == null || !roles.contains("TEACHER")) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        try {
            long requestId = Long.parseLong(request.getParameter("requestId"));

            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=request_not_found");
                return;
            }

            if (req.getTeacherId() != currentUser.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-list?error=not_allowed");
                return;
            }

            if (!"WAITING_BOARD".equals(req.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId + "&error=not_editable");
                return;
            }

            long roomId = Long.parseLong(request.getParameter("requestedRoomId"));
            String purpose = request.getParameter("purpose");
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            boolean success = updateAssetRequest(requestId, roomId, purpose, categoryIds, quantities, notes);

            if (success) {
                // Notify board about updated request
                List<Long> boardIds = userDAO.getIdsByRole("BOARD");
                NotificationEndPoint.sendToUsers(boardIds, currentUser.getFullName()+" đã cập nhật yêu cầu: " + req.getRequestCode());
                
                response.sendRedirect(request.getContextPath() + "/teacher/request-detail?id=" + requestId + "&msg=success");
            } else {
                request.setAttribute("error", "Update failed. Please try again.");
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            request.setAttribute("error", "Invalid input data.");
            doGet(request, response);
        }
    }

    private boolean updateAssetRequest(long requestId, long roomId, String purpose,
            String[] categoryIds, String[] quantities, String[] notes) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            boolean updated = requestDAO.updateRequest(conn, requestId, roomId, purpose);
            if (!updated) {
                conn.rollback();
                return false;
            }

            itemDAO.deleteByRequestId(conn, requestId);

            if (categoryIds != null) {
                for (int i = 0; i < categoryIds.length; i++) {
                    AssetRequestItem item = new AssetRequestItem();
                    item.setRequestId(requestId);
                    item.setCategoryId(Long.parseLong(categoryIds[i]));
                    item.setQuantity(Integer.parseInt(quantities[i]));
                    item.setNote(notes[i]);
                    itemDAO.insert(conn, item);
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("[UpdateRequest] Database error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("[UpdateRequest] Rollback failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("[UpdateRequest] Unexpected error: " + e.getMessage());
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
