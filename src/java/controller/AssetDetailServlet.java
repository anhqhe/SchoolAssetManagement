package controller;

import dao.AssetDAO;
import model.Asset;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AssetDetailServlet", urlPatterns = {"/assets/detail"})
public class AssetDetailServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
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
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/assets/list");
            return;
        }
        
        try {
            long assetId = Long.parseLong(idStr);
            Asset asset = assetDAO.getAssetById(assetId);
            
            if (asset == null) {
                request.setAttribute("error", "Không tìm thấy tài sản với ID: " + assetId);
                response.sendRedirect(request.getContextPath() + "/assets/list");
                return;
            }
            
            request.setAttribute("asset", asset);
            request.getRequestDispatcher("/views/admin/asset-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/assets/list");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/asset-detail.jsp").forward(request, response);
        }
    }
}

