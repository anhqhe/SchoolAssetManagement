package controller.admin.room;

import dao.RoomDAO;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.Room;
import model.User;

@WebServlet(name = "RoomAssignTeacherServlet", urlPatterns = {"/rooms/assign-teacher"})
public class RoomAssignTeacherServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        List<String> roles = (currentUser != null) ? currentUser.getRoles() : null;
        if (roles == null || !roles.contains("ADMIN")) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String roomIdParam = req.getParameter("roomId");
        if (roomIdParam == null || roomIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/rooms?assignError=invalid_room");
            return;
        }

        long roomId;
        try {
            roomId = Long.parseLong(roomIdParam.trim());
        } catch (NumberFormatException ex) {
            resp.sendRedirect(req.getContextPath() + "/rooms?assignError=invalid_room");
            return;
        }

        try {
            Room room = roomDAO.getRoomById(roomId);
            if (room == null) {
                resp.sendRedirect(req.getContextPath() + "/rooms?assignError=room_not_found");
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/rooms?assignError=db");
            return;
        }

        String teacherIdParam = req.getParameter("teacherId");
        Long teacherId = null;
        if (teacherIdParam != null && !teacherIdParam.trim().isEmpty()) {
            try {
                teacherId = Long.parseLong(teacherIdParam.trim());
            } catch (NumberFormatException ex) {
                resp.sendRedirect(req.getContextPath() + "/rooms?assignError=invalid_teacher");
                return;
            }
        }

        try {
            if (teacherId != null) {
                final long selectedTeacherId = teacherId.longValue();
                boolean teacherExists = userDAO.getAllTeachers().stream()
                        .anyMatch(t -> t.getUserId() == selectedTeacherId);
                if (!teacherExists) {
                    resp.sendRedirect(req.getContextPath() + "/rooms?assignError=teacher_not_found");
                    return;
                }
            }

            roomDAO.setPrimaryTeacherForRoom(roomId, teacherId);
            resp.sendRedirect(req.getContextPath() + "/rooms?assign=success");
        } catch (SQLException ex) {
            ex.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/rooms?assignError=db");
        }
    }
}
