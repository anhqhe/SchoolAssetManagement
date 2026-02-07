/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.asset;

import dao.asset.AssetDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.asset.Asset;
import model.User;

/**
 *
 * @author An
 */
@WebServlet(name="AssetServlet_Auth", urlPatterns={"/assetAuth"})
public class AssetServlet_Auth extends HttpServlet {

    private final AssetDao assetDAO = new AssetDao();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        // Kiểm tra authentication
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/views/auth/login.jsp");
            return;
        }
        
        // Kiểm tra authorization - Tất cả users đã login đều có thể xem
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/views/auth/login.jsp");
            return;
        }
        
        try {
            // Lấy parameters cho search/filter
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String categoryIdStr = request.getParameter("categoryId");
            
            List<Asset> assets;
            
            // Nếu có filter/search
            if (keyword != null || status != null || categoryIdStr != null) {
                Long categoryId = null;
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        categoryId = Long.parseLong(categoryIdStr);
                    } catch (NumberFormatException e) {
                        // Ignore invalid categoryId
                    }
                }
                assets = assetDAO.searchAssets(keyword, status, categoryId);
            } else {
                // Lấy tất cả assets
                assets = assetDAO.findAll();
            }
            
            // Set attributes
            request.setAttribute("assets", assets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryIdStr);
            
            // Forward to JSP
            request.getRequestDispatcher("/views/asset/asset-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải danh sách tài sản: " + e.getMessage());
            request.getRequestDispatcher("/views/asset/asset-list.jsp").forward(request, response);
        }
    }
    

   
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       
    }
 
}
