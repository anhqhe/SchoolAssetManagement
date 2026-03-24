package controller.staff;
import dao.TransferDAO;
import model.Transfer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "TransferDetailServlet", urlPatterns = {"/transfers/detail"})
public class TransferDetailServlet extends HttpServlet {
    private final TransferDAO transferDAO = new TransferDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Thiếu tham số id\"}");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw.trim());
            Transfer t = transferDAO.getTransferById(id);
            if (t == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Không tìm thấy phiếu điều chuyển\"}");
                return;
            }

            // Lấy danh sách assetId của phiếu
            List<Integer> assetIds = transferDAO.getAssetIdsByTransferId(id);
            StringBuilder assetIdsJson = new StringBuilder("[");
            for (int i = 0; i < assetIds.size(); i++) {
                if (i > 0) assetIdsJson.append(",");
                assetIdsJson.append(assetIds.get(i));
            }
            assetIdsJson.append("]");

            String json = "{"
                 + "\"transferCode\":"    + jsonStr(t.getTransferCode())    + ","
                 + "\"fromRoomId\":"      + t.getFromRoomId()               + ","
                 + "\"toRoomId\":"        + t.getToRoomId()                 + ","
                 + "\"fromRoomName\":"    + jsonStr(t.getFromRoomName())    + ","
                 + "\"toRoomName\":"      + jsonStr(t.getToRoomName())      + ","
                 + "\"requestedByName\":" + jsonStr(t.getRequestedByName()) + ","
                 + "\"reason\":"          + jsonStr(t.getReason())          + ","
                 + "\"status\":"          + jsonStr(t.getStatus())          + ","
                 + "\"createdAt\":"       + jsonStr(
                                               t.getCreatedAt() != null
                                               ? new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                                                       .format(t.getCreatedAt())
                                               : ""
                                           )                                + ","
                 + "\"assetNames\":"      + jsonStr(t.getAssetNames())      + ","
                 + "\"assetIds\":"        + assetIdsJson                    + ","
                 + "\"version\":"         + t.getVersion()
                 + "}";

            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Id không hợp lệ\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"Lỗi server: " + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String jsonStr(String value) {
        if (value == null) return "\"\"";
        return "\"" + escapeJson(value) + "\"";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}