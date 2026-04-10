package controller;

import dao.LibroDAO;
import dao.PrestamoDAO;
import dao.UsuarioDAO;
import model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet del dashboard principal.
 * Muestra estadísticas generales de la biblioteca.
 * Requiere sesión activa.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private LibroDAO    libroDAO;
    private UsuarioDAO  usuarioDAO;
    private PrestamoDAO prestamoDAO;

    @Override
    public void init() {
        libroDAO    = new LibroDAO();
        usuarioDAO  = new UsuarioDAO();
        prestamoDAO = new PrestamoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Verificar sesión activa
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Estadísticas para el dashboard
            int totalLibros       = libroDAO.listarTodos().size();
            int librosDisponibles = libroDAO.listarDisponibles().size();
            int totalUsuarios     = usuarioDAO.listarTodos().size();
            int prestamosActivos  = prestamoDAO.listarActivos().size();

            req.setAttribute("totalLibros",       totalLibros);
            req.setAttribute("librosDisponibles", librosDisponibles);
            req.setAttribute("totalUsuarios",     totalUsuarios);
            req.setAttribute("prestamosActivos",  prestamosActivos);

            req.getRequestDispatcher("/dashboard.jsp").forward(req, resp);

        } catch (SQLException e) {
            req.setAttribute("errorMsg", "Error al cargar el dashboard: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        }
    }
}
