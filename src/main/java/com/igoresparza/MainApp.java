package com.igoresparza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Igoresparza/fxml/tableView.fxml"));
        Parent root = loader.load();

        // Configurar la ventana
        primaryStage.setTitle("Tabla de Personas");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
