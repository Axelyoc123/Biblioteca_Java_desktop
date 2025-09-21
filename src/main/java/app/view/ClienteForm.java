package app.view;

import app.dao.ClienteDAO;
import app.model.Cliente;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClienteForm {
    // Declaración de los componentes de la interfaz gráfica para la gestión de clientes
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtNIT;
    private JTextField txtTelefono;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnCargar;
    private JTable tblClientes;
    private JButton btnEliminar;
    private JButton btnBuscarNombre;

    // Instancia del objeto DAO para la interacción con la base de datos de clientes
    private final ClienteDAO clienteDAO = new ClienteDAO();
    // Modelo de la tabla para mostrar los datos de los clientes
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "NIT", "Teléfono", "Estado"}, 0
    );
    // Variable para almacenar el ID del cliente seleccionado
    private Integer selectedId = null;

    // Constructor de la clase Cliente
    public ClienteForm() {
        // Configuración inicial de la ventana y la tabla
        panelPrincipal.setPreferredSize(new Dimension(700, 500));
        tblClientes.setModel(model);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Llenar el ComboBox para el estado del cliente (Activo/Inactivo)
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        // Asignar los listeners de eventos a cada botón
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscarNombre.addActionListener(e -> onBuscarPorNombre());

        // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
        tblClientes.getSelectionModel().addListSelectionListener(this::onTableSelection);

        // Cargar los datos en la tabla al iniciar la aplicación
        cargarTabla();
    }
    // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
    private void onTableSelection(ListSelectionEvent e) {
        // Evitar que el evento se dispare dos veces
        if (e.getValueIsAdjusting()) return;

        int row = tblClientes.getSelectedRow();
        // Si no hay fila seleccionada, limpiar el formulario y el ID
        if (row == -1) {
            selectedId = null;
            limpiarFormulario();
            return;
        }

        // nos ayudara a leeer los datos que estan en la tabla
        Object idVal   = model.getValueAt(row, 0);
        Object nomVal  = model.getValueAt(row, 1);
        Object nitVal  = model.getValueAt(row, 2);
        Object telVal  = model.getValueAt(row, 3);
        Object estVal  = model.getValueAt(row, 4);

        // Guardamos el id seleccionado
        selectedId = (idVal != null) ? Integer.parseInt(idVal.toString()) : null;

        // Cargamos la información en los campos txt
        txtNombre.setText(nomVal != null ? nomVal.toString() : "");
        txtNIT.setText(nitVal != null ? nitVal.toString() : "");
        txtTelefono.setText(telVal != null ? telVal.toString() : "");

        // hacemos la condicional para rellenar los comboBox segun si estaba activo o inactivo
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
        String nit = txtNIT.getText().trim();
        String telefonoStr = txtTelefono.getText().trim();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el NIT es obligatorio
        if (nit.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo NIT es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNIT.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el Teléfono es obligatorio
        if (telefonoStr.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Teléfono es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }

        int telefono;
        try {
            // Validar que el teléfono sea un número válido
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panelPrincipal, "El teléfono debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor donde le pasaremos la información del nombre, nit, telefono, estado
            Cliente cliente = new Cliente(nombre, nit, telefono, estado);
            //llamamos a nuestro metodo insertar y le pasamos el constructor
            clienteDAO.insertar(cliente);
            //limpiamos los txt
            limpiarFormulario();
            //mostramos los datos en la tabla
            cargarTabla();
            JOptionPane.showMessageDialog(panelPrincipal, "Cliente guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos nuestro metodo para el botón al momento que se le de click en actualizar
    private void onActualizar() {
        //veriicamos si hay una fila seleccionada
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un cliente de la tabla para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String nit = txtNIT.getText().trim();
        String telefonoStr = txtTelefono.getText().trim();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el NIT es obligatorio
        if (nit.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo NIT es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNIT.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el Teléfono es obligatorio
        if (telefonoStr.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Teléfono es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }

        int telefono;
        try {
            // Validar que el teléfono sea un número válido
            telefono = Integer.parseInt(telefonoStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panelPrincipal, "El teléfono debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            txtTelefono.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor para actualizar la información por medio del update
            Cliente cliente = new Cliente(selectedId, nombre, nit, telefono, estado);
            boolean ok = clienteDAO.actualizar(cliente);
            if (ok) {
                //mostramos los datos en la tabla
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Cliente actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Creaermos nuestro botón de eliminar
    private void onEliminar() {
        //validaremos si hay un dato seleccionado si no mostraremos un mensaje de alerta
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un cliente para eliminar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //mostrara una ventana de confirmación para eliminar el dato
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea eliminar este cliente?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                //utilizaremos nuestro constructor para eliminar un id cambiando el estado a 0
                boolean ok = clienteDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Cliente eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al eliminar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //mostraremos los datos del cliente en la tabla
    private void cargarTabla() {
        try {
            List<Cliente> lista = clienteDAO.listar();
            model.setRowCount(0); // Limpiar las filas existentes en la tabla
            // Recorrer la lista de clientes y agregar cada uno como una nueva fila
            for (Cliente c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getNit(),
                        c.getTelefono(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar la tabla de clientes.", "Error", JOptionPane.ERROR_MESSAGE);
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
            List<Cliente> lista = clienteDAO.buscarPorNombre(nombre);
            model.setRowCount(0); // Limpiar todas las filas de la tabla

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron clientes con ese nombre.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }

            // Iterar sobre la lista y agregar cada cliente como una nueva fila
            for (Cliente c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getNit(),
                        c.getTelefono(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //creamos metodo par limpiar el formulario
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtNIT.setText("");
        txtTelefono.setText("");
        cboEstado.setSelectedIndex(0);
        tblClientes.clearSelection();
        selectedId = null;
    }
    //crearemos el metodo seleccionar la fila por el ID
    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblClientes.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    // Método principal que ejecuta la aplicación Swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Clientes");
            f.setContentPane(new ClienteForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}