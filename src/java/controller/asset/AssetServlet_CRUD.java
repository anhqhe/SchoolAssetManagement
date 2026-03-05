/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.asset;

import dao.asset.AssetDao;
import dao.asset.AssetCategoryDao;
import dao.asset.RoomDao;
import dao.asset.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            try {
                insertAsset(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(AssetServlet_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if ("edit".equals(action)) {
            try {
                updateAsset(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(AssetServlet_CRUD.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            String activeState = request.getParameter("activeState");
            Boolean isActiveFilter = null;
            if ("active".equals(activeState)) {
                isActiveFilter = Boolean.TRUE;
            } else if ("inactive".equals(activeState)) {
                isActiveFilter = Boolean.FALSE;
            }
            List<Asset> assets;
            boolean hasFilter = ((keyword != null && !keyword.trim().isEmpty())
                    || (status != null && !status.trim().isEmpty())
                    || (categoryIdStr != null && !categoryIdStr.trim().isEmpty())
                    || (activeState != null && !activeState.trim().isEmpty()));

            if (hasFilter) {
                Long categoryId = null;
                if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                    try {
                        categoryId = Long.parseLong(categoryIdStr);
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
                assets = assetDao.searchAssets(keyword, status, categoryId, isActiveFilter);
            } else {
                assets = assetDao.findAll();
            }

            request.setAttribute("assets", assets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryIdStr);
            request.setAttribute("activeState", activeState);
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.getRequestDispatcher("/views/asset/asset-list.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            AssetCategoryDao categoryDao = new AssetCategoryDao();
            request.setAttribute("categories", categoryDao.findAllActive());
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.setAttribute("mode", "create");
            request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Asset asset = new AssetDao().findById(id);
            AssetCategoryDao categoryDao = new AssetCategoryDao();
            request.setAttribute("categories", categoryDao.findAllActive());
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.setAttribute("asset", asset);
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void insertAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            String error = validateAssetForm(request, true);
            if (error != null) {
                Asset assetForForm = buildAssetFromRequestSafe(request);
                AssetCategoryDao categoryDao = new AssetCategoryDao();
                request.setAttribute("asset", assetForForm);
                request.setAttribute("categories", categoryDao.findAllActive());
                request.setAttribute("rooms", roomDao.findAllActive());
                request.setAttribute("teachers", userDao.findAllTeachers());
                request.setAttribute("errorMessage", error);
                request.setAttribute("mode", "create");
                request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
                return;
            }
            Asset asset = builtAssetFromRequest(request);
            assetDao.insert(asset);
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            Asset assetForForm = buildAssetFromRequestSafe(request);
            AssetCategoryDao categoryDao = new AssetCategoryDao();
            request.setAttribute("asset", assetForForm);
            request.setAttribute("categories", categoryDao.findAllActive());
            request.setAttribute("errorMessage", "Mã tài sản đã tồn tại hoặc dữ liệu không hợp lệ.");
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.setAttribute("mode", "create");
            request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
        }
    }

    private void updateAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            String error = validateAssetForm(request, false);
            if (error != null) {
                Asset assetForForm = buildAssetFromRequestSafe(request);
                String assetIdStr = request.getParameter("assetId");
                if (assetForForm != null && assetIdStr != null && !assetIdStr.trim().isEmpty()) {
                    try {
                        assetForForm.setAssetId(Integer.parseInt(assetIdStr.trim()));
                    } catch (NumberFormatException ignored) {
                    }
                }
                AssetCategoryDao categoryDao = new AssetCategoryDao();
                request.setAttribute("asset", assetForForm);
                request.setAttribute("categories", categoryDao.findAllActive());
                request.setAttribute("errorMessage", error);
                request.setAttribute("rooms", roomDao.findAllActive());
                request.setAttribute("teachers", userDao.findAllTeachers());
                request.setAttribute("mode", "edit");
                request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
                return;
            }
            Asset asset = builtAssetFromRequest(request);
            asset.setAssetId(Integer.parseInt(request.getParameter("assetId")));
            assetDao.update(asset);
            response.sendRedirect(request.getContextPath() + "/assets?action=list");
        } catch (SQLException e) {
            Asset assetForForm = buildAssetFromRequestSafe(request);
            String assetIdStr = request.getParameter("assetId");
            if (assetForForm != null && assetIdStr != null && !assetIdStr.trim().isEmpty()) {
                try {
                    assetForForm.setAssetId(Integer.parseInt(assetIdStr.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            AssetCategoryDao categoryDao = new AssetCategoryDao();
            request.setAttribute("asset", assetForForm);
            request.setAttribute("categories", categoryDao.findAllActive());
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.setAttribute("errorMessage", "Mã tài sản đã tồn tại hoặc dữ liệu không hợp lệ.");
            request.setAttribute("mode", "edit");
            request.getRequestDispatcher("/views/asset/asset-form.jsp").forward(request, response);
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

        String statusParam = request.getParameter("status");
        if (statusParam == null || statusParam.trim().isEmpty()) {
            a.setStatus("IN_STOCK");
        } else {
            a.setStatus(statusParam);
        }

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

    /**
     * Build Asset từ request params một cách an toàn (không throw) - dùng khi
     * hiển thị lại form sau validation fail
     */
    private Asset buildAssetFromRequestSafe(HttpServletRequest request) {
        try {
            Asset a = new Asset();
            a.setAssetCode(request.getParameter("assetCode"));
            a.setAssetName(request.getParameter("assetName"));
            String catIdStr = request.getParameter("categoryId");
            if (catIdStr != null && !catIdStr.trim().isEmpty()) {
                a.setCategoryId(Long.parseLong(catIdStr.trim()));
            }
            a.setSerialNumber(request.getParameter("serialNumber"));
            a.setModel(request.getParameter("model"));
            a.setBrand(request.getParameter("brand"));
            a.setOriginNote(request.getParameter("originNote"));

            String pDateStr = request.getParameter("purchaseDate");
            if (pDateStr != null && !pDateStr.trim().isEmpty()) {
                a.setPurchaseDate(LocalDate.parse(pDateStr.trim()).atStartOfDay());
            }
            String rDateStr = request.getParameter("receivedDate");
            if (rDateStr != null && !rDateStr.trim().isEmpty()) {
                a.setReceivedDate(LocalDate.parse(rDateStr.trim()).atStartOfDay());
            }

            a.setConditionNote(request.getParameter("conditionNote"));

            String statusParam = request.getParameter("status");
            if (statusParam != null && !statusParam.trim().isEmpty()) {
                a.setStatus(statusParam.trim());
            } else {
                a.setStatus("IN_STOCK");
            }

            String roomIdStr = request.getParameter("currentRoomId");
            if (roomIdStr != null && !roomIdStr.trim().isEmpty()) {
                try {
                    a.setCurrentRoomId(Long.parseLong(roomIdStr.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            String holderIdStr = request.getParameter("currentHolderId");
            if (holderIdStr != null && !holderIdStr.trim().isEmpty()) {
                try {
                    a.setCurrentHolderId(Long.parseLong(holderIdStr.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
            a.setIsActive("on".equals(request.getParameter("isActive")));
            return a;
        } catch (Exception e) {
            return null;
        }
    }

    private final RoomDao roomDao = new RoomDao();
    private final UserDao userDao = new UserDao();

    private String validateAssetForm(HttpServletRequest request, boolean isCreate) throws SQLException {
        String assetCode = request.getParameter("assetCode");
        String assetName = request.getParameter("assetName");
        String categoryIdStr = request.getParameter("categoryId");
        String roomIdStr = request.getParameter("currentRoomId");
        String holderIdStr = request.getParameter("currentHolderId");
        String pDateStr = request.getParameter("purchaseDate");
        String rDateStr = request.getParameter("receivedDate");

        if (assetCode == null || assetCode.trim().isEmpty()) {
            return "Mã tài sản là bắt buộc.";
        }

        if (assetCode == null || assetCode.trim().isEmpty()) {
            return "Mã tài sản là bắt buộc.";
        }
        if (assetName == null || assetName.trim().isEmpty()) {
            return "Tên tài sản là bắt buộc.";
        }

        if (categoryIdStr == null || categoryIdStr.trim().isEmpty()) {
            return "Vui lòng chọn danh mục.";
        }

        //Ngày mua phải sau ngày nhập
        try {
            if (pDateStr != null && !pDateStr.trim().isEmpty() && rDateStr != null && !rDateStr.trim().isEmpty()) {
                LocalDate p = LocalDate.parse(pDateStr);
                LocalDate r = LocalDate.parse(rDateStr);
                if (p.isAfter(r)) {
                    return "Ngày mua không được sau ngày nhận";
                }
            }
        } catch (Exception e) {
            return "Định dạng ngày không hợp lệ";
        }

        //Validate roomID tồn tại
        if (roomIdStr != null && !roomIdStr.isEmpty()) {
            try {
                long roomId = Long.parseLong(roomIdStr);
                if (roomId < 0) {
                    return "Id phòng không được âm.";
                }
                if (!roomDao.exists(roomId)) {
                    return "Phòng hiện tại không tồn tại.";
                }
            } catch (NumberFormatException e) {
                return "Room ID không hợp lệ.";
            }

        }

        //Validate holderId tồn tại
        if (holderIdStr != null && !holderIdStr.trim().isEmpty()) {
            try {
                long holderId = Long.parseLong(holderIdStr);
                if (holderId < 0) {
                    return "Id người giữ không được âm.";
                }
                if (!userDao.exists(holderId)) {
                    return "User (người giữ) không tồn tại.";
                }
            } catch (NumberFormatException e) {
                return "Room ID không hợp lệ.";
            }

        }
        //Check assetCode là duy nhất
        if (isCreate) {
            if (assetDao.existsByCode(assetCode.trim())) {
                return "Mã tài sản đã tồn tại, vui lòng chọn mã khác.";
            }
        } else {
            // Edit: chỉ báo lỗi nếu mã trùng với TÀI SẢN KHÁC (không tính bản thân)
            String assetIdStr = request.getParameter("assetId");
            if (assetIdStr != null && !assetIdStr.trim().isEmpty()) {
                try {
                    int assetId = Integer.parseInt(assetIdStr.trim());
                    if (assetDao.existsByCodeExcludingId(assetCode.trim(), assetId)) {
                        return "Mã tài sản đã tồn tại, vui lòng chọn mã khác.";
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;// hợp lệ
    }
}
