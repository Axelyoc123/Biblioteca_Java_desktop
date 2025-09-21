package app.model;

public class Cliente {
    private int id;
    private String nombre;
    private String nit;
    private int telefono;
    private int estado;

    //creamos un constructor vacio
    public Cliente(){};
    //creamos nuetro constructor con datos
    public Cliente(int id, String nombre, String nit, int telefono, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.estado = estado;
    }
    //creamos nuestro constructor sin ID para insertar datos
    public Cliente(String nombre, String nit, int telefono, int estado) {
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.estado = estado;
    }

    //creamos nuestros metodos getter y setters

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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", nit='"+ nit + '\''+
                ", telefono='"+ telefono + '\''+
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }
}
