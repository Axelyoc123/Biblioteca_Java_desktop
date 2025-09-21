package app.dao;

import app.core.PasswordUtil;
import app.db.Conexion;
import app.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    /** Crea un usuario: guarda el HASH en la columna 'password' */
    public int crearUsuario(String username, String plainPassword, String nombre, String rol, int estado) throws SQLException {
        String sql = "INSERT INTO usuario (username, password, nombre, rol, estado) VALUES (?, ?, ?, ?, ?)";
        String hash = PasswordUtil.hash(plainPassword);

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, nombre);
            ps.setString(4, rol);
            ps.setInt(5, estado);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    /** Actualiza un usuario. Devuelve true si la operación fue exitosa. */
    public boolean actualizar(Usuario u) throws SQLException {
        // En la actualización, no se cambia la contraseña a menos que se requiera un método específico para ello.
        String sql = "UPDATE usuario SET username = ?, nombre = ?, rol = ?, estado = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getRol());
            ps.setInt(4, u.getEstado());
            ps.setInt(5, u.getId());

            return ps.executeUpdate() > 0;
        }
    }

    /** Realiza una baja lógica de un usuario, cambiando su estado a 0 (inactivo). */
    public boolean eliminar(int id) throws SQLException {
        String sql = "UPDATE usuario SET estado = 0 WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Lista todos los usuarios (activos e inactivos). */
    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT id, username, nombre, password, rol, estado FROM usuario WHERE estado=1 ORDER BY id DESC";
        List<Usuario> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapUsuario(rs));
            }
        }
        return lista;
    }
    /** Busca usuarios por un nombre de usuario. */
    public List<Usuario> buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT id, username, nombre, password, rol, estado FROM usuario WHERE username LIKE ? AND estado=1";
        List<Usuario> lista = new ArrayList<>();
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + username + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapUsuario(rs));
                }
            }
        }
        return lista;
    }

    /** Valida el login de un usuario. */
    public Usuario validarLogin(String username, String plainPassword) throws SQLException {
        String sql = "SELECT id, username, password, nombre, rol, estado " +
                "FROM usuario WHERE username=? AND estado=1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String hash = rs.getString("password");
                boolean ok = PasswordUtil.verify(plainPassword, hash);
                if (!ok) return null;

                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nombre"),
                        rs.getString("rol"),
                        rs.getInt("estado")
                );
            }
        }
    }

    /**
     * mapea un ResultSet a un objeto Usuario.
     */
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("nombre"),
                rs.getString("rol"),
                rs.getInt("estado")
        );
    }
}