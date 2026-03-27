/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.asset;

import dao.asset.AssetDao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.asset.Asset;
import model.asset.AssetLifecycleEvent;


/**
 *
 * @author An
 */
@WebServlet(name="AssetLifecycleServlet", urlPatterns={"/asset-lifecycle"})
public class AssetLifecycleServlet extends HttpServlet {
   
    private final AssetDao assetDao = new AssetDao();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
            return;
        }
        try {
            int id = Integer.parseInt(idStr.trim());
            Asset asset = assetDao.findById(id);
            if (asset == null) {
                response.sendRedirect(request.getContextPath() + "/assets?action=list");
                return;
            }
            List<AssetLifecycleEvent> events = assetDao.getLifecycleEvents(id);
            request.setAttribute("asset", asset);
            request.setAttribute("events", events);
            request.getRequestDispatcher("/views/asset/asset-lifecycle.jsp")
                   .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

}
