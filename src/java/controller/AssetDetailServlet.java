package controller;

import dao.AssetDAO;
import model.Asset;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AssetDetailServlet", urlPatterns = {"/admin/asset-detail"})
public class AssetDetailServlet extends HttpServlet {
    
    private final AssetDAO assetDAO = new AssetDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/assets");
            return;
        }
        
        try {
            long assetId = Long.parseLong(idStr);
            Asset asset = assetDAO.getAssetById(assetId);
            
            if (asset == null) {
                request.setAttribute("error", "Không tìm thấy tài sản với ID: " + assetId);
                response.sendRedirect(request.getContextPath() + "/admin/assets");
                return;
            }
            
            request.setAttribute("asset", asset);
            request.getRequestDispatcher("/views/admin/asset-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/assets");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            request.getRequestDispatcher("/views/admin/asset-detail.jsp").forward(request, response);
        }
    }
}

