package app.dao;

import app.db.Conexion;
import app.model.Categoria;
import app.model.Prestamo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // INSERT: crea un Prestamo y devuelve el id generado
    public int insertar(Prestamo a) throws SQLException {
        //insertamos los datos para la Prestamo
        String sql = "INSERT INTO Prestamo (idCliente, idLibro, fecha, estado) VALUES (?, ?, ?, ?)";
        //preparamos una consulta SQL para insertar nuestros datos y también return generated keys nos sirve para asignar un valor al id automaticamente
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            //preparamos las consultas y le mandaremos los datos para insertar
            ps.setInt(1, a.getIdCliente());
            ps.setInt(2, a.getIdLibro());
            ps.setDate(3, a.getFecha()); // Asegúrate de que a.getFecha() no sea null
            ps.setInt(4, a.getEstado());
            //ejecutamos la declaracion del SQL
            ps.executeUpdate();
            //verificamos si realmente hubo un dato insertad
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    a.setId(id); // Asignamos el ID generado al objeto Prestamo
                    return id;
                }
            }
        }
        return -1; // no se obtuvo id
    }

    // SELECT *: lista todos los Prestamoes (últimos primero)
    public List<Prestamo> listar() throws SQLException {
        //preparamos nuestra consulta SQL almacenada en una variable
        String sql = "SELECT id, idCliente, idLibro, fecha, estado FROM Prestamo where estado=1 ORDER BY id DESC";
        //creamos una lista de arrays donde almacenaremos las Prestamos obtenidas
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             //prearamos una consutla SQL con nuestra consulta
             PreparedStatement ps = con.prepareStatement(sql);
             //ejecutaremos la consulta
             ResultSet rs = ps.executeQuery()) {
            //añadiremos lo que almacenamos en nuestro array
            while (rs.next()) {
                lista.add(mapPrestamo(rs));
            }
        }
        return lista;//retornaremos nuestra lista
    }

    // SELECT WHERE id = ?
    public Prestamo buscarPorId(int id) throws SQLException {
        //almacenaremos en una variable sql nuestra consulta
        String sql = "SELECT id, idCliente, idLibro, fecha, estado FROM Prestamo WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             //preararemos la sentencia sql
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos a nuestro metodo setter el id para buscar por la sentencia
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPrestamo(rs);
                }
            }
        }
        return null;
    }

    // Buscar por nombre de Prestamo
    public List<Prestamo> buscarPorCliente(int cliente) throws SQLException {
        String sql = "SELECT id, idCliente, idLibro, fecha, estado FROM Prestamo WHERE idCliente=?";
        List<Prestamo> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1,cliente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapPrestamo(rs));
                }
            }
        }
        return lista;
    }


    //Metodo para validar si el libro ya fue pretado
    public boolean libroPrestadoInsert(int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Prestamo WHERE idLibro = ? AND estado = 1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Metodo libro ya prestado para actualizar
    public boolean libroPrestamoActualizacion(int idLibro, int idPrestamoActual) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Prestamo WHERE idLibro = ? AND estado = 1 AND id != ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            ps.setInt(2, idPrestamoActual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Prestamo a) throws SQLException {
        //agregaremos a nuestro sql la sentencia UPDATE
        String sql = "UPDATE Prestamo SET idCliente = ?, idLibro = ?, fecha = ?, estado = ? WHERE id = ?";
        //preparamos nuestra sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos nuestra sentencecia sql a cada parametro del signo de ?
            ps.setInt(1, a.getIdCliente());
            ps.setInt(2, a.getIdLibro());
            ps.setDate(3, a.getFecha()); // Asegúrate de que a.getFecha() no sea null
            ps.setInt(4, a.getEstado());
            ps.setInt(5, a.getId()); // Cláusula WHERE
            //ejecutamos la sentencia para actualizar si se encontro un valor
            return ps.executeUpdate() > 0;
        }
    }

    // Eliminado logico para prestamos
    public boolean eliminar(int id) throws SQLException {
       //realizamos la consulta SQL
        String sql = "UPDATE Prestamo SET estado = 0 WHERE id = ?";
        //preparamos la sentencia sql
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            //enviaremos en un entero el ID
            ps.setInt(1, id);
            //ejecutamos la entencia si se encontro un valor mayor a 0
            return ps.executeUpdate() > 0;
        }
    }

    //mapea un ResultSet a Prestamo
    private Prestamo mapPrestamo(ResultSet rs) throws SQLException {
        return new Prestamo(
                rs.getInt("id"),
                rs.getInt("idCliente"),
                rs.getInt("idLibro"),
                rs.getDate("fecha"),
                rs.getInt("estado")
        );
    }
}