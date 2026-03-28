package controller.staff;

import dao.AssetDAO;
import dao.TransferDAO;
import dao.allocation.AssetStatusHistoryDAO;
import model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.Transfer;

@WebServlet("/transfers/approve")
public class TransferApproveServlet extends HttpServlet {

    private final TransferDAO transferDAO = new TransferDAO();
    private final AssetStatusHistoryDAO assetStatusHistoryDAO = new AssetStatusHistoryDAO();
    private final AssetDAO assetDAO = new AssetDAO();
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Kiểm tra đăng nhập
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Chưa đăng nhập\"}");
            return;
        }

        // Kiểm tra quyền
        boolean isAuthorized = currentUser.getRoles() != null &&
                (currentUser.getRoles().contains("ADMIN") ||
                 currentUser.getRoles().contains("BOARD"));
        if (!isAuthorized) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"success\":false,\"message\":\"Không có quyền phê duyệt\"}");
            return;
        }

        // Validate params
        String idParam      = request.getParameter("id");
        String versionParam = request.getParameter("version");

        if (idParam == null || idParam.trim().isEmpty() ||
            versionParam == null || versionParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Thiếu tham số id hoặc version\"}");
            return;
        }

        try {
            int transferId = Integer.parseInt(idParam.trim());
            int version    = Integer.parseInt(versionParam.trim());

            boolean updated = transferDAO.approveTransfer(transferId, version); 
            Transfer transferInfo = transferDAO.getTransferInfor(transferId);
             List<Integer> listAssetIds = transferInfo.getAssetIds();
             
         
            if (updated) {
                response.getWriter().write("{\"success\":true,\"message\":\"Phê duyệt thành công\"}");
               
            for (Integer assetId : listAssetIds) {

                    String currentAssetStatus = assetDAO.getAssetStatusDetails(
                        assetId,
                        transferInfo.getToRoomId()
                    );

                    assetStatusHistoryDAO.insertStatusHistory(
                        null,
                        assetId,
                        currentAssetStatus,
                        "IN_USE",
                        transferInfo.getReason(),
                        currentUser.getUserId(),
                        "Điều chuyển",
                        Long.valueOf(transferInfo.getFromRoomId()),
                        Long.valueOf(transferInfo.getToRoomId())
                    );
             }
            
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT); // 409
                response.getWriter().write("{\"success\":false,\"message\":\"Phiếu không tồn tại hoặc đã được xử lý bởi người khác\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"ID hoặc version không hợp lệ\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Lỗi server: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}