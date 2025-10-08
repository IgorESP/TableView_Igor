package com.Igoresparza.controladores;

import com.Igoresparza.modelo.Persona;
import com.Igoresparza.dao.PersonaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.concurrent.Task; // IMPORTANTE: Nueva importación para asincronía

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Controlador de la ventana principal que maneja la lógica de la interfaz de la tabla de personas.
 * Ahora ASÍNCRONO: Sincroniza las operaciones de la tabla con la base de datos a través del PersonaDAO
 * utilizando JavaFX Task para evitar el bloqueo de la interfaz de usuario.
 *
 * @author Igor Esparza
 * @version 1.2 (Síncrono -> Asíncrono)
 * @since 2025-10-04
 * @see com.Igoresparza.modelo.Persona
 * @see com.Igoresparza.dao.PersonaDAO
 */
public class ControladorVentana {

    private static final Logger logger = LoggerFactory.getLogger(ControladorVentana.class);
    private final PersonaDAO personaDAO = new PersonaDAO(); // Objeto de acceso a datos

    // ... (Declaraciones FXML y ObservableList se mantienen iguales)
    @FXML private GridPane gridPane;
    @FXML private TextField firstNameField, lastNameField;
    @FXML private DatePicker birthDatePicker;
    @FXML private Button addButton, restoreButton, deleteButton;
    @FXML private TableView<Persona> tableView;
    @FXML private TableColumn<Persona, Integer> idColumn;
    @FXML private TableColumn<Persona, String> firstNameColumn;
    @FXML private TableColumn<Persona, String> lastNameColumn;
    @FXML private TableColumn<Persona, LocalDate> birthDateColumn;

    private ObservableList<Persona> data = FXCollections.observableArrayList();
    private ObservableList<Persona> deletedData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        logger.info("Inicializando ControladorVentana y configurando TableView.");

        // --- CONFIGURACIÓN DE PANELES Y COLUMNAS (Se mantiene igual) ---
        ColumnConstraints col1 = new ColumnConstraints(100);
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(80);
        gridPane.getColumnConstraints().clear();
        gridPane.getColumnConstraints().addAll(col1, col2, col3);

