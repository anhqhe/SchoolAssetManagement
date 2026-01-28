/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.allocation.teacher;

import dao.allocation.RoomDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Allocation.User;
import dao.allocation.*;

/**
 *
 * @author Leo
 */
@WebServlet(name="AddRequest", urlPatterns={"/teacher/add-request"})
public class AddRequest extends HttpServlet {
    
    private RoomDAO roomDAO = new RoomDAO();
    private AssetCategoryDAO assetCategoryDAO = new AssetCategoryDAO();
   
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
        //        // 1. Get user information from session
//        HttpSession session = request.getSession();
//        User currentUser = (User) session.getAttribute("user");

        //DEMO
        List<String> roles = List.of("ADMIN", "TEACHER","STAFF");
        User currentUser = new User(1, "admin", "admin", true,roles );
        //Demo end

        try {
            // 2. Get data from Request Form
            long roomId = Long.parseLong(request.getParameter("requestedRoomId"));
            String purpose = request.getParameter("purpose");

            // Get list data
            String[] categoryIds = request.getParameterValues("categoryIds");
            String[] quantities = request.getParameterValues("quantities");
            String[] notes = request.getParameterValues("notes");

            // 3. Save to database
//            boolean success = requestService.createAssetRequest(
//                    currentUser.getUserId(),
//                    roomId,
//                    purpose,
//                    categoryIds,
//                    quantities,
//                    notes
//            );
            
            //demo
            boolean success = true;
            //demo end
            
            // 4. Phản hồi kết quả
            if (success) {
                // Chuyển hướng về danh sách phiếu đã tạo kèm thông báo thành công
                response.sendRedirect(request.getContextPath() + "/common/dashboard.jsp?msg=success");
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
}
