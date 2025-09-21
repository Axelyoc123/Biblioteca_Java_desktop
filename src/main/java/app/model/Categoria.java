package app.model;

public class Categoria {
    //creamos nuestros atributos y variantes
    private int id;
    private String nombre;
    private int estado;

    //creamos un contructor vacio
    public Categoria(){}

    //creamos un constructor con todas las clases

    public Categoria(int id, String nombre, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.estado = estado;
    }

    //creamos un contructor vacio cuando vayamos a realizer un insert
    public Categoria(String nombre, int estado) {
        this.nombre = nombre;
        this.estado = estado;
    }
    //creamos nuestros getter y setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }

}
