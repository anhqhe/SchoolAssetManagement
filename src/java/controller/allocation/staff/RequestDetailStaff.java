/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.allocation.staff;

import dao.allocation.AssetRequestDAO;
import dao.allocation.AssetRequestItemDAO;
import dto.AssetRequestDTO;
import dto.AssetRequestItemDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.AssetRequestItem;

/**
 *
 * @author Leo
 */
@WebServlet(name = "RequestDetailStaff", urlPatterns = {"/staff/request-detail"})
public class RequestDetailStaff extends HttpServlet {
    
    private AssetRequestDAO requestDAO = new AssetRequestDAO();
    private AssetRequestItemDAO reqItemDAO = new AssetRequestItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null) {
                response.sendRedirect("allocation-list");
                return;
            }

            long requestId = Long.parseLong(idParam);

            // 1. Lấy thông tin chung của phiếu (Người mượn, ngày mượn, phòng...)
            AssetRequestDTO requestDetail = requestDAO.findById(requestId);

            // 2. Lấy danh sách chi tiết các món đồ cần (Ví dụ: 2 Laptop, 1 Máy chiếu)
            List<AssetRequestItemDTO> itemList = reqItemDAO.findByRequestId(requestId);

            request.setAttribute("req", requestDetail);
            request.setAttribute("itemList", itemList);

            request.getRequestDispatcher("/views/allocation/staff/request-detail-staff.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("allocation-list?msg=error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
