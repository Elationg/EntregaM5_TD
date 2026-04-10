package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Clase Singleton para gestionar la conexión a la base de datos H2.
 * Garantiza una única instancia de conexión en toda la aplicación.
 * Implementa el patrón Singleton + auto-inicialización del esquema.
 */
public class ConexionDB {

    // URL H2 en modo embebido con archivo persistente en el home del usuario
    private static final String URL      = "jdbc:h2:~/biblioteca_untec;AUTO_SERVER=TRUE";
    private static final String USUARIO  = "sa";
    private static final String PASSWORD = "";

    private static ConexionDB instancia;
    private Connection conexion;

    /** Constructor privado: carga el driver H2 e inicializa el esquema */
    private ConexionDB() {
        try {
            Class.forName("org.h2.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            inicializarEsquema();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver H2 no encontrado.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con H2.", e);
        }
    }

    /**
     * Devuelve la única instancia de ConexionDB (Singleton).
     * Si la conexión fue cerrada o es inválida, la recrea.
     */
    public static ConexionDB getInstance() {
        try {
            if (instancia == null || instancia.conexion.isClosed()) {
                instancia = new ConexionDB();
            }
        } catch (SQLException e) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    /** Devuelve el objeto Connection activo */
    public Connection getConexion() {
        return conexion;
    }

    /**
     * Crea las tablas si no existen e inserta datos de ejemplo.
     * Se ejecuta una sola vez al primer arranque de la aplicación.
     */
    private void inicializarEsquema() throws SQLException {
        try (Statement stmt = conexion.createStatement()) {

            // Tabla usuarios
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS usuarios (
                    id        INT AUTO_INCREMENT PRIMARY KEY,
                    nombre    VARCHAR(100) NOT NULL,
                    email     VARCHAR(100) UNIQUE NOT NULL,
                    password  VARCHAR(100) NOT NULL,
                    rol       VARCHAR(20)  NOT NULL DEFAULT 'ESTUDIANTE'
                )
            """);

            // Tabla libros
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS libros (
                    id          INT AUTO_INCREMENT PRIMARY KEY,
                    titulo      VARCHAR(200) NOT NULL,
                    autor       VARCHAR(100) NOT NULL,
                    isbn        VARCHAR(20)  UNIQUE,
                    categoria   VARCHAR(50),
                    anio        INT,
                    disponible  BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);

            // Tabla prestamos
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS prestamos (
                    id                INT AUTO_INCREMENT PRIMARY KEY,
                    libro_id          INT  NOT NULL,
                    usuario_id        INT  NOT NULL,
                    fecha_prestamo    DATE NOT NULL,
                    fecha_devolucion  DATE,
                    estado            VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
                    FOREIGN KEY (libro_id)   REFERENCES libros(id),
                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                )
            """);

            // Datos de ejemplo: usuarios por defecto
            stmt.execute("""
                MERGE INTO usuarios (id, nombre, email, password, rol)
                KEY(id)
                VALUES
                  (1, 'Administrador', 'admin@untec.edu',       'admin123',   'ADMIN'),
                  (2, 'Juan Pérez',    'juan@untec.edu',         'student123', 'ESTUDIANTE'),
                  (3, 'María López',   'maria@untec.edu',        'student123', 'ESTUDIANTE')
            """);

            // Datos de ejemplo: catálogo inicial de libros
            stmt.execute("""
                MERGE INTO libros (id, titulo, autor, isbn, categoria, anio, disponible)
                KEY(id)
                VALUES
                  (1, 'Clean Code',                         'Robert C. Martin',  '978-0132350884', 'Programación',  2008, TRUE),
                  (2, 'El Quijote',                         'Miguel de Cervantes','978-8491050773', 'Literatura',    1605, TRUE),
                  (3, 'Introducción a los Algoritmos',      'Cormen et al.',      '978-0262033848', 'Informática',   2009, TRUE),
                  (4, 'Patrones de Diseño',                 'Gang of Four',       '978-0201633610', 'Programación',  1994, TRUE),
                  (5, 'Java: The Complete Reference',       'Herbert Schildt',    '978-1260440232', 'Programación',  2019, TRUE),
                  (6, 'Base de Datos: Diseño y Aplicación', 'Ramez Elmasri',      '978-0133970777', 'Bases de Datos',2015, TRUE)
            """);
        }
    }
}
