package app.view;

import app.dao.UsuarioDAO;
import app.model.Usuario;
import app.model.ComboItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UsuariosForm {
    public JPanel panelPrincipal;
    private JTextField txtUsername;
    private JTextField txtNombre;
    private JTextField txtPassword;
    private JComboBox<ComboItem> cboRol;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCargar;
    private JTable tbUsuarios;
    private JButton btnBuscarUsername; // Nuevo botón

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Username", "Nombre", "Password", "Rol", "Estado"}, 0
    );
    private Integer selectedId = null;

    public UsuariosForm() {
        panelPrincipal.setPreferredSize(new Dimension(900, 600));
        tbUsuarios.setModel(model);
        tbUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        cargarRolesEnCombo();

        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscarUsername.addActionListener(e -> onBuscarPorUsername()); // Nuevo listener

        tbUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tbUsuarios.getSelectedRow();
            if (row == -1) {
                selectedId = null;
                limpiarFormulario();
                return;
            }

            selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtUsername.setText(model.getValueAt(row, 1).toString());
            txtNombre.setText(model.getValueAt(row, 2).toString());
            txtPassword.setText(model.getValueAt(row, 3).toString());

            String rolNombre = model.getValueAt(row, 4).toString();
            seleccionarRolPorNombre(rolNombre);

            String estadoTxt = model.getValueAt(row, 5).toString();
            cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estadoTxt) ? 0 : 1);
        });

        cargarTabla();
    }

    private void cargarRolesEnCombo() {
        try {
            cboRol.removeAllItems();
            cboRol.addItem(new ComboItem(1, "Admin"));
            cboRol.addItem(new ComboItem(2, "Operador"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void seleccionarRolPorNombre(String nombre) {
        for (int i = 0; i < cboRol.getItemCount(); i++) {
            ComboItem item = cboRol.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboRol.setSelectedIndex(i);
                return;
            }
        }
    }

    private void onGuardar() {
        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        String password = txtPassword.getText().trim();
        ComboItem rolItem = (ComboItem) cboRol.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        if (username.isEmpty() || nombre.isEmpty() || password.isEmpty() || rolItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Todos los campos son obligatorios.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            usuarioDAO.crearUsuario(username, password, nombre, rolItem.getLabel(), estado);
            limpiarFormulario();
            cargarTabla();
            JOptionPane.showMessageDialog(panelPrincipal, "Usuario guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onActualizar() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un usuario de la tabla para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String username = txtUsername.getText().trim();
        String nombre = txtNombre.getText().trim();
        ComboItem rolItem = (ComboItem) cboRol.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        if (username.isEmpty() || nombre.isEmpty() || rolItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Los campos username, nombre y rol son obligatorios.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Usuario usuarioActualizado = new Usuario(selectedId, username, nombre, null, rolItem.getLabel(), estado);
            boolean ok = usuarioDAO.actualizar(usuarioActualizado);
            if (ok) {
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Usuario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Elimina un Usuario (baja lógica)
    private void onEliminar() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un usuario para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea eliminar este usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = usuarioDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Usuario eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al eliminar el usuario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para buscar un usuario por su nombre de usuario
    private void onBuscarPorUsername() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un nombre de usuario para buscar.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        try {
            List<Usuario> usuarios = usuarioDAO.buscarPorUsername(username);
            model.setRowCount(0);
            if (usuarios.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron usuarios con ese nombre de usuario.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Usuario u : usuarios) {
                    model.addRow(new Object[]{
                            u.getId(),
                            u.getUsername(),
                            u.getNombre(),
                            "********", // Ocultamos la contraseña
                            u.getRol(),
                            u.getEstado() == 1 ? "Activo" : "Inactivo"
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar usuarios: " + ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        try {
            List<Usuario> lista = usuarioDAO.listar();
            model.setRowCount(0);
            for (Usuario u : lista) {
                model.addRow(new Object[]{
                        u.getId(),
                        u.getUsername(),
                        u.getNombre(),
                        "********", // Ocultamos la contraseña por seguridad
                        u.getRol(),
                        u.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar la tabla de usuarios.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtUsername.setText("");
        txtNombre.setText("");
        txtPassword.setText("");
        if (cboRol.getItemCount() > 0) cboRol.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tbUsuarios.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tbUsuarios.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Usuarios");
            f.setContentPane(new UsuariosForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}