/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.teacher;

import dao.allocation.ApprovalDAO;
import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dto.ApprovalDTO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestDetail", urlPatterns = {"/teacher/request-detail"})
public class RequestDetailTeacher extends HttpServlet {

    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO itemDAO = new AssetRequestItemDAO();
    private ApprovalDAO approvalDAO = new ApprovalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            long requestId = Long.parseLong(request.getParameter("id"));

            AssetRequestDTO req = requestDAO.findById(requestId);
            List<AssetRequestItemDTO> items = itemDAO.findByRequestId(requestId);
            ApprovalDTO approval = approvalDAO.findByRef("ASSET_REQUEST", requestId);

            request.setAttribute("req", req);
            request.setAttribute("items", items);
            request.setAttribute("approval", approval);

            request.getRequestDispatcher("/views/allocation/teacher/request-detail.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            response.sendRedirect("request-list?error=notfound");
            System.err.println("Error in RequestDetail.doGet(): " + e);
            e.printStackTrace(System.err);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
