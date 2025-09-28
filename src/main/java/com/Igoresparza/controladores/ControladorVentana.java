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

    private ObservableList<Persona> data = FXCollections.observableArrayList();
    private ObservableList<Persona> deletedData = FXCollections.observableArrayList(); // Para restaurar filas eliminadas
    private int nextId = 1;

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

        // Botón Add
        addButton.setOnAction(e -> agregarPersona());

        // Botón Delete
        deleteButton.setOnAction(e -> {
            ObservableList<Persona> selected = tableView.getSelectionModel().getSelectedItems();
            deletedData.addAll(selected); // Guardar eliminados para restaurar
            data.removeAll(selected);
        });

        // Botón Restore
        restoreButton.setOnAction(e -> {
            data.addAll(deletedData);
            deletedData.clear();
        });
    }

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
