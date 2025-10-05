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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    private static final Logger logger = LoggerFactory.getLogger(ControladorVentana.class);

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
        logger.info("Inicializando ControladorVentana y configurando TableView.");

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

        // Botón Add
        addButton.setOnAction(e -> agregarPersona());
        logger.debug("Listener del botón 'Añadir' configurado.");

        // Botón Delete
        deleteButton.setOnAction(e -> {
            ObservableList<Persona> selected = tableView.getSelectionModel().getSelectedItems();
            logger.info("Intentando eliminar {} persona(s) seleccionada(s).", selected.size());

            List<Persona> toRemove = new ArrayList<>(selected);
            for (Persona p : toRemove) {
                if (personaDAO.deletePersona(p.getPersonId())) {
                    // Si se elimina de la DB, se añade a la lista de restauración y se quita de la tabla
                    data.remove(p);
                    deletedData.add(p);
                    logger.info("Persona ID {} eliminada con éxito (DB y lista de borrados).", p.getPersonId());
                } else {
                    logger.warn("Fallo al eliminar la persona ID {} de la DB. No se quitará de la tabla.", p.getPersonId());
                }
            }
        });
        logger.debug("Listener del botón 'Eliminar' configurado.");

        // Botón Restore
        restoreButton.setOnAction(e -> {
            logger.info("Iniciando restauración de {} persona(s).", deletedData.size());

            List<Persona> restored = new ArrayList<>();
            // Restaurar personas individualmente en la BDD para obtener un nuevo ID
            for (Persona p : deletedData) {
                // Inserta de nuevo la persona (sin su ID antiguo)
                Persona newP = personaDAO.insertPersona(new Persona(p.getFirstName(), p.getLastName(), p.getBirthDate()));
                if (newP != null) {
                    restored.add(newP);
                    logger.info("Persona restaurada con éxito (Nuevo ID: {}).", newP.getPersonId());
                } else {
                    logger.error("Fallo al restaurar la persona (Nombre: {}) en la BDD. Se saltará.", p.getFirstName());
                }
            }
            deletedData.clear();
            data.addAll(restored);
            logger.info("{} persona(s) restaurada(s) y añadidas a la tabla.", restored.size());
        });
        logger.debug("Listener del botón 'Restaurar' configurado.");
    }

    /**
     * Carga los datos iniciales de la base de datos o crea datos por defecto si está vacía.
     */
    private void inicializarDatosBDD() {
        List<Persona> personasExistentes = personaDAO.getAllPersonas();
        logger.info("Cargando datos iniciales. Total de registros en la DB: {}.", personasExistentes.size());

        if (personasExistentes.isEmpty()) {
            logger.warn("Base de datos vacía. Se procederá a la carga de 3 alumnos predefinidos.");
            // Creación de datos por defecto (mantenido de la lógica anterior)
            Persona p1 = new Persona("Igor", "Esparza", LocalDate.of(1995, 1, 1));
            Persona p2 = new Persona("Laura", "García", LocalDate.of(2000, 5, 15));
            Persona p3 = new Persona("David", "Pérez", LocalDate.of(1988, 10, 30));

            List<Persona> iniciales = List.of(p1, p2, p3);
            for(Persona p : iniciales) {
                personaDAO.insertPersona(p);
                logger.debug("Insertando alumno inicial: {}", p.getFirstName());
            }
            // Recargar datos para obtener IDs correctos después de la inserción
            data.addAll(personaDAO.getAllPersonas());
            logger.info("Carga inicial de 3 alumnos completada.");
        } else {
            data.addAll(personasExistentes);
            logger.info("Datos cargados correctamente desde la BDD a la tabla.");
        }
    }


    /**
     * Método privado que gestiona la lógica para agregar una nueva persona a la tabla, validando los datos
     * e insertando el registro en la base de datos.
     */
    private void agregarPersona() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();

        Persona p = new Persona(firstName, lastName, birthDate);
        logger.debug("Intento de agregar persona: {} {}", firstName, lastName);

        List<String> errores = new ArrayList<>();
        if (p.isValidPerson(errores)) {
            logger.debug("Datos validados correctamente. Procediendo a insertar en la BDD.");

            // 1. Insertar en la Base de Datos. El objeto 'p' se actualiza con el ID de la DB.
            Persona newPersonaWithId = personaDAO.insertPersona(p);

            if (newPersonaWithId != null) {
                // 2. Si es exitoso, añadir a la tabla.
                data.add(newPersonaWithId);
                logger.info("Persona {} {} añadida con éxito. ID: {}", firstName, lastName, newPersonaWithId.getPersonId());

                // Limpiar campos
                firstNameField.clear();
                lastNameField.clear();
                birthDatePicker.setValue(null);
            } else {
                errores.add("Fallo al guardar en la base de datos. Verifique la conexión o el esquema.");
                logger.error("Fallo al insertar la persona {} {} en la BDD (DAO devolvió null).", firstName, lastName);
            }
        }

        // Muestra la alerta si hay errores de validación o de persistencia
        if (!errores.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos inválidos o error de persistencia");
            alert.setContentText(String.join("\n", errores));
            alert.showAndWait();
            logger.warn("Validación fallida para la entrada de persona. Errores: {}", errores);
        }
    }
}