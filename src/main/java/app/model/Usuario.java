package app.model;

public class Usuario {
    private final Integer id;
    private final String username;
    private final String nombre;
    private final String password; // ¡Este es el campo que faltaba!
    private final String rol;
    private final int estado;

    // Constructor para operaciones de actualización y creación desde el formulario
    public Usuario(Integer id, String username, String nombre, String password, String rol, int estado) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
    }

    // Constructor que usa el DAO para obtener datos de la base de datos (sin password)
    public Usuario(Integer id, String username, String nombre, String rol, int estado) {
        this(id, username, nombre, null, rol, estado); // Llama al constructor principal con password = null
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getNombre() { return nombre; }
    public String getPassword() { return password; } // Agregado
    public String getRol() { return rol; }
    public int getEstado() { return estado; }
}