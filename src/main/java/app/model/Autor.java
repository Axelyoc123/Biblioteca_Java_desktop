// src/main/java/app/model/Autor.java
package app.model;

public class Autor {
    private Integer id;        // puede ser null al insertar
    private String nombre;
    private String biografia;
    private Integer estado;

    // Constructor vacío (necesario para frameworks y utilidades)
    public Autor() {}

    // Constructor con todos los campos
    public Autor(Integer id, String nombre, String biografia, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.biografia = biografia;
        this.estado = estado;
    }

    // Constructor sin ID (para cuando se va a insertar)
    public Autor(String nombre, String biografia, int estado) {
        this(null, nombre, biografia, estado);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }
}
