package app.view;

import app.core.Sesion;
import app.dao.UsuarioDAO;
import app.model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm {
    public JPanel panelPrincipal;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JButton btnSalir; // Added btnSalir to your class fields
    private JLabel lblStatus;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginForm() {
        panelPrincipal.setPreferredSize(new Dimension(360, 200));

        // Assigns an action to the 'Enter' button
        btnEntrar.addActionListener(e -> onEntrar());

        // Assigns an action to the 'Exit' button
        btnSalir.addActionListener(e -> onSalir());

        // Adds a key listener to the password field to trigger login on Enter press
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onEntrar();
                }
            }
        });
    }

    private void onEntrar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty()) {
           JOptionPane.showMessageDialog(panelPrincipal,"El campo usuario es obligatorio","Campos Obligatorios",JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,"El campo contraseña es obligatorio","Campos Obligatorios",JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Usuario u = usuarioDAO.validarLogin(user, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(panelPrincipal,"El usuario o contraseña son incorrectas","Credenciales Incorrectas",JOptionPane.ERROR_MESSAGE);
                return;
            }
            Sesion.login(u);
            abrirMenu();
            // Closes the login window after a successful login
            SwingUtilities.getWindowAncestor(panelPrincipal).dispose();
        } catch (Exception ex) {
            if (lblStatus != null) lblStatus.setText("Error de conexión");
            ex.printStackTrace();
        }
    }

    // metodo para salir del formulario
    private void onSalir() {
        // mostrarmos mensaje de confirmacion para cerrar
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Está seguro que desea salir?", "Salir",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Closes the entire application
            System.exit(0);
        }
    }
    //crearemos el metodo para abri el menu y poder loguearnos
    private void abrirMenu() {
        JFrame f = new JFrame("Menú Principal – Librería");
        f.setContentPane(new MainMenuForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Launcher
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Login");
            f.setContentPane(new LoginForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}