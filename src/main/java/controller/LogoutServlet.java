package controller;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * Servlet que maneja el cierre de sesión del usuario.
 * GET /logout → invalida la sesión y redirige al login.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate(); // destruye todos los atributos de sesión
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
