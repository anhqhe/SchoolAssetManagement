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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.asset.Asset;
import model.User;

/**
 *
 * @author An
 */
@WebServlet(name = "AssetServlet", urlPatterns = {"/assets"})
public class AssetServlet_CRUD extends HttpServlet {

    private AssetDao assetDao = new AssetDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        action = action.trim();
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
            case "detail":
                String idStr = request.getParameter("id");
                if (idStr == null || idStr.trim().isEmpty()) {
                    response.sendRedirect(request.getContextPath() + "/assets?action=list");
                    return;
                }
                try {
                    int id = Integer.parseInt(idStr.trim());
                    Asset asset = assetDao.findById(id);
                    request.setAttribute("asset", asset);
                    request.getRequestDispatcher("/views/asset/asset-detail.jsp").forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/assets?action=list");
                } catch (Exception e) {
                    response.sendRedirect(request.getContextPath() + "/assets?action=list");
                }
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
        } else if ("changeStatus".equals(action)) {
            changeStatusAsset(request, response);
        }
    }

    private void listAssets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String keyword = request.getParameter("keyword");
            String status = request.getParameter("status");
            String categoryIdStr = request.getParameter("categoryId");

            List<Asset> assets;
            if ((keyword != null && !keyword.trim().isEmpty())
                    || (status != null && !status.trim().isEmpty())
                    || (categoryIdStr != null && !categoryIdStr.trim().isEmpty())) {
                Long categoryId = null;
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        categoryId = Long.parseLong(categoryIdStr);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
                assets = assetDao.searchAssets(keyword, status, categoryId);
            } else {
                assets = assetDao.findAll();
            }

            request.setAttribute("assets", assets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryIdStr);
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

    private void changeStatusAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/views/auth/login.jsp");
            return;
        }
        try {
            long assetId = Long.parseLong(request.getParameter("assetId"));
            String newStatus = request.getParameter("newStatus");
            String reason = request.getParameter("reason");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/assets?action=detail&id=" + assetId);
                return;
            }
            assetDao.updateStatus(assetId, newStatus.trim(), reason != null ? reason.trim() : "", currentUser.getUserId());
            response.sendRedirect(request.getContextPath() + "/assets?action=detail&id=" + assetId);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
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
