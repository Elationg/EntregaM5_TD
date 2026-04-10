package model;

/**
 * Modelo que representa un libro en la biblioteca digital.
 * Contiene los atributos básicos de un libro y sus getters/setters.
 */
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String categoria;
    private int anio;
    private boolean disponible;

    public Libro() {}

    public Libro(int id, String titulo, String autor, String isbn,
                 String categoria, int anio, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.categoria = categoria;
        this.anio = anio;
        this.disponible = disponible;
    }

    // Getters y Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", titulo='" + titulo + "', autor='" + autor + "'}";
    }
}
