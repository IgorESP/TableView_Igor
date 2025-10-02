package com.Igoresparza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class MainApp extends Application {


    // Logger
    private static final Logger logger= LoggerFactory.getLogger(MainApp.class);


    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargamos el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/tableView.fxml"));
        Parent root = loader.load();


        // Configuramos la ventana principal
        logger.info("Creacion del titulo");
        logger.warn("Warning");
        primaryStage.setTitle("Tabla de Personas");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();



    }
}
