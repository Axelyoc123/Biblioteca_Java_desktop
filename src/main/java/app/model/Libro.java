// src/main/java/app/model/Libro.java
package app.model;

public class Libro {
    private Integer id;    // null al insertar
    private String nombre;
    private int anio;
    private int idAutor;   // FK a autor.id
    private int idCategoria;
    private int estado;    // 1 = Activo, 0 = Inactivo

    public Libro() {}

    public Libro(Integer id, String nombre, int anio, int idAutor, int idCategoria, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.anio = anio;
        this.idAutor = idAutor;
        this.idCategoria=idCategoria;
        this.estado = estado;
    }

    public Libro(String nombre, int anio, int idAutor, int idCategoria, int estado) {
        this(null, nombre, anio, idAutor,idCategoria, estado);
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public int getAnio() { return anio; }
    public int getIdAutor() { return idAutor; }
    public int getIdCategoria(){return idCategoria;}
    public int getEstado() { return estado; }

    public void setId(Integer id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setIdAutor(int idAutor) { this.idAutor = idAutor; }
    public void setIdCategoria(int idCategoria){this.idCategoria=idCategoria;}
    public void setEstado(int estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Libro{id=" + id + ", nombre='" + nombre + "'}";
    }
}
