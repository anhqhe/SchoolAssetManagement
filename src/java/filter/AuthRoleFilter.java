package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;
import java.util.List;

/**
 * Filter kiểm tra đăng nhập + phân quyền theo role cho từng nhóm URL.
 *
 * Các Role chính (theo sidebar):
 *  - ADMIN
 *  - ASSET_STAFF
 *  - TEACHER
 *  - BOARD
 *
 * Có thể chỉnh sửa mapping trong hàm isAuthorized() cho phù hợp nghiệp vụ.
 */
@WebFilter(filterName = "AuthRoleFilter", urlPatterns = {"/*"})
public class AuthRoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String contextPath = req.getContextPath();
        String uri = req.getRequestURI();
        String path = uri.substring(contextPath.length()); // vd: /auth/login, /assets/list, /views/admin/dashboard.jsp

        // 1. BYPASS cho static assets & trang public
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. Kiểm tra đã đăng nhập chưa
        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            // Chưa login -> chuyển về trang login
            resp.sendRedirect(contextPath + "/auth/login");
            return;
        }

        // 3. Phân quyền theo role cho từng URL
        List<String> roles = currentUser.getRoles();

        if (!isAuthorized(path, roles)) {
            // Không đủ quyền
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vào đường dẫn này.");
            return;
        }

        // 4. Hợp lệ -> cho đi tiếp
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    /**
     * Các path không yêu cầu đăng nhập / role:
     *  - Tài nguyên tĩnh: /assets, /vendor, .css, .js, .png, ...
     *  - Trang login, forgot-password, reset-password, index
     */
    private boolean isPublicPath(String path) {
        // Static resources
        if (path.startsWith("/assets/") ||
            path.startsWith("/vendor/") ||
            path.startsWith("/webjars/")) {
            return true;
        }

        // File tĩnh theo đuôi (phòng trường hợp mapping khác)
        if (path.endsWith(".css") || path.endsWith(".js") ||
            path.endsWith(".png") || path.endsWith(".jpg") ||
            path.endsWith(".jpeg") || path.endsWith(".gif") ||
            path.endsWith(".svg") || path.endsWith(".ico")) {
            return true;
        }

        // Trang / servlet auth
        if (path.equals("/") ||
            path.equals("/index.jsp") ||
            path.startsWith("/auth/") ||                      // /auth/login, /auth/logout
            path.startsWith("/views/auth/")) {                // login.jsp, forgot-password.jsp, reset-password.jsp
            return true;
        }

        // Trang lỗi chung (nếu có)
        if (path.startsWith("/views/common/")) {
            return true;
        }

        return false;
    }

    /**
     * Mapping URL -> các role được phép truy cập.
     * Có thể tuỳ chỉnh thêm/giảm role cho từng nhóm đường dẫn.
     */
    private boolean isAuthorized(String path, List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        // -------- ADMIN AREA ----------
        // Các trang quản trị & tài sản
        if (path.startsWith("/admin/") ||
            path.startsWith("/assets/") ||
            path.startsWith("/views/admin/")) {
            // Chỉ ADMIN và ASSET_STAFF được vào khu vực quản trị / quản lý tài sản
            return hasAnyRole(roles, "ADMIN", "ASSET_STAFF");
        }

        // -------- TEACHER AREA ----------
        // Trang cho giáo viên
        if (path.startsWith("/teacher/") ||
            path.startsWith("/views/allocation/teacher/") ||
            path.startsWith("/allocation/teacher/")) {
            // Giáo viên hoặc Admin (nếu muốn Admin có toàn quyền)
            return hasAnyRole(roles, "TEACHER", "ADMIN");
        }

        // -------- ASSET_STAFF AREA ----------
        // Khu vực dành cho nhân viên tài sản
        if (path.startsWith("/allocation/staff/") ||
            path.startsWith("/views/allocation/staff/")) {
            return hasAnyRole(roles, "ASSET_STAFF", "ADMIN");
        }

        // -------- BOARD AREA ----------
        // Khu vực dành cho Ban giám hiệu / Board
        if (path.startsWith("/board/") ||
            path.startsWith("/allocation/board/") ||
            path.startsWith("/views/allocation/board/")) {
            return hasAnyRole(roles, "BOARD", "ADMIN");
        }

        // -------- COMMON LOGGED-IN AREA ----------
        // Các đường dẫn khác: chỉ cần đang đăng nhập là được
        return true;
    }

    private boolean hasAnyRole(List<String> roles, String... allowed) {
        for (String a : allowed) {
            if (roles.contains(a)) {
                return true;
            }
        }
        return false;
    }
}


