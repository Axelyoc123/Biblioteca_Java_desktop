package app.model;
import java.sql.Date;
public class Prestamo {
    private int id;
    private int idCliente;
    private int idLibro;
    private Date fecha;
    private int estado;

    //creamos nuestro constructor vacio
    public Prestamo(){}
    //creamos nuestro constructor

    public Prestamo(int id, int idCliente, int idLibro, Date fecha, int estado) {
        this.id = id;
        this.idCliente = idCliente;
        this.idLibro = idLibro;
        this.fecha = fecha;
        this.estado = estado;
    }

    //creamos nuestro contructor sin id para insertar datos

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    @Override
    public String toString() {
        return "Prestamo{" +
                "id=" + id +
                ", idCliente='" + idCliente + '\'' +
                ", idLibro='"+ idLibro + '\''+
                ", fecha='"+ fecha + '\''+
                ", estado=" + (estado == 1 ? "Activo" : "Inactivo") +
                '}';
    }
}
