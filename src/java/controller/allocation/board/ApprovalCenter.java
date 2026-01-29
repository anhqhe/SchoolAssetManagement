/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.board;

import dao.allocation.ApprovalDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Allocation.User;

/**
 *
 * @author Leo
 */
@WebServlet(name="Approval", urlPatterns={"/board/approval"})
public class ApprovalCenter extends HttpServlet {
    
    private ApprovalDAO approvalDAO = new ApprovalDAO();
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         // Lấy danh sách các yêu cầu đang ở trạng thái WAITING_APPROVE
        request.setAttribute("pendingApprovals", approvalDAO.getPendingList());

        request.getRequestDispatcher("/views/allocation/board/approval-center.jsp")
                .forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        HttpSession session = request.getSession();
        User boardMember = (User) session.getAttribute("user");

        try {
            long refId = Long.parseLong(request.getParameter("refId"));
            String refType = request.getParameter("refType"); // ASSET_REQUEST hoặc ASSET_TRANSFER
            String decision = request.getParameter("decision"); // APPROVED hoặc REJECTED
            String note = request.getParameter("decisionNote");

            
            boolean success = approvalDAO.processDecision(
                    refId, refType, boardMember.getUserId(), decision, note
            );

            if (success) {
                response.sendRedirect("approve?msg=done");
            } else {
                response.sendRedirect("approve?msg=error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("approve?msg=fail");
        }
    }

}
