/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.staff;

import dao.allocation.AssetRequestDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.AssetRequest;

/**
 *
 * @author Leo
 */
@WebServlet(name="ManageRequest", urlPatterns={"/staff/allocation-list"})
public class ManageAllocationRequest extends HttpServlet {
    
    private AssetRequestDAO assetRequestDAO = new AssetRequestDAO();
   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // 1. Gọi Service lấy danh sách phiếu PENDING
        // Lưu ý: Service nên dùng DAO có JOIN để lấy TeacherName và RoomName
        List<AssetRequest> list = assetRequestDAO.getPendingRequests();

        // 2. Đẩy dữ liệu ra JSP
        request.setAttribute("pendingRequests", list);

        // Nhận thông báo từ các thao tác trước đó (nếu có)
        String msg = request.getParameter("msg");
        if ("success".equals(msg)) request.setAttribute("msg", "Gửi duyệt thành công!");
        if ("out_of_stock".equals(msg)) request.setAttribute("error", "Kho không đủ tài sản!");

        request.getRequestDispatcher("/views/allocation/staff/allocation-list.jsp")
                .forward(request, response);
    } 

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
    }

}
