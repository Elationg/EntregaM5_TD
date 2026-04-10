package dao;

import model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para la entidad Libro.
 * Implementa operaciones CRUD sobre la tabla "libros" usando JDBC.
 */
public class LibroDAO {

    private Connection con;

    public LibroDAO() {
        this.con = ConexionDB.getInstance().getConexion();
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    /**
     * Inserta un nuevo libro en la base de datos.
     * @param libro objeto Libro con los datos a insertar
     */
    public void insertar(Libro libro) throws SQLException {
        String sql = "INSERT INTO libros (titulo, autor, isbn, categoria, anio, disponible) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getCategoria());
            ps.setInt(5, libro.getAnio());
            ps.setBoolean(6, libro.isDisponible());
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    /**
     * Devuelve todos los libros del catálogo.
     */
    public List<Libro> listarTodos() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros ORDER BY titulo";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearLibro(rs));
            }
        }
        return lista;
    }

    /**
     * Devuelve solo los libros disponibles para préstamo.
     */
    public List<Libro> listarDisponibles() throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE disponible = TRUE ORDER BY titulo";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearLibro(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un libro por su ID.
     * @return Libro encontrado, o null si no existe
     */
    public Libro buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM libros WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca libros por título o autor (búsqueda parcial, case-insensitive).
     */
    public List<Libro> buscar(String termino) throws SQLException {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros WHERE LOWER(titulo) LIKE ? OR LOWER(autor) LIKE ?";
        String patron = "%" + termino.toLowerCase() + "%";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearLibro(rs));
                }
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    /**
     * Actualiza los datos de un libro existente.
     */
    public void actualizar(Libro libro) throws SQLException {
        String sql = "UPDATE libros SET titulo=?, autor=?, isbn=?, categoria=?, anio=?, disponible=? "
                   + "WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getCategoria());
            ps.setInt(5, libro.getAnio());
            ps.setBoolean(6, libro.isDisponible());
            ps.setInt(7, libro.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Cambia el estado de disponibilidad de un libro.
     * Se usa al registrar o devolver un préstamo.
     */
    public void actualizarDisponibilidad(int libroId, boolean disponible) throws SQLException {
        String sql = "UPDATE libros SET disponible=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, disponible);
            ps.setInt(2, libroId);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    /**
     * Elimina un libro por su ID.
     * Solo se debe invocar si el libro no tiene préstamos activos.
     */
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM libros WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /** Mapea un ResultSet a un objeto Libro */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro();
        libro.setId(rs.getInt("id"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setIsbn(rs.getString("isbn"));
        libro.setCategoria(rs.getString("categoria"));
        libro.setAnio(rs.getInt("anio"));
        libro.setDisponible(rs.getBoolean("disponible"));
        return libro;
    }
}
