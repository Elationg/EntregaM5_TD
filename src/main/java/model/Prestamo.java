package model;

import java.time.LocalDate;

/**
 * Modelo que representa un préstamo de un libro a un usuario.
 * Estado puede ser: ACTIVO o DEVUELTO.
 */
public class Prestamo {

    private int id;
    private int libroId;
    private int usuarioId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado; // "ACTIVO" o "DEVUELTO"

    // Campos enriquecidos para mostrar en vistas (joins)
    private String tituloLibro;
    private String nombreUsuario;

    public Prestamo() {}

    public Prestamo(int id, int libroId, int usuarioId,
                    LocalDate fechaPrestamo, LocalDate fechaDevolucion, String estado) {
        this.id = id;
        this.libroId = libroId;
        this.usuarioId = usuarioId;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.estado = estado;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTituloLibro() { return tituloLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    /** Indica si el préstamo aún está activo (no devuelto) */
    public boolean isActivo() {
        return "ACTIVO".equalsIgnoreCase(this.estado);
    }

    @Override
    public String toString() {
        return "Prestamo{id=" + id + ", libroId=" + libroId
                + ", usuarioId=" + usuarioId + ", estado='" + estado + "'}";
    }
}
