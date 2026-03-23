/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.AssetRequestDAO;
import dao.allocation.FeedbackDAO;
import dao.allocation.RoomDAO;
import dto.AssetRequestDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestList", urlPatterns = {"/teacher/request-list"})
public class RequestListTeacher extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private RoomDAO roomDAO = new RoomDAO();
    private FeedbackDAO feedbackDAO = new FeedbackDAO();

    private static final Logger LOGGER
            = Logger.getLogger(RequestListTeacher.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get User Data from Session
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }

        //currentUser != null
        //Filter
        // Get parameters
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String sortBy = request.getParameter("sortBy");

        List<AssetRequestDTO> list;
        try {
            list = requestDAO.getRequestsByTeacher(currentUser.getUserId(), keyword, status, sortBy);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while loading teacher asset request list. userId="
                    + currentUser.getUserId(), ex);

            response.sendRedirect(request.getContextPath() + "/views/common/500.jsp");
            return;
        }
        
        request.setAttribute("roomDAO", roomDAO);
        request.setAttribute("requestList", list);
        request.getRequestDispatcher("/views/allocation/request-list.jsp")
                .forward(request, response);
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

        String action = request.getParameter("action");
        if (!"feedback".equals(action)) {
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        String requestIdRaw = request.getParameter("requestId");
        String contentRaw = request.getParameter("content");

        long requestId;
        try {
            requestId = Long.parseLong(requestIdRaw);
        } catch (NumberFormatException ex) {
            session.setAttribute("type", "error");
            session.setAttribute("message", "ID yêu cầu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        String content = contentRaw == null ? "" : contentRaw.trim();
        if (content.isEmpty()) {
            session.setAttribute("type", "warning");
            session.setAttribute("message", "Vui lòng nhập nội dung đánh giá.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }
        if (content.length() > 1000) {
            session.setAttribute("type", "warning");
            session.setAttribute("message", "Nội dung đánh giá tối đa 1000 ký tự.");
            response.sendRedirect(request.getContextPath() + "/teacher/request-list");
            return;
        }

        try {
            AssetRequestDTO req = requestDAO.findById(requestId);
            if (req == null || req.getTeacherId() == null || req.getTeacherId() != currentUser.getUserId()) {
                session.setAttribute("type", "error");
                session.setAttribute("message", "Bạn không có quyền đánh giá yêu cầu này.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }
            if (!"COMPLETED".equals(req.getStatus())) {
                session.setAttribute("type", "warning");
                session.setAttribute("message", "Chỉ yêu cầu đã hoàn thành mới được đánh giá.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }

            boolean exists = feedbackDAO.existsByCreatorAndTarget(
                    currentUser.getUserId(), "ASSET_REQUEST", requestId);
            if (exists) {
                session.setAttribute("type", "info");
                session.setAttribute("message", "Bạn đã đánh giá yêu cầu này trước đó.");
                response.sendRedirect(request.getContextPath() + "/teacher/request-list");
                return;
            }

            boolean success = feedbackDAO.insert(
                    currentUser.getUserId(), "ASSET_REQUEST", requestId, content);

            session.setAttribute("type", success ? "success" : "error");
            session.setAttribute("message", success
                    ? "Gửi đánh giá thành công."
                    : "Không thể gửi đánh giá. Vui lòng thử lại.");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE,
                    "Database error while saving teacher feedback. userId="
                    + currentUser.getUserId() + ", requestId=" + requestId, ex);
            session.setAttribute("type", "error");
            session.setAttribute("message", "Có lỗi xảy ra khi gửi đánh giá.");
        }

        response.sendRedirect(request.getContextPath() + "/teacher/request-list");
    }

}
