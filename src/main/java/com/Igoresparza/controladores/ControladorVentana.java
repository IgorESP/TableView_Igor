package com.Igoresparza.controladores;

import com.Igoresparza.modelo.Persona;
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
 * Gestiona la tabla, los campos de entrada, los botones de acción (Añadir, Eliminar, Restaurar)
 * y la lógica de validación de los datos de la persona.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 * @see com.Igoresparza.modelo.Persona
 * @see javafx.fxml.FXML
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
    /** Lista observable para almacenar personas eliminadas, permitiendo la restauración. */
    private ObservableList<Persona> deletedData = FXCollections.observableArrayList();
    /** Contador para asignar el siguiente ID de persona. */
    private int nextId = 1;

    /**
     * Método de inicialización llamado automáticamente por el FXMLLoader después de que
     * todos los elementos FXML han sido inyectados.
     * Configura las columnas de la tabla, las fábricas de valores, los listeners de botones
     * y carga los datos iniciales.
     */
    @FXML
    public void initialize() {
        // Configurar columnas del GridPane
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

        // --- CARGAR PERSONAS INICIALES ---
        Persona p1 = new Persona("Xiker", "Garcia", LocalDate.of(2002, 1, 15));
        p1.setPersonId(nextId++);
        Persona p2 = new Persona("Ruben", "Luna", LocalDate.of(2005, 5, 20));
        p2.setPersonId(nextId++);
        Persona p3 = new Persona("Gaizka", "Rodriguez", LocalDate.of(2001, 12, 3));
        p3.setPersonId(nextId++);

        data.addAll(p1, p2, p3);


        // Botón Add: Listener para agregar una nueva persona
        addButton.setOnAction(e -> agregarPersona());

        // Botón Delete: Listener para eliminar las filas seleccionadas de la tabla
        deleteButton.setOnAction(e -> {
            ObservableList<Persona> selected = tableView.getSelectionModel().getSelectedItems();
            deletedData.addAll(selected); // Guardar eliminados para restaurar
            data.removeAll(selected);
        });

        // Botón Restore: Listener para restaurar las personas que fueron eliminadas
        restoreButton.setOnAction(e -> {
            data.addAll(deletedData);
            deletedData.clear();
        });
    }

    /**
     * Método privado que gestiona la lógica para agregar una nueva persona a la tabla.
     * Obtiene los datos de los campos de entrada, valida la persona y, si es válida,
     * la añade a la lista observable y limpia los campos. En caso de error, muestra una alerta.
     */
    private void agregarPersona() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        LocalDate birthDate = birthDatePicker.getValue();

        Persona p = new Persona(firstName, lastName, birthDate);
        p.setPersonId(nextId++);

        // Validar antes de agregar usando lista mutable
        List<String> errores = new ArrayList<>();
        if (p.isValidPerson(errores)) {
            data.add(p);
            firstNameField.clear();
            lastNameField.clear();
            birthDatePicker.setValue(null);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos inválidos");
            alert.setContentText(String.join("\n", errores));
            alert.showAndWait();
        }
    }
}