package app.view;

import app.dao.AutorDAO;
import app.model.Autor;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AutorForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextArea txtBiografia;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnCargar;
    private JTable tblAutores;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnBuscarNombre;
    private JLabel Autores;

    //crearemos nuestro constructor para Autor
    private final AutorDAO autorDAO = new AutorDAO();
    //crearemos el objeto para almacenar la información de la tabla
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Biografía", "Estado"}, 0
    );

    // guardamos el ID seleccionado para actualizar
    private Integer selectedId = null;

    public AutorForm() {
        panelPrincipal.setPreferredSize(new Dimension(900, 600));

        tblAutores.setModel(model);
        tblAutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // craremos nuestro comboBox para mostrar activo o inactivo
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");
        cboEstado.setSelectedIndex(0);

        // crearemos nuestros botónes donde ejecutaran una accion al darle click
        btnGuardar.addActionListener(e -> onGuardar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnActualizar.addActionListener(e -> onActualizar());
        btnEliminar.addActionListener(e->onEliminar());
        btnBuscarNombre.addActionListener(e -> onBuscarPorNombre());

        // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
        tblAutores.getSelectionModel().addListSelectionListener(this::onTableSelection);
    }

    private void onTableSelection(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = tblAutores.getSelectedRow();
        if (row == -1) {
            selectedId = null;
            return;
        }
        // nos ayudara a leeer los datos que estan en la tabla
        Object idVal   = model.getValueAt(row, 0);
        Object nomVal  = model.getValueAt(row, 1);
        Object bioVal  = model.getValueAt(row, 2);
        Object estVal  = model.getValueAt(row, 3);

        // Guardamos el id seleccionado
        selectedId = (idVal != null) ? Integer.parseInt(idVal.toString()) : null;

        // Cargamos la información en los campos txt
        txtNombre.setText(nomVal != null ? nomVal.toString() : "");
        txtBiografia.setText(bioVal != null ? bioVal.toString() : "");

        //hacemos la condicional para rellear los comboBox segun si estaba activo o inactivo
        if (estVal != null && estVal.toString().equalsIgnoreCase("Activo")) {
            cboEstado.setSelectedIndex(0); // 1
        } else if (estVal != null && estVal.toString().equalsIgnoreCase("Inactivo")) {
            cboEstado.setSelectedIndex(1); // 0
        }
    }
    //crearemos nuestro metodo para el botón al momento que se le de click en guardar
    private void onGuardar() {
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        String bio    = txtBiografia.getText().trim();
        int estado    = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,"El campo Nombre es oblitagorio.","Campo obligatorio",JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        //hacemos la verificacion si el campo bio esta vacio
        if(bio.isEmpty()){
            JOptionPane.showMessageDialog(panelPrincipal,"El campo Biografia es oblitagorio.","Campo obligatorio",JOptionPane.WARNING_MESSAGE);
            txtBiografia.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor donde le pasaremos la información del nombre, bio, estado
            Autor a = new Autor(nombre, bio, estado);
            //llamamos a nuestro metodo insertar y le pasamos el constructor
            autorDAO.insertar(a);
            //limpiamos los txt
            limpiarFormulario();
            //mostramos los datos en la tabla
            cargarTabla();
            selectedId = null;
            JOptionPane.showMessageDialog(panelPrincipal, "Autor guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar el autor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onActualizar() {
        //veriicamos si hay una fila seleccionada
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un autor para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //almacenaremos los datos de nuestro txt
        String nombre = txtNombre.getText().trim();
        String bio    = txtBiografia.getText().trim();
        int estado    = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        //verificaremos si nuestros datos estan vacios
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,"El campo Nombre es oblitagorio.","Campo obligatorio",JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        if(bio.isEmpty()){
            JOptionPane.showMessageDialog(panelPrincipal,"El campo Biografia es oblitagorio.","Campo obligatorio",JOptionPane.WARNING_MESSAGE);
            txtBiografia.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor para actualizar la información por medio del update
            Autor a = new Autor(selectedId, nombre, bio, estado);
            boolean ok = autorDAO.actualizar(a);
            if (ok) {
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Autor actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar el autor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblAutores.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    //Creaermos nuestro botón de eliminar
    private void onEliminar() {
        //validaremos si hay un dato seleccionado si no mostraremos un mensaje de alerta
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione una categoría para eliminar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //mostrara una ventana de confirmación para eliminar el dato
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea eliminar el Autor?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                //utilizaremos nuestro constructor para eliminar un id cambiando el estado a 0
                boolean ok = autorDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Autor eliminado con exito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al eliminar un autor: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //mostraremos los datos del autor en la tabla
    private void cargarTabla() {
        try {
            List<Autor> lista = autorDAO.listar();
            model.setRowCount(0);
            for (Autor a : lista) {
                model.addRow(new Object[]{
                        a.getId(),
                        a.getNombre(),
                        a.getBiografia(),
                        a.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onBuscarPorNombre() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un nombre para buscar.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            Autor autor = autorDAO.buscarPorNombre(nombre);
            model.setRowCount(0);
            if (autor != null) {
                model.addRow(new Object[]{
                        autor.getId(),
                        autor.getNombre(),
                        autor.getBiografia(),
                        autor.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            } else {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontró ningún autor con ese nombre.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar el autor: " + ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }
    //creamos metodo par limpiar el formulario
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtBiografia.setText("");
        cboEstado.setSelectedIndex(0);
        tblAutores.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Autores");
            f.setContentPane(new AutorForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}