// src/main/java/app/model/LibroConAutor.java
package app.model;

public class LibroConAutor {
    private final int id;
    private final String nombre;
    private final int anio;
    private final String autorNombre;
    private final int estado;

    public LibroConAutor(int id, String nombre, int anio, String autorNombre, int estado) {
        this.id = id; this.nombre = nombre; this.anio = anio;
        this.autorNombre = autorNombre; this.estado = estado;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getAnio() { return anio; }
    public String getAutorNombre() { return autorNombre; }
    public int getEstado() { return estado; }
}