        // Configurar TableView (Se mantiene igual)
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getPersonId()).asObject());
        firstNameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getLastName()));
        birthDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getBirthDate()));
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setItems(data);

        // --- LÓGICA DE INICIALIZACIÓN DE DATOS (AHORA ASÍNCRONA) ---
        inicializarDatosBDDAsync();

        // --- LISTENERS DE BOTONES (AHORA ASÍNCRONOS) ---
        addButton.setOnAction(e -> agregarPersonaAsync());
        deleteButton.setOnAction(e -> eliminarPersonasAsync());
        restoreButton.setOnAction(e -> restaurarPersonasAsync());
    }

    /**
     * [ASÍNCRONO] Carga los datos iniciales de la base de datos o crea datos por defecto si está vacía.
     */
    private void inicializarDatosBDDAsync() {
        Task<List<Persona>> loadTask = new Task<>() {
            @Override
            protected List<Persona> call() throws Exception {
                // Lógica de BDD ejecutada en hilo de background (usa el Future.get() del DAO)
                List<Persona> personasExistentes = personaDAO.getAllPersonasAsync().get();
                logger.info("Cargando datos iniciales ASÍNCRONAMENTE. Registros: {}.", personasExistentes.size());

                if (personasExistentes.isEmpty()) {
                    logger.warn("Base de datos vacía. Se procederá a la carga de 3 alumnos predefinidos.");
                    List<Persona> iniciales = List.of(
                            new Persona("Igor", "Esparza", LocalDate.of(1995, 1, 1)),
                            new Persona("Laura", "García", LocalDate.of(2000, 5, 15)),
                            new Persona("David", "Pérez", LocalDate.of(1988, 10, 30))
                    );
                    for(Persona p : iniciales) {
                        // El insertPersonaAsync().get() inserta y obtiene el nuevo ID.
                        personaDAO.insertPersonaAsync(p).get();
                    }
                    // Recargar datos para obtener IDs correctos después de la inserción
                    return personaDAO.getAllPersonasAsync().get();
                }
                return personasExistentes;
            }

            @Override
            protected void succeeded() {
                // Se ejecuta en el hilo UI. Actualiza la tabla.
                data.addAll(getValue());
                logger.info("Carga inicial ASÍNCRONA completada. UI actualizada.");
            }

            @Override
            protected void failed() {
                // Se ejecuta en el hilo UI
                logger.error("Fallo al cargar datos iniciales de la BDD.", getException());
                showAlert("Error de Conexión", "Fallo al conectar o cargar datos iniciales de la BDD.", Alert.AlertType.ERROR, getException());
            }
        };

        new Thread(loadTask).start();
    }


    /**
     * [ASÍNCRONO] Método que gestiona la lógica para agregar una nueva persona.
     */
    private void agregarPersonaAsync() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();

        Persona p = new Persona(firstName, lastName, birthDate);
        logger.debug("Intento de agregar persona ASÍNCRONAMENTE: {} {}", firstName, lastName);

        List<String> errores = new ArrayList<>();
        if (!p.isValidPerson(errores)) {
            showAlert("Datos inválidos", "Datos inválidos", Alert.AlertType.ERROR, errores);
            logger.warn("Validación fallida para la entrada de persona. Errores: {}", errores);
            return;
        }

        Task<Persona> insertTask = new Task<>() {
            @Override
            protected Persona call() throws Exception {
                // Ejecución en hilo de background. Llama al método ASÍNCRONO del DAO y espera el resultado.
                return personaDAO.insertPersonaAsync(p).get();
            }

            @Override
            protected void succeeded() {
                // Se ejecuta en el hilo UI.
                Persona newPersonaWithId = getValue();
                if (newPersonaWithId != null) {
                    data.add(newPersonaWithId);
                    logger.info("Persona {} {} añadida con éxito ASÍNCRONO. ID: {}", firstName, lastName, newPersonaWithId.getPersonId());
                    firstNameField.clear();
                    lastNameField.clear();
                    birthDatePicker.setValue(null);
                } else {
                    showAlert("Error de Persistencia", "Fallo al guardar en la base de datos. Verifique la conexión o el esquema.", Alert.AlertType.ERROR, getException() );
                }
            }

            @Override
            protected void failed() {
                // Se ejecuta en el hilo UI
                logger.error("Fallo al insertar persona.", getException());
                showAlert("Error de BDD", "Error crítico al intentar guardar la persona en la base de datos.", Alert.AlertType.ERROR, getException());
            }
        };

        new Thread(insertTask).start();
    }

    /**
     * [ASÍNCRONO] Método que gestiona la eliminación de una o varias personas.
     */
    private void eliminarPersonasAsync() {
        ObservableList<Persona> selected = tableView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) return;

        logger.info("Intentando eliminar {} persona(s) seleccionada(s) ASÍNCRONAMENTE.", selected.size());
        List<Persona> toRemove = new ArrayList<>(selected);

        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Esta lógica se ejecuta en un hilo de background
                for (Persona p : toRemove) {
                    // Llama al método ASÍNCRONO del DAO y espera el resultado.
                    if (personaDAO.deletePersonaAsync(p.getPersonId()).get()) {
                        deletedData.add(p);
                        logger.info("Persona ID {} marcada para eliminación exitosa.", p.getPersonId());
                    } else {
                        logger.warn("Fallo al eliminar la persona ID {} de la DB.", p.getPersonId());
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                // Se ejecuta en el hilo UI
                data.removeAll(toRemove);
                logger.info("Operación de eliminación ASÍNCRONA completada. UI actualizada.");
            }

            @Override
            protected void failed() {
                // Se ejecuta en el hilo UI
                logger.error("Fallo general al eliminar personas.", getException());
                showAlert("Error de Eliminación", "Fallo al intentar eliminar personas de la BDD.", Alert.AlertType.ERROR, getException());
            }
        };

        new Thread(deleteTask).start();
    }

    /**
     * [ASÍNCRONO] Método que gestiona la restauración de personas eliminadas.
     */
    private void restaurarPersonasAsync() {
        if (deletedData.isEmpty()) return;

        logger.info("Iniciando restauración ASÍNCRONA de {} persona(s).", deletedData.size());

        Task<List<Persona>> restoreTask = new Task<>() {
            @Override
            protected List<Persona> call() throws Exception {
                // Esta lógica se ejecuta en un hilo de background
                List<Persona> restored = new ArrayList<>();
                for (Persona p : deletedData) {
                    // Insertar y obtener el nuevo ID (usa el Future.get() del DAO)
                    Persona newP = personaDAO.insertPersonaAsync(new Persona(p.getFirstName(), p.getLastName(), p.getBirthDate())).get();
                    if (newP != null) {
                        restored.add(newP);
                        logger.info("Persona restaurada con éxito (Nuevo ID: {}).", newP.getPersonId());
                    } else {
                        logger.error("Fallo al restaurar la persona (Nombre: {}) en la BDD.", p.getFirstName());
                    }
                }
                return restored;
            }

            @Override
            protected void succeeded() {
                // Se ejecuta en el hilo UI
                List<Persona> restored = getValue();
                deletedData.clear();
                data.addAll(restored);
                logger.info("{} persona(s) restaurada(s) ASÍNCRONAMENTE y añadidas a la tabla.", restored.size());
            }

            @Override
            protected void failed() {
                // Se ejecuta en el hilo UI
                logger.error("Fallo general al restaurar personas.", getException());
                showAlert("Error de Restauración", "Fallo al intentar restaurar personas en la BDD.", Alert.AlertType.ERROR, getException());
            }
        };

        new Thread(restoreTask).start();
    }

    // --- MÉTODOS AUXILIARES ---

    private void showAlert(String title, String header, Alert.AlertType type, List<String> content) {
        showAlert(title, header, type, String.join("\n", content));
    }

    private void showAlert(String title, String header, Alert.AlertType type, Throwable exception) {
        String contentText = "";
        if (exception != null) {
            Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
            if (cause instanceof SQLException) {
                contentText = "Error de SQL/Conexión: " + cause.getMessage();
            } else {
                contentText = "Error interno: " + cause.getMessage();
            }
        }
        showAlert(title, header, type, contentText);
    }

    private void showAlert(String title, String header, Alert.AlertType type, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        if(contentText != null && !contentText.isEmpty()) {
            alert.setContentText(contentText);
        }
        alert.showAndWait();
    }
}