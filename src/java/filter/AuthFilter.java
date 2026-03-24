package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.SystemConfigDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import model.User;

@WebFilter(
        filterName = "AuthFilter",
        urlPatterns = {
            "/admin/*",
            "/staff/*",
            "/teacher/*",
            "/board/*",
            "/transfers/*",
            "/settings",
            "/change-password",
            "/profile",
            "/profile/*"
        }
)
public class AuthFilter implements Filter {
    private final SystemConfigDAO systemConfigDAO = new SystemConfigDAO();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User currentUser = session != null ? (User) session.getAttribute("currentUser") : null;

        // Chưa đăng nhập -> chuyển về trang login
        if (currentUser == null) {
            res.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        List<String> roles = currentUser.getRoles();
        if (roles == null) {
            roles = Collections.emptyList();
        }

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length()); // vd: /admin/dashboard

        boolean allowed = true;

        // Chỉ ADMIN được vào một số trang quản trị sâu
        if (path.startsWith("/admin/user")
                || path.startsWith("/admin/reports")) {
            allowed = roles.contains("ADMIN");
        }

        // Nhóm ASSET_STAFF (và BOARD +  ADMIN) cho các URL quản lý tài sản
        else if ( path.startsWith("/staff/")
                || path.startsWith("/transfers/")) {
            allowed = roles.contains("ASSET_STAFF") || roles.contains("ADMIN") || roles.contains("BOARD");
        }

        // Nhóm TEACHER
        else if (path.startsWith("/teacher/")) {
            allowed = roles.contains("TEACHER") || roles.contains("ADMIN");
        }

        // Nhóm BOARD
        else if (path.startsWith("/board/")) {
            allowed = roles.contains("BOARD") || roles.contains("ADMIN");
        }

        // /admin/dashboard, /change-password, ...: chỉ cần đăng nhập là được

        if (!allowed) {
            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
            req.setAttribute("errorMessage", "Bạn không có quyền truy cập trang này.");
            req.getRequestDispatcher("/views/common/403.jsp").forward(req, res);
            return;
        }

        // Load UI config (theme/banner) để layout/JSP có thể áp dụng ngay.
        // Mặc định dùng theo sb-admin-2 nếu config không tồn tại hoặc không hợp lệ.
        try {
            Map<String, String> configs = systemConfigDAO.getAll();

            String primary = configs.get(SystemConfigDAO.KEY_UI_PRIMARY_COLOR);
            if (primary == null || !primary.matches("^#[0-9a-fA-F]{6}$")) {
                primary = "#4e73df";
            }
            req.setAttribute("uiPrimaryColor", primary);

            String bannerEnabledRaw = configs.get(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_ENABLED);
            boolean bannerEnabled = "true".equalsIgnoreCase(bannerEnabledRaw) || "1".equals(bannerEnabledRaw);
            req.setAttribute("uiBannerEnabled", bannerEnabled);

            String bannerText = configs.get(SystemConfigDAO.KEY_UI_GLOBAL_BANNER_TEXT);
            req.setAttribute("uiBannerText", bannerText != null ? bannerText : "");
        } catch (SQLException ignored) {
            req.setAttribute("uiPrimaryColor", "#4e73df");
            req.setAttribute("uiBannerEnabled", false);
            req.setAttribute("uiBannerText", "");
        }

        chain.doFilter(request, response);
    }
}

