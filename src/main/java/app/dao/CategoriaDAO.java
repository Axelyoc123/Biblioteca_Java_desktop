package app.dao;

import app.db.Conexion;
import app.model.Autor;
import app.model.Categoria;
import app.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    // INSERT: crea un Categoria y devuelve el id generado
    public int insertar(Categoria a) throws SQLException {
        String sql = "INSERT INTO Categoria (nombre, estado) VALUES (?, ?)";//insertamos los datos para la categoria
        try (Connection con = Conexion.getConnection();
             //preparamos una consulta SQL para insertar nuestros datos y también return generated keys nos sirve para asignar un valor al id automaticamente
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //preparamos las consultas y le mandaremos el nombre y estado para insertar
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getEstado());
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

    // SELECT *: lista todos los Categoriaes (últimos primero)
    public List<Categoria> listar() throws SQLException {
        //preparamos nuestra consulta SQL almacenada en una variable
        String sql = "SELECT id, nombre, estado FROM Categoria WHERE estado=1 ORDER BY id DESC";
        //creamos una lista de arrays donde almacenaremos las categorias obtenidas
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             //prearamos una consutla SQL con nuestra consulta
             PreparedStatement ps = con.prepareStatement(sql);
             //ejecutaremos la consulta
             ResultSet rs = ps.executeQuery()) {
            //añadiremos lo que almacenamos en nuestro array
            while (rs.next()) {
                lista.add(mapCategoria(rs));
            }
        }
        return lista;//retornaremos nuestra lista
    }

    // SELECT WHERE id = ?
    public Categoria buscarPorId(int id) throws SQLException {
        //almacenaremos en una variable sql nuestra consulta
        String sql = "SELECT id, nombre, estado FROM Categoria WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             //preararemos la sentencia sql
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos a nuestro metodo setter el id para buscar por la sentencia
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCategoria(rs);
                }
            }
        }
        return null;
    }

    // Buscar por nombre de categoria
    public List<Categoria> buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, estado FROM Categoria WHERE nombre LIKE ? AND estado=1";
        List<Categoria> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCategoria(rs));
                }
            }
        }
        return lista;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Categoria a) throws SQLException {
        //agregaremos a nuestro sql la sentencia UPDATE
        String sql = "UPDATE Categoria SET nombre = ?, estado = ? WHERE id = ?";
        //preparamos nuestra sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos nuestra sentencecia sql a cada parametro del signo de ?
            ps.setString(1, a.getNombre());
            ps.setInt(2, a.getEstado());
            ps.setInt(3, a.getId());
            //ejecutamos la sentencia para actualizar si se encontro un valor
            return ps.executeUpdate() > 0;
        }
    }

    // Eliminado logico
    public boolean eliminar(int id) throws SQLException {
        //almacenamos en la sentencia sql la consulta elimianr por ID
        String sql = "UPDATE Categoria SET estado = 0 WHERE id = ?";
        //preparamos la sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos en un entero el ID
            ps.setInt(1, id);
            //ejecutamos la entencia si se encontro un valor mayor a 0
            return ps.executeUpdate() > 0;
        }
    }

    //mapea un ResultSet a Categoria
    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getInt("estado") // BIT en SQL Server ↔ boolean en Java
        );
    }
}