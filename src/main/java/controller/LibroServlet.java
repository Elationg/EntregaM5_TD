package controller;

import dao.LibroDAO;
import model.Libro;
import model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet controlador para la gestión de libros.
 *
 * GET  /libros              → lista todos los libros
 * GET  /libros?accion=nuevo → formulario de alta
 * GET  /libros?accion=editar&id=X → formulario de edición
 * GET  /libros?accion=eliminar&id=X → elimina el libro
 * GET  /libros?buscar=termino → búsqueda por título/autor
 * POST /libros              → guarda nuevo libro o actualiza existente
 */
@WebServlet("/libros")
public class LibroServlet extends HttpServlet {

    private LibroDAO libroDAO;

    @Override
    public void init() {
        libroDAO = new LibroDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Verificar sesión
        if (!sesionActiva(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        String buscar = req.getParameter("buscar");

        try {
            if ("nuevo".equals(accion)) {
                // Mostrar formulario vacío para nuevo libro
                req.setAttribute("libro", new Libro());
                req.setAttribute("modoEdicion", false);
                req.getRequestDispatcher("/libros/formulario.jsp").forward(req, resp);

            } else if ("editar".equals(accion)) {
                // Cargar libro existente para edición
                int id = Integer.parseInt(req.getParameter("id"));
                Libro libro = libroDAO.buscarPorId(id);
                if (libro == null) {
                    resp.sendRedirect(req.getContextPath() + "/libros");
                    return;
                }
                req.setAttribute("libro", libro);
                req.setAttribute("modoEdicion", true);
                req.getRequestDispatcher("/libros/formulario.jsp").forward(req, resp);

            } else if ("eliminar".equals(accion)) {
                // Solo admins pueden eliminar
                if (!esAdmin(req)) {
                    req.setAttribute("errorMsg", "No tenés permisos para eliminar libros.");
                    req.getRequestDispatcher("/error.jsp").forward(req, resp);
                    return;
                }
                int id = Integer.parseInt(req.getParameter("id"));
                libroDAO.eliminar(id);
                resp.sendRedirect(req.getContextPath() + "/libros?mensaje=eliminado");

            } else if (buscar != null && !buscar.isBlank()) {
                // Búsqueda por título o autor
                List<Libro> resultados = libroDAO.buscar(buscar);
                req.setAttribute("libros", resultados);
                req.setAttribute("terminoBusqueda", buscar);
                req.getRequestDispatcher("/libros/lista.jsp").forward(req, resp);

            } else {
                // Listado completo
                List<Libro> libros = libroDAO.listarTodos();
                req.setAttribute("libros", libros);

                // Mensaje de operación exitosa (si viene de un redirect)
                String mensaje = req.getParameter("mensaje");
                if (mensaje != null) req.setAttribute("mensajeExito", getMensaje(mensaje));

                req.getRequestDispatcher("/libros/lista.jsp").forward(req, resp);
            }

        } catch (SQLException e) {
            req.setAttribute("errorMsg", "Error en la base de datos: " + e.getMessage());
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/libros");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!sesionActiva(req)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!esAdmin(req)) {
            req.setAttribute("errorMsg", "Solo administradores pueden modificar el catálogo.");
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }

        req.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        String titulo  = req.getParameter("titulo");
        String autor   = req.getParameter("autor");
        String isbn    = req.getParameter("isbn");
        String categ   = req.getParameter("categoria");
        String anioStr = req.getParameter("anio");

        // Validación básica
        if (titulo == null || titulo.isBlank() || autor == null || autor.isBlank()) {
            req.setAttribute("error", "Título y autor son obligatorios.");
            req.setAttribute("libro", construirLibro(req));
            req.setAttribute("modoEdicion", idParam != null && !idParam.isBlank());
            req.getRequestDispatcher("/libros/formulario.jsp").forward(req, resp);
            return;
        }

        int anio = 0;
        try {
            if (anioStr != null && !anioStr.isBlank()) anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException ignored) {}

        Libro libro = new Libro();
        libro.setTitulo(titulo.trim());
        libro.setAutor(autor.trim());
        libro.setIsbn(isbn != null ? isbn.trim() : "");
        libro.setCategoria(categ != null ? categ.trim() : "");
        libro.setAnio(anio);
        libro.setDisponible(true);

        try {
            if (idParam != null && !idParam.isBlank()) {
                // Actualización
                libro.setId(Integer.parseInt(idParam));
                // Preservar disponibilidad actual
                Libro existente = libroDAO.buscarPorId(libro.getId());
                if (existente != null) libro.setDisponible(existente.isDisponible());
                libroDAO.actualizar(libro);
                resp.sendRedirect(req.getContextPath() + "/libros?mensaje=actualizado");
            } else {
                // Alta nueva
                libroDAO.insertar(libro);
                resp.sendRedirect(req.getContextPath() + "/libros?mensaje=creado");
            }
        } catch (SQLException e) {
            req.setAttribute("error", "Error al guardar: " + e.getMessage());
            req.setAttribute("libro", libro);
            req.setAttribute("modoEdicion", idParam != null && !idParam.isBlank());
            req.getRequestDispatcher("/libros/formulario.jsp").forward(req, resp);
        }
    }

    // ---- Helpers ----

    private boolean sesionActiva(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("usuarioLogueado") != null;
    }

    private boolean esAdmin(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return false;
        Usuario u = (Usuario) session.getAttribute("usuarioLogueado");
        return u != null && u.isAdmin();
    }

    private Libro construirLibro(HttpServletRequest req) {
        Libro l = new Libro();
        l.setTitulo(req.getParameter("titulo"));
        l.setAutor(req.getParameter("autor"));
        l.setIsbn(req.getParameter("isbn"));
        l.setCategoria(req.getParameter("categoria"));
        try { l.setAnio(Integer.parseInt(req.getParameter("anio"))); } catch (Exception ignored) {}
        String id = req.getParameter("id");
        if (id != null && !id.isBlank()) try { l.setId(Integer.parseInt(id)); } catch (Exception ignored) {}
        return l;
    }

    private String getMensaje(String codigo) {
        return switch (codigo) {
            case "creado"    -> "Libro agregado exitosamente al catálogo.";
            case "actualizado" -> "Libro actualizado correctamente.";
            case "eliminado"   -> "Libro eliminado del catálogo.";
            default            -> "";
        };
    }
}
