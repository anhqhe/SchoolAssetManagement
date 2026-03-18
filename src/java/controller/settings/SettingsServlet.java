package controller.settings;

import dao.SystemConfigDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import model.User;

@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
public class SettingsServlet extends HttpServlet {

    private final SystemConfigDAO systemConfigDAO = new SystemConfigDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*
         * Màn hình cài đặt hệ thống (UI config):
         * - Primary color
         * - Banner toàn hệ thống
         *
         * Lưu ý: hiện AuthFilter đang cho phép mọi user đăng nhập vào /settings,
         * nhưng về mặt nghiệp vụ nên giới hạn ADMIN (hoặc role tương đương).
         */
        try {
            Map<String, String> configs = systemConfigDAO.getAll();

            request.setAttribute("uiPrimaryColor", defaultIfBlank(configs.get(SystemConfigDAO.KEY_UI_PRIMARY_COLOR), "#4e73df"));

            String bannerEnabledRaw = defaultIfBlank(configs.get(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_ENABLED), "false");
            boolean bannerEnabled = "true".equalsIgnoreCase(bannerEnabledRaw) || "1".equals(bannerEnabledRaw);
            request.setAttribute("uiBannerEnabled", bannerEnabled);
            request.setAttribute("uiBannerText", defaultIfBlank(configs.get(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_TEXT), ""));

            request.getRequestDispatcher("/views/settings/settings.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Không thể tải cài đặt hệ thống.");
            request.getRequestDispatcher("/views/common/500.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        /*
         * Lưu cấu hình UI:
         * - Validate format màu (#RRGGBB)
         * - Banner text chỉ lưu khi bật banner
         *
         * Lưu ý bảo mật:
         * - Form POST nên có CSRF token.
         * - Dữ liệu bannerText khi render ra HTML phải escape để tránh XSS.
         */
        String primaryColor = trimToNull(request.getParameter("uiPrimaryColor"));
        String bannerEnabledParam = trimToNull(request.getParameter("uiBannerEnabled"));
        String bannerText = trimToNull(request.getParameter("uiBannerText"));

        if (primaryColor == null || !primaryColor.matches("^#[0-9a-fA-F]{6}$")) {
            primaryColor = "#4e73df";
        }
        boolean bannerEnabled = "on".equalsIgnoreCase(bannerEnabledParam) || "true".equalsIgnoreCase(bannerEnabledParam) || "1".equals(bannerEnabledParam);
        if (!bannerEnabled) {
            bannerText = "";
        }

        // Dùng cho audit (UpdatedById). Nếu không lấy được userId thì lưu 0.
        long updatedById = getCurrentUserId(request.getSession(false));

        try {
            systemConfigDAO.upsert(SystemConfigDAO.KEY_UI_PRIMARY_COLOR, primaryColor, updatedById);
            systemConfigDAO.upsert(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_ENABLED, String.valueOf(bannerEnabled), updatedById);
            systemConfigDAO.upsert(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_TEXT, bannerText != null ? bannerText : "", updatedById);

            response.sendRedirect(request.getContextPath() + "/settings?success=1");
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Không thể lưu cài đặt hệ thống.");
            request.getRequestDispatcher("/views/common/500.jsp").forward(request, response);
        }
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String defaultIfBlank(String value, String def) {
        if (value == null) {
            return def;
        }
        String t = value.trim();
        return t.isEmpty() ? def : t;
    }

    private static long getCurrentUserId(HttpSession session) {
        if (session == null) {
            return 0L;
        }
        Object obj = session.getAttribute("currentUser");
        if (!(obj instanceof User)) {
            return 0L;
        }
        User user = (User) obj;
        try {
            return user.getUserId();
        } catch (Exception ignored) {
            return 0L;
        }
    }
}

