package app.view;

import app.dao.AutorDAO;
import app.dao.CategoriaDAO;
import app.dao.LibroDAO;
import app.model.Autor;
import app.model.Categoria;
import app.model.ComboItem;
import app.model.Libro;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LibroForm {
    // Declaración de los componentes de la interfaz gráfica para la gestión de libros
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtAnio;
    private JComboBox<ComboItem> cboAutor;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnCargar;
    private JTable tblLibros;
    private JLabel Categora;
    private JComboBox<ComboItem> cboCategoria;
    private JButton btnEliminar;
    private JButton btnBuscarCategoria;
    private JButton btnBuscarNombre;

    // Instancia de los objetos DAO para la interacción con la base de datos
    private final AutorDAO autorDAO = new AutorDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    // Modelo de la tabla para mostrar los datos de los libros
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Año", "Autor", "Categoria", "Estado"}, 0
    );

    // Variable para almacenar el ID del libro seleccionado
    private Integer selectedId = null;

    // Constructor de la clase
    public LibroForm() {
        // Configuración inicial de la interfaz
        panelPrincipal.setPreferredSize(new Dimension(900, 600));
        tblLibros.setModel(model);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Llenar el ComboBox de estado
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        // Carga los datos iniciales en los ComboBox de autor y categoría
        cargarAutoresEnCombo();
        cargarCategoriasEnCombo();

        // Asigna los listeners a los botones
        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());
        btnEliminar.addActionListener(e -> onEliminar());
        btnBuscarNombre.addActionListener(e -> onBuscarPorNombre());
        btnBuscarCategoria.addActionListener(e -> onBuscarPorCategoria());

        // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
        tblLibros.getSelectionModel().addListSelectionListener(this::onTableSelection);

        // Carga la tabla al iniciar la ventana
        cargarTabla();
    }
    // al momento de seleccionar una fila de la tabla nos ayudara a rellenar los datos en nuestros campos
    private void onTableSelection(ListSelectionEvent e) {
        // Evitar que el evento se dispare dos veces
        if (e.getValueIsAdjusting()) return;
        int row = tblLibros.getSelectedRow();
        // Si no hay fila seleccionada, limpiar el formulario y el ID
        if (row == -1) {
            selectedId = null;
            limpiarFormulario();
            return;
        }

        // nos ayudara a leeer los datos que estan en la tabla
        Object idVal = model.getValueAt(row, 0);
        Object nomVal = model.getValueAt(row, 1);
        Object anioVal = model.getValueAt(row, 2);
        Object autorVal = model.getValueAt(row, 3);
        Object catVal = model.getValueAt(row, 4);
        Object estVal = model.getValueAt(row, 5);

        // Guardamos el id seleccionado
        selectedId = (idVal != null) ? (Integer) idVal : null;

        // Cargamos la información en los campos txt
        txtNombre.setText(nomVal != null ? nomVal.toString() : "");
        txtAnio.setText(anioVal != null ? anioVal.toString() : "");

        // Selecciona el autor en el ComboBox por su nombre
        if (autorVal != null) seleccionarAutorPorNombre(autorVal.toString());

        // Selecciona la categoría en el ComboBox por su nombre
        if (catVal != null) seleccionarCategoriaPorNombre(catVal.toString());

        // hacemos la condicional para rellenar los comboBox segun si estaba activo o inactivo
        if (estVal != null && estVal.toString().equalsIgnoreCase("Activo")) {
            cboEstado.setSelectedIndex(0); // 1
        } else if (estVal != null && estVal.toString().equalsIgnoreCase("Inactivo")) {
            cboEstado.setSelectedIndex(1); // 0
        }
    }
    // Carga los autores en el JComboBox
    private void cargarAutoresEnCombo() {
        try {
            cboAutor.removeAllItems();
            List<Autor> autores = autorDAO.listar();
            for (Autor a : autores) {
                cboAutor.addItem(new ComboItem(a.getId(), a.getNombre()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // Carga las categorías en el JComboBox
    private void cargarCategoriasEnCombo() {
        try {
            cboCategoria.removeAllItems();
            List<Categoria> categorias = categoriaDAO.listar();
            for (Categoria c : categorias) {
                cboCategoria.addItem(new ComboItem(c.getId(), c.getNombre()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    // Selecciona un autor en el combo por su nombre
    private void seleccionarAutorPorNombre(String nombre) {
        for (int i = 0; i < cboAutor.getItemCount(); i++) {
            ComboItem item = cboAutor.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboAutor.setSelectedIndex(i);
                return;
            }
        }
    }
    // Selecciona una categoría en el combo por su nombre
    private void seleccionarCategoriaPorNombre(String nombre) {
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            ComboItem item = cboCategoria.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboCategoria.setSelectedIndex(i);
                return;
            }
        }
    }
    //crearemos nuestro metodo para el botón al momento que se le de click en guardar
    private void onGuardar() {
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem categoriaItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el año es obligatorio
        if (anioStr.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Año es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el autor es obligatorio
        if (autorItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un Autor.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de la categoria es obligatoria
        if (categoriaItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar una Categoría.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio;
        try {
            // Validar que el año sea un número válido
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panelPrincipal, "El año debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            txtAnio.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor donde le pasaremos la información del nombre, año, autor, categoria, estado
            Libro l = new Libro(nombre, anio, autorItem.getId(), categoriaItem.getId(), estado);
            //llamamos a nuestro metodo insertar y le pasamos el constructor
            libroDAO.insertar(l);
            //limpiamos los txt
            limpiarFormulario();
            //mostramos los datos en la tabla
            cargarTabla();
            JOptionPane.showMessageDialog(panelPrincipal, "Libro guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al guardar el libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos nuestro metodo para el botón al momento que se le de click en actualizar
    private void onActualizar() {
        //veriicamos si hay una fila seleccionada
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un libro para actualizar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem categoriaItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Nombre es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el año es obligatorio
        if (anioStr.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "El campo Año es obligatorio.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            txtAnio.requestFocus();
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el autor es obligatorio
        if (autorItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar un Autor.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //validaremos si nuestro campo esta vacio para regresar un mensaje de la categoria es obligatoria
        if (categoriaItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Debe seleccionar una Categoría.", "Campo obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio;
        try {
            // Validar que el año sea un número válido
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(panelPrincipal, "El año debe ser un número válido.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            txtAnio.requestFocus();
            return;
        }

        try {
            //crearemos nuestro constructor para actualizar la información por medio del update
            Libro l = new Libro(selectedId, nombre, anio, autorItem.getId(), categoriaItem.getId(), estado);
            boolean ok = libroDAO.actualizar(l);
            if (ok) {
                //mostramos los datos en la tabla
                cargarTabla();
                seleccionarFilaPorId(selectedId);
                JOptionPane.showMessageDialog(panelPrincipal, "Libro actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al actualizar el libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //Creaermos nuestro botón de eliminar
    private void onEliminar() {
        //validaremos si hay un dato seleccionado si no mostraremos un mensaje de alerta
        if (selectedId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un libro para eliminar.", "Error de selección", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //mostrara una ventana de confirmación para eliminar el dato
        int confirm = JOptionPane.showConfirmDialog(panelPrincipal, "¿Está seguro que desea eliminar este libro?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                //utilizaremos nuestro constructor para eliminar un id cambiando el estado a 0
                boolean ok = libroDAO.eliminar(selectedId);
                if (ok) {
                    cargarTabla();
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(panelPrincipal, "Libro eliminado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panelPrincipal, "Error al eliminar el libro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //mostraremos los datos del libro en la tabla
    private void cargarTabla() {
        try {
            List<Libro> lista = libroDAO.listar();
            model.setRowCount(0); // Limpiar las filas existentes en la tabla
            // Recorrer la lista de libros y agregar cada uno como una nueva fila
            for (Libro l : lista) {
                String autorNombre = autorDAO.buscarPorId(l.getIdAutor()).getNombre();
                String categoriaNombre = categoriaDAO.buscarPorId(l.getIdCategoria()).getNombre();

                model.addRow(new Object[] {
                        l.getId(),
                        l.getNombre(),
                        l.getAnio(),
                        autorNombre,
                        categoriaNombre,
                        l.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al cargar la tabla de libros.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos el método buscar por nombre
    private void onBuscarPorNombre() {
        //almacenaremos la información en variables de lo que tengamos en nuestros txt
        String nombre = txtNombre.getText().trim();
        //validaremos si nuestro campo esta vacio para regresar un mensaje de el nombre es obligatorio
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "Ingrese un nombre para buscar.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            List<Libro> libros = libroDAO.buscarPorNombre(nombre);
            model.setRowCount(0);
            if (libros.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron libros con ese nombre.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Libro l : libros) {
                    String autorNombre = autorDAO.buscarPorId(l.getIdAutor()).getNombre();
                    String categoriaNombre = categoriaDAO.buscarPorId(l.getIdCategoria()).getNombre();
                    model.addRow(new Object[]{
                            l.getId(),
                            l.getNombre(),
                            l.getAnio(),
                            autorNombre,
                            categoriaNombre,
                            l.getEstado() == 1 ? "Activo" : "Inactivo"
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar libros: " + ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }
    //crearemos el método buscar por categoria
    private void onBuscarPorCategoria() {
        //almacenaremos la información en variables de lo que tengamos en nuestros combobox
        ComboItem categoriaItem = (ComboItem) cboCategoria.getSelectedItem();
        //validaremos si nuestro campo esta vacio para regresar un mensaje de la categoria es obligatoria
        if (categoriaItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione una categoría para buscar.", "Campo Obligatorio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Libro> libros = libroDAO.buscarPorCategoria(categoriaItem.getId());
            model.setRowCount(0);
            if (libros.isEmpty()) {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron libros en esta categoría.", "No Encontrado", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Libro l : libros) {
                    String autorNombre = autorDAO.buscarPorId(l.getIdAutor()).getNombre();
                    String categoriaNombre = categoriaDAO.buscarPorId(l.getIdCategoria()).getNombre();
                    model.addRow(new Object[]{
                            l.getId(),
                            l.getNombre(),
                            l.getAnio(),
                            autorNombre,
                            categoriaNombre,
                            l.getEstado() == 1 ? "Activo" : "Inactivo"
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al buscar libros por categoría: " + ex.getMessage(), "Error de Búsqueda", JOptionPane.ERROR_MESSAGE);
        }
    }
    //creamos metodo para limpiar el formulario
    private void limpiarFormulario() {
        txtNombre.setText("");
        txtAnio.setText("");
        if (cboAutor.getItemCount() > 0) cboAutor.setSelectedIndex(0);
        if (cboCategoria.getItemCount() > 0) cboCategoria.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tblLibros.clearSelection();
        selectedId = null;
    }
    //crearemos el metodo seleccionar la fila por el ID
    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblLibros.setRowSelectionInterval(i, i);
                break;
            }
        }
    }
    // Método principal que ejecuta la aplicación Swing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Libros");
            f.setContentPane(new LibroForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}