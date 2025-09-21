package app.view;

import app.core.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {
    //componentes de la interfaz del panel
    public JPanel panelPrincipal;
    private JButton btnAutores;
    private JButton btnLibros;
    private JButton btnClientes; // Agregado
    private JButton btnCategorias; // Agregado
    private JButton btnPrestamos; // Agregado
    private JButton btnSalir;
    private JButton btnUsuario;
    private JLabel lblUsuario;

    public MainMenuForm() {
        panelPrincipal.setPreferredSize(new Dimension(500, 300)); // Se ajusta el tamaño

        // Muestra el nombre y el rol del usuario logueado
        if (lblUsuario != null && Sesion.isLogged()) {
            lblUsuario.setText("Usuario: " + Sesion.getUsuario().getNombre()
                    + " (" + Sesion.getUsuario().getRol() + ")");
        }

        // bloqueamos el boton Usuario para que solo el usuario administrador pueda ingresar
        if (btnUsuario != null) {
            btnUsuario.setEnabled(Sesion.hasRole("ADMIN"));
        }
        // bloqueamos el boton Categoria para que solo el usuario administrador pueda ingresar
        if (btnCategorias != null) {
            btnCategorias.setEnabled(Sesion.hasRole("ADMIN")); // Las categorías solo para ADMIN
        }

        // Asigna las acciones a los botones
        if (btnAutores != null) btnAutores.addActionListener(e -> abrirAutores());
        if (btnLibros != null) btnLibros.addActionListener(e -> abrirLibros());
        if (btnClientes != null) btnClientes.addActionListener(e -> abrirClientes());
        if (btnCategorias != null) btnCategorias.addActionListener(e -> abrirCategorias());
        if (btnPrestamos != null) btnPrestamos.addActionListener(e -> abrirPrestamos());
        if(btnUsuario!=null) btnUsuario.addActionListener(e->abrirUsuario());
        if(btnSalir != null) btnSalir.addActionListener(e ->CerrarForm());
    }
    private void abrirAutores() {
        JFrame f = new JFrame("Gestión de Autores");
        f.setContentPane(new AutorForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void CerrarForm(){
        // mostrarmos mensaje de confirmacion para cerrar
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Está seguro que desea salir?", "Salir",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Closes the entire application
            System.exit(0);
        }
    }

    private void abrirUsuario() {
        JFrame f = new JFrame("Gestión de Usuarios");
        f.setContentPane(new UsuariosForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void abrirLibros() {
        JFrame f = new JFrame("Gestión de Libros");
        f.setContentPane(new LibroForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Método para abrir el menú de Clientes
    private void abrirClientes() {
        JFrame f = new JFrame("Gestión de Clientes");
        f.setContentPane(new ClienteForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Método para abrir el menú de Categorías
    private void abrirCategorias() {
        JFrame f = new JFrame("Gestión de Categorías");
        f.setContentPane(new CategoriaForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Método para abrir el menú de Préstamos
    private void abrirPrestamos() {
        JFrame f = new JFrame("Gestión de Préstamos");
        f.setContentPane(new PrestamoForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}