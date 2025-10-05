package com.Igoresparza.controladores;

import com.Igoresparza.modelo.Persona;
// Asegúrate de que esta sea la ruta correcta a tu DAO.
import com.Igoresparza.dao.PersonaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la ventana principal que maneja la lógica de la interfaz de la tabla de personas.
 * Sincroniza las operaciones de la tabla (Añadir, Eliminar, Restaurar) con la base de datos a través del PersonaDAO.
 *
 * @author Igor Esparza
 * @version 1.1
 * @since 2025-10-04
 * @see com.Igoresparza.modelo.Persona
 * @see com.Igoresparza.dao.PersonaDAO
 */
public class ControladorVentana {

    @FXML
    private GridPane gridPane;

    @FXML
    private TextField firstNameField, lastNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private Button addButton, restoreButton, deleteButton;

    @FXML
    private TableView<Persona> tableView;

    @FXML
    private TableColumn<Persona, Integer> idColumn;
    @FXML
    private TableColumn<Persona, String> firstNameColumn;
    @FXML
    private TableColumn<Persona, String> lastNameColumn;
    @FXML
    private TableColumn<Persona, LocalDate> birthDateColumn;

    /** Lista observable de personas que se muestran en la tabla. */
    private ObservableList<Persona> data = FXCollections.observableArrayList();
    /** Lista observable para almacenar personas eliminadas, permitiendo la restauración en memoria. */
    private ObservableList<Persona> deletedData = FXCollections.observableArrayList();

    /** Objeto de acceso a datos para la persistencia de Persona. */
    private final PersonaDAO personaDAO = new PersonaDAO();

    /**
     * Método de inicialización llamado automáticamente por el FXMLLoader.
     * Carga la configuración de la tabla, inicializa datos si es necesario, y establece los listeners de botones.
     */
    @FXML
    public void initialize() {
        // --- CONFIGURACIÓN DE PANELES Y COLUMNAS ---
        ColumnConstraints col1 = new ColumnConstraints(100);
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(80);
        gridPane.getColumnConstraints().clear();
        gridPane.getColumnConstraints().addAll(col1, col2, col3);

        // Configurar TableView
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getPersonId()).asObject());
        firstNameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLastName()));
        birthDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBirthDate()));

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(data);

        // --- LÓGICA DE INICIALIZACIÓN DE DATOS (CON BDD) ---
        inicializarDatosBDD();


        // --- LISTENERS DE BOTONES ---

        // Botón Add: Listener para agregar una nueva persona
        addButton.setOnAction(e -> agregarPersona());

        // Botón Delete: Listener para eliminar las filas seleccionadas (sincronizado con BDD)
        deleteButton.setOnAction(e -> {
            ObservableList<Persona> selected = tableView.getSelectionModel().getSelectedItems();
            List<Persona> toRemove = new ArrayList<>(selected);

            for (Persona p : toRemove) {
                // 1. Eliminar de la Base de Datos.
                if (personaDAO.deletePersona(p.getPersonId())) {
                    // 2. Si se elimina de la DB, se elimina de la tabla y se añade a la lista de restauración.
                    data.remove(p);
                    deletedData.add(p);
                } else {
                    System.err.println("Error: Fallo al eliminar la persona " + p.getPersonId() + " de la DB.");
                }
            }
        });

        // Botón Restore: Listener para restaurar las personas que fueron eliminadas (sincronizado con BDD)
        restoreButton.setOnAction(e -> {
            List<Persona> restored = new ArrayList<>();

            for (Persona p : deletedData) {
                // Se reinserta en la BDD (obteniendo un nuevo ID).
                Persona newP = personaDAO.insertPersona(p);

                if (newP != null) {
                    restored.add(newP);
                } else {
                    System.err.println("Fallo al restaurar la persona " + p.getFirstName() + ". Error de DB.");
                }
            }

            // Limpiar la lista de eliminados y añadir los restaurados a la tabla
            deletedData.clear();
            data.addAll(restored);
        });
    }

    /**
     * Verifica si la base de datos está vacía y, si lo está, inserta los 3 alumnos iniciales.
     * Luego, carga todos los datos disponibles en la tabla.
     */
    private void inicializarDatosBDD() {
        List<Persona> personasExistentes = personaDAO.getAllPersonas();

        if (personasExistentes.isEmpty()) {
            System.out.println("Base de datos vacía. Inicializando con 3 alumnos predefinidos.");

            // 1. Crear los 3 alumnos (LocalDate.of(AÑO, MES, DÍA))
            Persona p1 = new Persona("Xiker", "Garcia", LocalDate.of(2002, 1, 15));
            Persona p2 = new Persona("Ruben", "Luna", LocalDate.of(2005, 5, 20));
            Persona p3 = new Persona("Gaizka", "Rodriguez", LocalDate.of(2001, 12, 3));

            List<Persona> iniciales = List.of(p1, p2, p3);

            // 2. Insertar cada alumno en la base de datos (el DAO le asigna el ID)
            for(Persona p : iniciales) {
                personaDAO.insertPersona(p);
            }

            // Recargar para obtener los IDs generados por la BDD.
            data.addAll(personaDAO.getAllPersonas());
        } else {
            // Si ya hay datos, simplemente cárgalos
            data.addAll(personasExistentes);
        }
    }

    /**
     * Gestiona la lógica para agregar una nueva persona, validando los datos
     * e insertando el registro en la base de datos.
     */
    private void agregarPersona() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();

        Persona p = new Persona(firstName, lastName, birthDate);

        List<String> errores = new ArrayList<>();
        if (p.isValidPerson(errores)) {
            // 1. Insertar en la Base de Datos. El objeto 'p' se actualiza con el ID de la DB.
            Persona newPersonaWithId = personaDAO.insertPersona(p);

            if (newPersonaWithId != null) {
                // 2. Si es exitoso, añadir a la tabla.
                data.add(newPersonaWithId);

                // Limpiar campos
                firstNameField.clear();
                lastNameField.clear();
                birthDatePicker.setValue(null);
            } else {
                errores.add("Fallo al guardar en la base de datos. Verifique la conexión o el esquema.");
            }
        }

        // Muestra la alerta si hay errores de validación o de persistencia
        if (!errores.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos inválidos o error de persistencia");
            alert.setContentText(String.join("\n", errores));
            alert.showAndWait();
        }
    }
}