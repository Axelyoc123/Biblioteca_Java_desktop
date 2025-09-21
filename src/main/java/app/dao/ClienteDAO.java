package app.dao;

import app.db.Conexion;
import app.model.Categoria;
import app.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // INSERT: crea un Cliente y devuelve el id generado
    public int insertar(Cliente a) throws SQLException {
        String sql = "INSERT INTO Cliente (nombre,nit,telefono, estado) VALUES (?, ?, ?, ?)";//insertamos los datos para la Cliente
        try (Connection con = Conexion.getConnection();
             //preparamos una consulta SQL para insertar nuestros datos y también return generated keys nos sirve para asignar un valor al id automaticamente
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //preparamos las consultas y le mandaremos el nombre y estado para insertar
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getNit());
            ps.setInt(3, a.getTelefono());
            ps.setInt(4, a.getEstado());
            //ejecutamos la declaracion del SQL
            ps.executeUpdate();
            //verificamos si realmente hubo un dato insertad
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setId(id);
                    return id;
                }
            }
        }
        return -1; // no se obtuvo id
    }

    // SELECT *: lista todos los Clientees (últimos primero)
    public List<Cliente> listar() throws SQLException {
        //preparamos nuestra consulta SQL almacenada en una variable
        String sql = "SELECT id, nombre,nit,telefono, estado FROM Cliente WHERE estado=1 ORDER BY id DESC";
        //creamos una lista de arrays donde almacenaremos las Clientes obtenidas
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             //prearamos una consutla SQL con nuestra consulta
             PreparedStatement ps = con.prepareStatement(sql);
             //ejecutaremos la consulta
             ResultSet rs = ps.executeQuery()) {
            //añadiremos lo que almacenamos en nuestro array
            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        }
        return lista;//retornaremos nuestra lista
    }

    // SELECT WHERE id = ?
    public Cliente buscarPorId(int id) throws SQLException {
        //almacenaremos en una variable sql nuestra consulta
        String sql = "SELECT id, nombre,nit,telefono, estado FROM Cliente WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             //preararemos la sentencia sql
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos a nuestro metodo setter el id para buscar por la sentencia
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCliente(rs);
                }
            }
        }
        return null;
    }

    // Buscar por nombre
    public List<Cliente> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre,nit,telefono, estado FROM Cliente WHERE nombre LIKE ? AND estado=1";
        List<Cliente> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCliente(rs));
                }
            }
        }
        return lista;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Cliente a) throws SQLException {
        //agregaremos a nuestro sql la sentencia UPDATE
        String sql = "UPDATE Cliente SET nombre = ?, nit = ?, telefono = ?, estado = ? WHERE id = ?";
        //preparamos nuestra sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos nuestra sentencecia sql a cada parametro del signo de ?
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getNit()); // Correcto: ahora el NIT está en la posición 2
            ps.setInt(3, a.getTelefono()); // Correcto: ahora el teléfono está en la posición 3
            ps.setInt(4, a.getEstado()); // Correcto: el estado está en la posición 4
            ps.setInt(5, a.getId()); // Correcto: el ID está en la posición 5
            //ejecutamos la sentencia para actualizar si se encontro un valor
            return ps.executeUpdate() > 0;
        }
    }

    // Eliminado logico
    public boolean eliminar(int id) throws SQLException {
        //almacenamos en la sentencia sql la consulta elimianr por ID
        String sql = "UPDATE Cliente SET estado = 0 WHERE id = ?";
        //preparamos la sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos en un entero el ID
            ps.setInt(1, id);
            //ejecutamos la entencia si se encontro un valor mayor a 0
            return ps.executeUpdate() > 0;
        }
    }

    //mapea un ResultSet a Cliente
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("nit"),
                rs.getInt("telefono"),
                rs.getInt("estado") // BIT en SQL Server ↔ boolean en Java
        );
    }
}