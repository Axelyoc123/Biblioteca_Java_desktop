package app.dao;

import app.db.Conexion;
import app.model.Libro;
import app.model.LibroConAutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    //creamos un metodo par insertar la información de libros
    public int insertar(Libro l) throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "INSERT INTO libro (nombre, anio, idAutor, idCategoria, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, l.getNombre());
            ps.setInt(2, l.getAnio());
            ps.setInt(3, l.getIdAutor());
            ps.setInt(4, l.getIdCategoria());
            ps.setInt(5, l.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    l.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
    //creamos un metodo para actualizar la información
    public boolean actualizar(Libro l) throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "UPDATE libro SET nombre=?, anio=?, idAutor=?, idCategoria=?, estado=? WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, l.getNombre());
            ps.setInt(2, l.getAnio());
            ps.setInt(3, l.getIdAutor());
            ps.setInt(4, l.getIdCategoria());
            ps.setInt(5, l.getEstado());
            ps.setInt(6, l.getId());
            return ps.executeUpdate() > 0;
        }
    }
    //eliminado logico
    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE Libro SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Método para listar todos los libros activos
    public List<Libro> listar() throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "SELECT id, nombre, anio, idAutor, idCategoria, estado FROM libro WHERE estado = 1 ORDER BY id DESC";
        List<Libro> libros = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(mapLibro(rs));
            }
        }
        return libros;
    }

    public Libro buscarPorId(int id) throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "SELECT id, nombre, anio, idAutor,idCategoria, estado FROM libro WHERE id=?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLibro(rs);
                }
            }
        }
        return null;
    }

    // Busca libros por nombre
    public List<Libro> buscarPorNombre(String nombre) throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "SELECT id, nombre, anio, idAutor, idCategoria, estado FROM libro WHERE nombre LIKE ? AND estado = 1";
        List<Libro> libros = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapLibro(rs));
                }
            }
        }
        return libros;
    }

    // Busca libros por ID de categoría
    public List<Libro> buscarPorCategoria(int categoria) throws SQLException {
        //creamos nuestra consulta sql y la almacenamos en la variable sql
        String sql = "SELECT id, nombre, anio, idAutor,idCategoria, estado FROM libro WHERE idCategoria = ? AND estado = 1";
        List<Libro> libros = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1,categoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapLibro(rs));
                }
            }
        }
        return libros;
    }

    // Método de ayuda para mapear un ResultSet a un objeto Libro
    private Libro mapLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getInt("anio"),
                rs.getInt("idAutor"),
                rs.getInt("idCategoria"),
                rs.getInt("estado")
        );
    }

    // Lista con JOIN para mostrar el nombre del autor en la tabla
    public List<LibroConAutor> listarConAutor() throws SQLException {
        String sql = """
                SELECT l.id, l.nombre, l.anio, a.nombre AS autorNombre, l.estado
                FROM libro l
                JOIN autor a ON a.id = l.idAutor
                ORDER BY l.id DESC
                """;
        List<LibroConAutor> data = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.add(new LibroConAutor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("anio"),
                        rs.getString("autorNombre"),
                        rs.getInt("estado")
                ));
            }
        }
        return data;
    }
}