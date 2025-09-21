package app.view;

import app.dao.CategoriaDAO;
import app.model.Categoria;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CategoriaForm {
    // Componentes de la interfaz gráfica
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnCargar;
    private JTable tblCategorias;
    private JButton btnEliminar;
    private JButton btnBuscarNombre;

    // Conectores a la base de datos y modelos
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    // Modelo para la tabla, define las columnas
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Estado"}, 0
    );

    // Variable para almacenar el ID del elemento seleccionado en la tabla
    private Integer selectedId = null;

    // Constructor de la clase
    public CategoriaForm() {
        // Configuración inicial de la interfaz
        panelPrincipal.setPreferredSize(new Dimension(600, 400));
        tblCategorias.setModel(model);
        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Llenar el ComboBox de estado
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        // Asignar acciones a los botones usando expresiones lambda
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscarNombre.addActionListener(e -> onBuscarPorNombre());

        // Listener para la selección de filas en la tabla
        tblCategorias.getSelectionModel().addListSelectionListener(this::onTableSelection);
    }
    // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
    private void onTableSelection(ListSelectionEvent e) {
        // Evitar que el evento se dispare dos veces
        if (e.getValueIsAdjusting()) return;

        int row = tblCategorias.getSelectedRow();
        // Si no hay fila seleccionada, limpiar el formulario y el ID
        if (row == -1) {
            selectedId = null;
            limpiarFormulario();
            return;
        }

        // nos ayudara a leeer los datos que estan en la tabla
        Object idVal   = model.getValueAt(row, 0);
        Object nomVal  = model.getValueAt(row, 1);
        Object estVal  = model.getValueAt(row, 2);

        // Guardamos el id seleccionado
        selectedId = (idVal != null) ? Integer.parseInt(idVal.toString()) : null;

        // Cargamos la información en los campos txt
        txtNombre.setText(nomVal != null ? nomVal.toString() : "");

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
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor donde le pasaremos la información del nombre, estado
            Categoria categoria = new Categoria(nombre, estado);
            //llamamos a nuestro metodo insertar y le pasamos el constructor
            categoriaDAO.insertar(categoria);
            //limpiamos los txt
            limpiarFormulario();
            //mostramos los datos en la tabla
            cargarTabla();
            JOptionPane.showMessageDialog(panelPrincipal, "Categoría guardada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar la categoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos nuestro metodo para el botón al momento que se le de click en actualizar
    private void onActualizar() {
        //veriicamos si hay una fila seleccionada
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione una categoría de la tabla para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor para actualizar la información por medio del update
            Categoria categoria = new Categoria(selectedId, nombre, estado);
            boolean ok = categoriaDAO.actualizar(categoria);
            if (ok) {
                //mostramos los datos en la tabla
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Categoría actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar la categoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea eliminar esta categoría?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                //utilizaremos nuestro constructor para eliminar un id cambiando el estado a 0
                boolean ok = categoriaDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Categoría eliminada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al eliminar la categoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //mostraremos los datos de la categoria en la tabla
    private void cargarTabla() {
        try {
            List<Categoria> lista = categoriaDAO.listar();
            model.setRowCount(0); // Limpiar todas las filas de la tabla
            // Iterar sobre la lista y agregar cada categoría como una nueva fila
            for (Categoria c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar la tabla de categorías.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos el método buscar por nombre
    private void onBuscarPorNombre() {
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un nombre para buscar.", "Error de búsqueda", JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            List<Categoria> lista = categoriaDAO.buscarPorNombre(nombre);
            model.setRowCount(0); // Limpiar todas las filas de la tabla

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron categorías con ese nombre.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
            // Iterar sobre la lista y agregar cada categoría como una nueva fila
            for (Categoria c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar categorías: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //creamos metodo par limpiar el formulario
    private void limpiarFormulario() {
        txtNombre.setText("");
        cboEstado.setSelectedIndex(0);
        tblCategorias.clearSelection();
        selectedId = null;
    }
    //crearemos el metodo seleccionar la fila por el ID
    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblCategorias.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    // Método principal que ejecuta la aplicación Swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Categorías");
            // Se crea una nueva instancia de la clase Categoria para mostrar su panel
            f.setContentPane(new CategoriaForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}