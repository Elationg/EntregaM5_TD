package controller;

import dao.LibroDAO;
import dao.PrestamoDAO;
import dao.UsuarioDAO;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet controlador para la gestión de préstamos.
 *
 * GET  /prestamos                   → lista todos los préstamos
 * GET  /prestamos?accion=nuevo      → formulario de nuevo préstamo
 * GET  /prestamos?accion=devolver&id=X → registra devolución
 * POST /prestamos                   → registra un nuevo préstamo
 */
@WebServlet("/prestamos")
public class PrestamoServlet extends HttpServlet {

    private PrestamoDAO prestamoDAO;
    private LibroDAO    libroDAO;
    private UsuarioDAO  usuarioDAO;

    @Override
    public void init() {
        prestamoDAO = new PrestamoDAO();
        libroDAO    = new LibroDAO();
        usuarioDAO  = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!sesionActiva(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");

        try {
            if ("nuevo".equals(accion)) {
                // Cargar listas para el formulario
                List<Libro>   librosDisp = libroDAO.listarDisponibles();
                List<Usuario> usuarios   = usuarioDAO.listarTodos();
                req.setAttribute("librosDisponibles", librosDisp);
                req.setAttribute("usuarios", usuarios);
                req.getRequestDispatcher("/prestamos/formulario.jsp").forward(req, resp);

            } else if ("devolver".equals(accion)) {
                // Solo admins pueden registrar devoluciones
                if (!esAdmin(req)) {
                    req.setAttribute("errorMsg", "Solo administradores pueden gestionar devoluciones.");
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
                int id = Integer.parseInt(req.getParameter("id"));
                prestamoDAO.devolver(id);
                resp.sendRedirect(req.getContextPath() + "/prestamos?mensaje=devuelto");

            } else {
                // Listar préstamos (admin ve todos, estudiante ve solo los suyos)
                List<Prestamo> prestamos;
                if (esAdmin(req)) {
                    prestamos = prestamoDAO.listarTodos();
                } else {
                    Usuario u = getUsuarioLogueado(req);
                    prestamos = prestamoDAO.listarPorUsuario(u.getId());
                }
                req.setAttribute("prestamos", prestamos);

                String mensaje = req.getParameter("mensaje");
                if (mensaje != null) req.setAttribute("mensajeExito", getMensaje(mensaje));

                req.getRequestDispatcher("/prestamos/lista.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            req.setAttribute("errorMsg", "Error en base de datos: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/prestamos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!sesionActiva(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");

        String libroIdStr   = req.getParameter("libroId");
        String usuarioIdStr = req.getParameter("usuarioId");

        try {
            // Validación de campos
            if (libroIdStr == null || libroIdStr.isBlank()
                    || usuarioIdStr == null || usuarioIdStr.isBlank()) {
                req.setAttribute("error", "Debe seleccionar libro y usuario.");
                cargarFormulario(req);
                req.getRequestDispatcher("/prestamos/formulario.jsp").forward(req, resp);
                return;
            }

            int libroId   = Integer.parseInt(libroIdStr);
            int usuarioId = Integer.parseInt(usuarioIdStr);

            // Verificar que el libro siga disponible
            Libro libro = libroDAO.buscarPorId(libroId);
            if (libro == null || !libro.isDisponible()) {
                req.setAttribute("error", "El libro seleccionado ya no está disponible.");
                cargarFormulario(req);
                req.getRequestDispatcher("/prestamos/formulario.jsp").forward(req, resp);
                return;
            }

            Prestamo prestamo = new Prestamo();
            prestamo.setLibroId(libroId);
            prestamo.setUsuarioId(usuarioId);
            prestamo.setFechaPrestamo(LocalDate.now());
            prestamo.setEstado("ACTIVO");

            prestamoDAO.registrar(prestamo);
            resp.sendRedirect(req.getContextPath() + "/prestamos?mensaje=registrado");

        } catch (SQLException e) {
            req.setAttribute("error", "Error al registrar préstamo: " + e.getMessage());
            try { cargarFormulario(req); } catch (SQLException ex) { /* ignorar */ }
            req.getRequestDispatcher("/prestamos/formulario.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/prestamos");
        }
    }

    // ---- Helpers ----

    private void cargarFormulario(HttpServletRequest req) throws SQLException {
        req.setAttribute("librosDisponibles", libroDAO.listarDisponibles());
        req.setAttribute("usuarios", usuarioDAO.listarTodos());
    }

    private boolean sesionActiva(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("usuarioLogueado") != null;
    }

    private boolean esAdmin(HttpServletRequest req) {
        Usuario u = getUsuarioLogueado(req);
        return u != null && u.isAdmin();
    }

    private Usuario getUsuarioLogueado(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return null;
        return (Usuario) session.getAttribute("usuarioLogueado");
    }

    private String getMensaje(String codigo) {
        return switch (codigo) {
            case "registrado" -> "Préstamo registrado correctamente.";
            case "devuelto"   -> "Devolución registrada. El libro ya está disponible.";
            default           -> "";
        };
    }
}
