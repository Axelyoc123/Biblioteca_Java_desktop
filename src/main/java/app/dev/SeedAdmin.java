package app.dev;

import app.dao.UsuarioDAO;

public class SeedAdmin {
    public static void main(String[] args) throws Exception {
        UsuarioDAO dao = new UsuarioDAO();
        int id = dao.crearUsuario("admin", "admin123", "Administrador", "ADMIN",1);
        System.out.println("Admin creado id=" + id);
    }
}