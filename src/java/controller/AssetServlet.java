package controller;

import dao.AssetDAO;
import model.Asset;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AssetServlet", urlPatterns = {"/admin/assets"})
public class AssetServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
                assets = assetDAO.getAllAssets();
            }
            
            // Set attributes
            request.setAttribute("assets", assets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryIdStr);
            
            // Forward to JSP
            request.getRequestDispatcher("/views/admin/asset-list.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra khi tải danh sách tài sản: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/asset-list.jsp").forward(request, response);
        }
    }
}

