package app.core;

import app.model.Usuario;

public class Sesion {
    private static Usuario usuarioActual;

    public static void login(Usuario u) { usuarioActual = u; }
    public static void logout() { usuarioActual = null; }
    public static Usuario getUsuario() { return usuarioActual; }

    public static boolean isLogged() { return usuarioActual != null; }
    public static boolean hasRole(String rol) {
        return isLogged() && rol.equalsIgnoreCase(usuarioActual.getRol());
    }
}