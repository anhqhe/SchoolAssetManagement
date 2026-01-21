/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller.auth;

import dao.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author Quang Anh
 */
@WebServlet(name="LoginServlet", urlPatterns={"/auth/login"})
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet LoginServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        try {
            User user = userDAO.authenticate(username, password);
            if (user == null) {
                req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
                req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
                return;
            }

            // Lưu vào session
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30*60); // 30 phút

            // Chuyển hướng tuỳ vai trò (ví dụ)
            if (user.getRoles() != null && user.getRoles().contains("ADMIN")) {
                resp.sendRedirect(req.getContextPath() + "/views/admin/dashboard.jsp");
            } else if (user.getRoles().contains("ASSET_STAFF")) {
                resp.sendRedirect(req.getContextPath() + "/views/admin/dashboard.jsp");
            } else {
                // mặc định teacher => chuyển tới dashboard hoặc trang teacher
                resp.sendRedirect(req.getContextPath() + "/views/admin/dashboard.jsp");
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Có lỗi hệ thống, thử lại.");
            req.getRequestDispatcher("/views/auth/login.jsp").forward(req, resp);
        }
    }
    

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
