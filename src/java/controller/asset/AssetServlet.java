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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Asset;

/**
 *
 * @author An
 */
@WebServlet(name = "AssetServlet", urlPatterns = {"/assets"})
public class AssetServlet extends HttpServlet {

    private AssetDao assetDao = new AssetDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "create":
                showCreateForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteAsset(request, response);
                break;
            default:
                listAssets(request, response);

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("create".equals(action)) {
            insertAsset(request, response);
        } else if ("edit".equals(action)) {
            updateAsset(request, response);
        }
    }

    private void listAssets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Asset> assets = assetDao.findAll();
            request.setAttribute("assets", assets);
            request.getRequestDispatcher("/views/asset/asset-list.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("mode", "create");
        request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Asset asset = new AssetDao().findById(id);
            request.setAttribute("asset", asset);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void insertAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Asset asset = builtAssetFromRequest(request);
            assetDao.insert(asset);
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void updateAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Asset asset = builtAssetFromRequest(request);
            asset.setAssetId(Integer.parseInt(request.getParameter("assetId")));
            assetDao.update(asset);
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void deleteAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            assetDao.delete(id);
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private Asset builtAssetFromRequest(HttpServletRequest request)
            throws ServletException, IOException {
        Asset a = new Asset();
        a.setAssetCode(request.getParameter("assetCode"));
        a.setAssetName(request.getParameter("assetName"));
        a.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
        a.setSerialNumber(request.getParameter("serialNumber"));
        a.setModel(request.getParameter("model"));
        a.setBrand(request.getParameter("brand"));
        a.setOriginNote(request.getParameter("originNote"));
        //Purchase Date
        String pDateStr = request.getParameter("purchaseDate");
        if (pDateStr != null && !pDateStr.isEmpty()) {
            a.setPurchaseDate(LocalDate.parse(pDateStr).atStartOfDay());
        }
        //Received Date
        String rDateStr = request.getParameter("receivedDate");
        if (rDateStr != null && !rDateStr.isEmpty()) {
            a.setReceivedDate(LocalDate.parse(rDateStr).atStartOfDay());
        }
        a.setConditionNote(request.getParameter("conditionNote"));
        a.setStatus(request.getParameter("status"));
        //Current Room Id
        String roomIdStr = request.getParameter("currentRoomId");
        if (roomIdStr != null && !roomIdStr.isEmpty()) {
            a.setCurrentRoomId(Integer.parseInt(roomIdStr));
        }
        //Current Holder Id
        String holderIdStr = request.getParameter("currentHolderId");
        if (holderIdStr != null && !holderIdStr.isEmpty()) {
            a.setCurrentHolderId(Integer.parseInt(holderIdStr));
        }
        a.setIsActive("on".equals(request.getParameter("isActive")));
        return a;
    }

}
