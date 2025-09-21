// src/main/java/app/dao/AutorDAO.java
package app.dao;

import app.db.Conexion;
import app.model.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AutorDAO {

    // INSERT: crea un autor y devuelve el id generado
    public int insertar(Autor a) throws SQLException {
        String sql = "INSERT INTO autor (nombre, biografia, estado) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getNombre());
            ps.setString(2, a.getBiografia());
            ps.setInt(3, a.getEstado());

            ps.executeUpdate();

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

    // SELECT *: lista todos los autores (últimos primero)
    public List<Autor> listar() throws SQLException {
        String sql = "SELECT id, nombre, biografia, estado FROM autor WHERE estado=1 ORDER BY id DESC";
        List<Autor> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapAutor(rs));
            }
        }
        return lista;
    }

    // SELECT WHERE id = ?
    public Autor buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT id, nombre, biografia, estado FROM autor WHERE nombre = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1,nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAutor(rs);
                }
            }
        }
        return null;
    }

    public Autor buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, biografia, estado FROM autor WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAutor(rs);
                }
            }
        }
        return null;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Autor a) throws SQLException {
        String sql = "UPDATE autor SET nombre = ?, biografia = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, a.getNombre());
            ps.setString(2, a.getBiografia());
            ps.setInt(3, a.getEstado());
            ps.setInt(4, a.getId());

            return ps.executeUpdate() > 0;
        }
    }

    // Eliminado logico
    public boolean eliminar(int id) throws SQLException {
        //preparamos la consulta SQL para actualizar
        String sql = "UPDATE Autor SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // mapea un ResultSet a Autor
    private Autor mapAutor(ResultSet rs) throws SQLException {
        return new Autor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("biografia"),
                rs.getInt("estado") // BIT en SQL Server ↔ boolean en Java
        );
    }
}
