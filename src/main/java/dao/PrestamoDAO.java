package dao;

import model.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Prestamo.
 * Gestiona los préstamos y devoluciones de libros usando JDBC.
 */
public class PrestamoDAO {

    private Connection con;

    public PrestamoDAO() {
        this.con = ConexionDB.getInstance().getConexion();
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * Registra un nuevo préstamo en la base de datos.
     * También actualiza la disponibilidad del libro a FALSE.
     *
     * @param prestamo objeto con libroId, usuarioId y fechaPrestamo ya cargados
     */
    public void registrar(Prestamo prestamo) throws SQLException {
        // 1. Insertar el préstamo
        String sqlPrestamo = "INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, estado) "
                           + "VALUES (?, ?, ?, 'ACTIVO')";
        try (PreparedStatement ps = con.prepareStatement(sqlPrestamo)) {
            ps.setInt(1, prestamo.getLibroId());
            ps.setInt(2, prestamo.getUsuarioId());
            ps.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            ps.executeUpdate();
        }

        // 2. Marcar el libro como no disponible
        LibroDAO libroDAO = new LibroDAO();
        libroDAO.actualizarDisponibilidad(prestamo.getLibroId(), false);
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    /**
     * Lista todos los préstamos con datos del libro y usuario (JOIN).
     */
    public List<Prestamo> listarTodos() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo AS titulo_libro, u.nombre AS nombre_usuario
            FROM prestamos p
            JOIN libros   l ON p.libro_id   = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            ORDER BY p.fecha_prestamo DESC
            """;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }
        }
        return lista;
    }

    /**
     * Lista los préstamos activos (no devueltos).
     */
    public List<Prestamo> listarActivos() throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo AS titulo_libro, u.nombre AS nombre_usuario
            FROM prestamos p
            JOIN libros   l ON p.libro_id   = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.estado = 'ACTIVO'
            ORDER BY p.fecha_prestamo DESC
            """;
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearPrestamo(rs));
            }
        }
        return lista;
    }

    /**
     * Lista los préstamos de un usuario específico.
     */
    public List<Prestamo> listarPorUsuario(int usuarioId) throws SQLException {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, l.titulo AS titulo_libro, u.nombre AS nombre_usuario
            FROM prestamos p
            JOIN libros   l ON p.libro_id   = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.usuario_id = ?
            ORDER BY p.fecha_prestamo DESC
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPrestamo(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Busca un préstamo por su ID.
     */
    public Prestamo buscarPorId(int id) throws SQLException {
        String sql = """
            SELECT p.*, l.titulo AS titulo_libro, u.nombre AS nombre_usuario
            FROM prestamos p
            JOIN libros   l ON p.libro_id   = l.id
            JOIN usuarios u ON p.usuario_id = u.id
            WHERE p.id = ?
            """;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPrestamo(rs);
                }
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // UPDATE — Devolución
    // -------------------------------------------------------------------------

    /**
     * Registra la devolución de un préstamo activo.
     * Actualiza el estado a DEVUELTO, registra fecha de devolución
     * y restaura la disponibilidad del libro.
     *
     * @param prestamoId ID del préstamo a cerrar
     */
    public void devolver(int prestamoId) throws SQLException {
        // 1. Obtener el préstamo para saber qué libro liberar
        Prestamo prestamo = buscarPorId(prestamoId);
        if (prestamo == null || !prestamo.isActivo()) {
            throw new SQLException("Préstamo no encontrado o ya fue devuelto.");
        }

        // 2. Actualizar estado y fecha de devolución
        String sql = "UPDATE prestamos SET estado='DEVUELTO', fecha_devolucion=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setInt(2, prestamoId);
            ps.executeUpdate();
        }

        // 3. Restaurar disponibilidad del libro
        LibroDAO libroDAO = new LibroDAO();
        libroDAO.actualizarDisponibilidad(prestamo.getLibroId(), true);
    }

    // -------------------------------------------------------------------------
    // Estadísticas
    // -------------------------------------------------------------------------

    /** Cuenta cuántos préstamos activos tiene un usuario */
    public int contarActivosPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE usuario_id=? AND estado='ACTIVO'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Mapea un ResultSet a un objeto Prestamo */
    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Prestamo p = new Prestamo();
        p.setId(rs.getInt("id"));
        p.setLibroId(rs.getInt("libro_id"));
        p.setUsuarioId(rs.getInt("usuario_id"));

        Date fechaPrestamo = rs.getDate("fecha_prestamo");
        if (fechaPrestamo != null) p.setFechaPrestamo(fechaPrestamo.toLocalDate());

        Date fechaDev = rs.getDate("fecha_devolucion");
        if (fechaDev != null) p.setFechaDevolucion(fechaDev.toLocalDate());

        p.setEstado(rs.getString("estado"));

        // Campos del JOIN
        try { p.setTituloLibro(rs.getString("titulo_libro")); } catch (SQLException ignored) {}
        try { p.setNombreUsuario(rs.getString("nombre_usuario")); } catch (SQLException ignored) {}

        return p;
    }
}
