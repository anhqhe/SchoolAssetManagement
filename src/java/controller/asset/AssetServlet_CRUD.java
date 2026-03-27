/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.asset;

import dao.asset.AssetDao;
import dao.asset.AssetCategoryDao;
import dao.room.RoomDao;
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
            // ------------------ PHÂN TRANG ------------------
            int page = 1;
            int pageSize = 10; // số bản ghi / trang
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam.trim());
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            boolean hasFilter = ((keyword != null && !keyword.trim().isEmpty())
                    || (status != null && !status.trim().isEmpty())
                    || (categoryIdStr != null && !categoryIdStr.trim().isEmpty())
                    || (activeState != null && !activeState.trim().isEmpty()));
            Long categoryId = null;
            if (categoryIdStr != null && !categoryIdStr.trim().isEmpty()) {
                try {
                    categoryId = Long.parseLong(categoryIdStr.trim());
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            // 1. Đếm tổng bản ghi theo filter
            int totalRecords;
            if (hasFilter) {
                totalRecords = assetDao.countSearchAssets(keyword, status, categoryId, isActiveFilter);
            } else {
                totalRecords = assetDao.countAllAssets();
            }
            int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
            if (totalPages == 0) {
                totalPages = 1;
            }
            if (page > totalPages) {
                page = totalPages;
            }
            int offset = (page - 1) * pageSize;
            // 2. Lấy danh sách theo trang
            List<Asset> assets;
            if (hasFilter) {
                assets = assetDao.searchAssetsByPage(keyword, status, categoryId, isActiveFilter, offset, pageSize);
            } else {
                assets = assetDao.findAllByPage(offset, pageSize);
            }
            // 3. Truyền sang JSP
            request.setAttribute("assets", assets);
            request.setAttribute("keyword", keyword);
            request.setAttribute("status", status);
            request.setAttribute("categoryId", categoryIdStr);
            request.setAttribute("activeState", activeState);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("pageSize", pageSize);
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
            request.getRequestDispatcher("/views/asset/asset-create.jsp").forward(request, response);
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
            request.getRequestDispatcher("/views/asset/asset-edit.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void insertAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        try {
            String error = validateAssetFormCreate(request);
            if (error != null) {
                Asset assetForForm = buildAssetFromRequestSafe(request);
                AssetCategoryDao categoryDao = new AssetCategoryDao();
                request.setAttribute("asset", assetForForm);
                request.setAttribute("categories", categoryDao.findAllActive());
                request.setAttribute("rooms", roomDao.findAllActive());
                request.setAttribute("teachers", userDao.findAllTeachers());
                request.setAttribute("errorMessage", error);
                request.setAttribute("mode", "create");
                request.getRequestDispatcher("/views/asset/asset-create.jsp").forward(request, response);
                return;
            }
            Asset template = buildAssetFromRequestForCreate(request);
            int quantity = Integer.parseInt(request.getParameter("quantity").trim());

            // --- Lấy thông tin cho phiếu ghi tăng ---
            HttpSession session = request.getSession(false);
            User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;
            long userId = (currentUser != null) ? currentUser.getUserId() : 0;

            String sourceType = request.getParameter("sourceType");
            if (sourceType == null || sourceType.trim().isEmpty()) {
                sourceType = "Mua mới";
            }

            List<String> codes = assetDao.generateAssetCodes(template.getCategoryId(), quantity);
            List<Long> insertedAssetIds = new ArrayList<>();

            for (String code : codes) {
                Asset a = cloneAsset(template);
                a.setAssetCode(code);
                long assetId = assetDao.insert(a);
                if (assetId > 0) {
                    insertedAssetIds.add(assetId);
                }
            }
            int inserted = insertedAssetIds.size();

            if (inserted > 0) {
                String increaseCode = assetDao.generateIncreaseCode();
                LocalDate receivedDate = template.getReceivedDate() != null
                        ? template.getReceivedDate().toLocalDate()
                        : LocalDate.now();
                long increaseID = assetDao.insertIncreaseRecord(increaseCode, sourceType.trim(),
                        null, receivedDate, userId, null);
                if (increaseID > 0) {
                    for (long assetId : insertedAssetIds) {
                        assetDao.insertIncreaseItem(increaseID, assetId, null);
                    }
                    // Ghi lifecycle event "NEW" cho mỗi tài sản vừa tạo
                    for (long assetId : insertedAssetIds) {
                        assetDao.insertLifecycleEvent(
                                assetId,
                                "NEW", // type
                                null, // oldStatus (không có)
                                "IN_STOCK", // newStatus
                                "Tạo mới tài sản", // reason
                                userId,
                                null, // oldRoomId
                                null // newRoomId
                        );
                    }

                }
            }

            response.sendRedirect(request.getContextPath() + "/assets?action=list&created=" + inserted);
        } catch (SQLException e) {
            Asset assetForForm = buildAssetFromRequestSafe(request);
            AssetCategoryDao categoryDao = new AssetCategoryDao();
            request.setAttribute("asset", assetForForm);
            request.setAttribute("categories", categoryDao.findAllActive());
            request.setAttribute("errorMessage", "Mã tài sản đã tồn tại hoặc dữ liệu không hợp lệ.");
            request.setAttribute("rooms", roomDao.findAllActive());
            request.setAttribute("teachers", userDao.findAllTeachers());
            request.setAttribute("mode", "create");
            request.getRequestDispatcher("/views/asset/asset-create.jsp").forward(request, response);
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
                request.getRequestDispatcher("/views/asset/asset-edit.jsp").forward(request, response);
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
            request.getRequestDispatcher("/views/asset/asset-edit.jsp").forward(request, response);
        }
    }

    private void deleteAsset(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Lấy userId người thực hiện
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            User currentUser = session != null
                    ? (User) session.getAttribute("currentUser") : null;
            long userId = (currentUser != null) ? currentUser.getUserId() : 0;
            assetDao.delete(id);
            // Ghi lifecycle event DELETED
            assetDao.insertLifecycleEvent(
                    id,
                    "DELETED", // type
                    null, // oldStatus
                    "DELETED", // newStatus
                    "Xóa tài sản", // reason
                    userId,
                    null,
                    null
            );
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

    private Asset cloneAsset(Asset src) {
        Asset a = new Asset();
        a.setAssetName(src.getAssetName());
        a.setCategoryId(src.getCategoryId());
        a.setUnit(src.getUnit());
        a.setSerialNumber(src.getSerialNumber());
        a.setModel(src.getModel());
        a.setBrand(src.getBrand());
        a.setOriginNote(src.getOriginNote());
        a.setPurchaseDate(src.getPurchaseDate());
        a.setReceivedDate(src.getReceivedDate());
        a.setConditionNote(src.getConditionNote());
        a.setStatus(src.getStatus());
        a.setCurrentRoomId(src.getCurrentRoomId());
        a.setCurrentHolderId(src.getCurrentHolderId());
        a.setIsActive(src.isIsActive());
        return a;
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

    private Asset buildAssetFromRequestForCreate(HttpServletRequest request) {
        Asset a = new Asset();
        a.setAssetName(request.getParameter("assetName"));
        a.setCategoryId(Long.parseLong(request.getParameter("categoryId")));
        a.setUnit(request.getParameter("unit"));
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

    private final RoomDao roomDao = new RoomDao();
    private final UserDao userDao = new UserDao();

    private String validateAssetForm(HttpServletRequest request, boolean isCreate) throws SQLException {
        String assetName = request.getParameter("assetName");
        String categoryIdStr = request.getParameter("categoryId");
        String roomIdStr = request.getParameter("currentRoomId");
        String holderIdStr = request.getParameter("currentHolderId");
        String pDateStr = request.getParameter("purchaseDate");
        String rDateStr = request.getParameter("receivedDate");
        String serial = request.getParameter("serialNumber");

        // Giới hạn số seri tối đa 10 ký tự
        if (serial != null && serial.trim().length() > 10) {
            return "Số seri tối đa 10 ký tự.";
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
        return null;// hợp lệ
    }

    private String validateAssetFormCreate(HttpServletRequest request) throws SQLException {
        String assetName = request.getParameter("assetName");
        String quantityStr = request.getParameter("quantity");
        String categoryIdStr = request.getParameter("categoryId");
        String roomIdStr = request.getParameter("currentRoomId");
        String holderIdStr = request.getParameter("currentHolderId");
        String pDateStr = request.getParameter("purchaseDate");
        String rDateStr = request.getParameter("receivedDate");
        String serial = request.getParameter("serialNumber");
        // Giới hạn số seri tối đa 10 ký tự
        if (serial != null && serial.trim().length() > 10) {
            return "Số seri tối đa 10 ký tự.";
        }

        if (assetName == null || assetName.trim().isEmpty()) {
            return "Tên tài sản là bắt buộc.";
        }
        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            return "Số lượng là bắt buộc.";
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr.trim());
            if (quantity < 1 || quantity > 1000) {
                return "Số lượng phải từ 1 đến 1000.";
            }
        } catch (NumberFormatException e) {
            return "Số lượng không hợp lệ.";
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

        return null;
    }
}
