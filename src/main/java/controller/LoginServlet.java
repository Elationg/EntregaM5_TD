package controller;

import dao.UsuarioDAO;
import model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet controlador del flujo de autenticación.
 *
 * GET  /login  → muestra el formulario de login (index.jsp)
 * POST /login  → valida credenciales y redirige según resultado
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() {
        usuarioDAO = new UsuarioDAO();
    }

    /**
     * GET /login → redirige a index.jsp si no hay sesión activa,
     * o al dashboard si el usuario ya está autenticado.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("usuarioLogueado") != null) {
            resp.sendRedirect(req.getContextPath() + "/dashboard");
        } else {
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }

    /**
     * POST /login → procesa el formulario de login.
     * Si las credenciales son válidas crea sesión y redirige al dashboard.
     * Si son inválidas, vuelve al login con mensaje de error.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email    = req.getParameter("email");
        String password = req.getParameter("password");

        // Validación básica de campos vacíos
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            req.setAttribute("error", "Por favor completá todos los campos.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(email, password);

            if (usuario != null) {
                // Crear sesión y guardar el usuario logueado
                HttpSession session = req.getSession(true);
                session.setAttribute("usuarioLogueado", usuario);
                session.setMaxInactiveInterval(30 * 60); // 30 minutos
                resp.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                req.setAttribute("error", "Email o contraseña incorrectos.");
                req.getRequestDispatcher("/index.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            req.setAttribute("error", "Error del sistema. Intente nuevamente.");
            req.getRequestDispatcher("/index.jsp").forward(req, resp);
        }
    }
}
