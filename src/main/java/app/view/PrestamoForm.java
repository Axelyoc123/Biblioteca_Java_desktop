package app.view;

import app.dao.ClienteDAO;
import app.dao.LibroDAO;
import app.dao.PrestamoDAO;
import app.model.Cliente;
import app.model.ComboItem;
import app.model.Libro;
import app.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PrestamoForm {
    // Componentes de la interfaz gráfica
    public JPanel panelPrincipal;
    private JComboBox<ComboItem> cboCliente;
    private JComboBox<ComboItem> cboLibro;
    private JTextField txtFecha;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnCargar;
    private JTable tblPrestamos;
    private JButton btnEliminar;
    private JButton btnBuscarCliente;

    // Instancias de los objetos DAO
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    // Modelo para la tabla de préstamos
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Cliente", "Libro", "Fecha", "Estado"}, 0
    );

    // Variable para almacenar el ID del préstamo seleccionado
    private Integer selectedId = null;

    // Constructor de la clase
    public PrestamoForm() {
        // Configuración inicial de la interfaz y la tabla
        panelPrincipal.setPreferredSize(new Dimension(800, 500));
        tblPrestamos.setModel(model);
        tblPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Llenar los ComboBox de cliente y libro con datos activos de la base de datos
        cargarClientesEnCombo();
        cargarLibrosEnCombo();

        // El campo de fecha se auto-completa con la fecha actual al cargar, pero puede ser editado.
        txtFecha.setText(LocalDate.now().toString());

        // Llenar el ComboBox de estado
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        // Asignar acciones a los botones usando expresiones lambda
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscarCliente.addActionListener(e -> onBuscarPorCliente());

        // Listener para la selección de filas en la tabla, carga los datos al formulario
        tblPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;

            int row = tblPrestamos.getSelectedRow();
            if (row == -1) {
                selectedId = null;
                limpiarFormulario();
                return;
            }

            // Cargar los datos de la fila seleccionada a los campos del formulario
            selectedId = (Integer) model.getValueAt(row, 0);
            String clienteNombre = (String) model.getValueAt(row, 1);
            String libroNombre = (String) model.getValueAt(row, 2);
            txtFecha.setText(model.getValueAt(row, 3).toString());
            String estadoTxt = (String) model.getValueAt(row, 4);

            seleccionarItemPorNombre(cboCliente, clienteNombre);
            seleccionarItemPorNombre(cboLibro, libroNombre);
            cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estadoTxt) ? 0 : 1);
        });

        // Cargar la tabla al iniciar la aplicación
        cargarTabla();
    }

    // Método para cargar la lista de clientes activos en el ComboBox
    private void cargarClientesEnCombo() {
        try {
            cboCliente.removeAllItems();
            // Agregar un item de "placeholder" al inicio
            cboCliente.addItem(new ComboItem(-1, "Seleccione un cliente..."));

            List<Cliente> clientes = clienteDAO.listar();
            for (Cliente c : clientes) {
                if (c.getEstado() == 1) { // Solo clientes activos
                    cboCliente.addItem(new ComboItem(c.getId(), c.getNombre()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar clientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para cargar la lista de libros activos en el ComboBox
    private void cargarLibrosEnCombo() {
        try {
            cboLibro.removeAllItems();
            // Agregar un item de "placeholder" al inicio
            cboLibro.addItem(new ComboItem(-1, "Seleccione un libro..."));

            List<Libro> libros = libroDAO.listar();
            for (Libro l : libros) {
                if (l.getEstado() == 1) { // Solo libros activos
                    cboLibro.addItem(new ComboItem(l.getId(), l.getNombre()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar libros: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método de utilidad para seleccionar un ítem en un ComboBox por su nombre (label)
    private void seleccionarItemPorNombre(JComboBox<ComboItem> combo, String nombre) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            ComboItem item = combo.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    // Lógica para guardar un nuevo préstamo
    private void onGuardar() {
        ComboItem clienteItem = (ComboItem) cboCliente.getSelectedItem();
        ComboItem libroItem = (ComboItem) cboLibro.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        Date fecha;
        try {
            fecha = Date.valueOf(txtFecha.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(panelPrincipal, "Formato de fecha inválido. Use AAAA-MM-DD.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //validamos que no hayan campos vacios como cliente y libro
        if (clienteItem == null || clienteItem.getId() == -1) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un cliente.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (libroItem == null || libroItem.getId() == -1) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un libro.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if(prestamoDAO.libroPrestadoInsert(libroItem.getId())){
                JOptionPane.showMessageDialog(panelPrincipal,"Este libro ya fue prestado. Por favor Seleccionar un nuevo libro","Libro Prestado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Prestamo prestamo = new Prestamo();
            prestamo.setIdCliente(clienteItem.getId());
            prestamo.setIdLibro(libroItem.getId());
            prestamo.setFecha(fecha);
            prestamo.setEstado(estado);

            prestamoDAO.insertar(prestamo);
            limpiarFormulario();
            cargarTabla();
            JOptionPane.showMessageDialog(panelPrincipal, "Préstamo guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar el préstamo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lógica para actualizar un préstamo existente
    private void onActualizar() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un préstamo de la tabla para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }


        ComboItem clienteItem = (ComboItem) cboCliente.getSelectedItem();
        ComboItem libroItem = (ComboItem) cboLibro.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        Date fecha;
        try {
            fecha = Date.valueOf(txtFecha.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(panelPrincipal, "Formato de fecha inválido. Use AAAA-MM-DD.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (clienteItem == null || clienteItem.getId() == -1 || libroItem == null || libroItem.getId() == -1) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un cliente y un libro.", "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if(prestamoDAO.libroPrestamoActualizacion(libroItem.getId(),selectedId)){
                JOptionPane.showMessageDialog(panelPrincipal,"Este libro ya fue prestado. Por favor Seleccionar un nuevo libro","Libro Prestado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Prestamo prestamo = new Prestamo();
            prestamo.setId(selectedId);
            prestamo.setIdCliente(clienteItem.getId());
            prestamo.setIdLibro(libroItem.getId());
            prestamo.setFecha(fecha);
            prestamo.setEstado(estado);

            boolean ok = prestamoDAO.actualizar(prestamo);
            if (ok) {
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Préstamo actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar el préstamo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Lógica para realizar una eliminación lógica
    private void onEliminar() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un prestamo para devolver.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea Devolver este libro?", "Confirmar Devolución", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = prestamoDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Libro devuelto exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al devolver el libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Método para cargar y mostrar todos los préstamos en la JTable
    private void cargarTabla() {
        try {
            List<Prestamo> lista = prestamoDAO.listar();
            model.setRowCount(0);
            for (Prestamo p : lista) {
                // Obtener los nombres de cliente y libro a partir de sus IDs
                String clienteNombre = "N/A";
                Cliente cliente = clienteDAO.buscarPorId(p.getIdCliente());
                if(cliente != null) clienteNombre = cliente.getNombre();

                String libroNombre = "N/A";
                Libro libro = libroDAO.buscarPorId(p.getIdLibro());
                if(libro != null) libroNombre = libro.getNombre();

                model.addRow(new Object[]{
                        p.getId(),
                        clienteNombre,
                        libroNombre,
                        p.getFecha(),
                        p.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar la tabla de préstamos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //metodo para buscar por cliente
    private void onBuscarPorCliente() {
        ComboItem clienteItem = (ComboItem) cboCliente.getSelectedItem();

        if (clienteItem == null || clienteItem.getId() == -1) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un cliente para buscar.", "Error de búsqueda", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<Prestamo> lista = prestamoDAO.buscarPorCliente(clienteItem.getId());
            model.setRowCount(0);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron préstamos para el cliente seleccionado.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }

            for (Prestamo p : lista) {
                // Obtener los nombres de cliente y libro a partir de sus IDs
                String clienteNombre = "N/A";
                Cliente cliente = clienteDAO.buscarPorId(p.getIdCliente());
                if (cliente != null) clienteNombre = cliente.getNombre();

                String libroNombre = "N/A";
                Libro libro = libroDAO.buscarPorId(p.getIdLibro());
                if (libro != null) libroNombre = libro.getNombre();

                model.addRow(new Object[]{
                        p.getId(),
                        clienteNombre,
                        libroNombre,
                        p.getFecha(),
                        p.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar préstamos por cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Método para limpiar los campos del formulario
    private void limpiarFormulario() {
        // Seleccionar el item "placeholder" para que el combo box se vea vacío
        cboCliente.setSelectedIndex(0);
        cboLibro.setSelectedIndex(0);

        txtFecha.setText(LocalDate.now().toString());
        cboEstado.setSelectedIndex(0);
        tblPrestamos.clearSelection();
        selectedId = null;
    }

    // Método para seleccionar una fila de la tabla por su ID
    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && (Integer) val == id) {
                tblPrestamos.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    // Método principal que ejecuta la aplicación Swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Préstamos");
            f.setContentPane(new PrestamoForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}