package app.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {


    /*private static final String URL  = "jdbc:sqlserver://rds11g.isbelasoft.com:1433;databaseName=ejemplo_Clase4;encrypt=false";
    private static final String USER = "umg";           // tu usuario
    private static final String PASS = "Umg123";  // tu contraseña*/

    private static final String URL  = "jdbc:sqlserver://localhost:1433;databaseName=BibliotecaDB;encrypt=false";
    private static final String USER = "sa";           // tu usuario
    private static final String PASS = "Dev2025!";  // tu contraseña*/
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Método de prueba
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión exitosa a SQL Server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
