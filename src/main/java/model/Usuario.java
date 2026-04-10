package model;

/**
 * Modelo que representa un usuario del sistema.
 * Puede tener rol ADMIN o ESTUDIANTE.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private String rol; // "ADMIN" o "ESTUDIANTE"

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String password, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    /** Verifica si el usuario tiene rol de administrador */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.rol);
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nombre='" + nombre + "', rol='" + rol + "'}";
    }
}
