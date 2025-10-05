package com.Igoresparza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javafx.scene.image.Image;
/**
 * Clase principal de la aplicación de gestión de personas.
 * Se encarga de la inicialización del entorno JavaFX, la carga del FXML
 * de la vista principal y la configuración del escenario (Stage).
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 * @see javafx.application.Application
 * @see com.Igoresparza.controladores.ControladorVentana
 */
public class MainApp extends Application {

    /**
     * Logger estático para el registro de eventos y mensajes de la aplicación.
     */
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    /**
     * El método 'start' es el punto de entrada primario para todas las aplicaciones JavaFX
     * después de que la llamada a 'launch(args)' haya sido procesada.
     * Carga la vista principal y configura el escenario (Stage).
     *
     * @param primaryStage El escenario (Stage) principal para esta aplicación, provisto por el sistema JavaFX.
     * @throws Exception Si ocurre un error durante la carga del FXML o la inicialización.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargamos el FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/tableView.fxml"));
        Parent root = loader.load();
        // 1. Cargar el archivo de imagen
        Image icon = new Image(getClass().getResourceAsStream("imagenes/imagen_logo.png"));

        // 2. Establecer el icono en el Stage (ventana principal)
        primaryStage.getIcons().add(icon);

        // Configuramos la ventana principal
        logger.info("Creacion del titulo");
        logger.warn("Warning");
        primaryStage.setTitle("Tabla de Personas");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**
     * Método principal que lanza la aplicación JavaFX.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}