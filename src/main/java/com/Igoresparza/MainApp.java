package com.Igoresparza;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import javafx.scene.image.Image; // Añadido para manejo de Image

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

    // Logger
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
        logger.info("Iniciando el método start(Stage).");

        try {
            logger.debug("Cargando archivo FXML: fxml/tableView.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/tableView.fxml"));
            Parent root = loader.load();
            logger.info("Vista FXML cargada correctamente.");

            // Lógica del icono
            try {
                logger.debug("Intentando cargar el icono de la ventana.");
                // Asumiendo la ruta corregida del diálogo anterior
                Image icon = new Image(getClass().getResourceAsStream("imagenes/imagen_logo.png"));
                primaryStage.getIcons().add(icon);
                logger.debug("Icono de la ventana establecido.");
            } catch (Exception e) {
                // Capturará el NullPointerException si el recurso no existe
                logger.warn("No se pudo cargar o establecer el icono de la ventana. (Ruta: imagenes/imagen_logo.png)", e);
            }

            // Configuramos la ventana principal
            primaryStage.setTitle("Tabla de Personas");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.show();
            logger.info("Ventana principal mostrada con éxito.");

        } catch (Exception e) {
            logger.error("Error FATAL durante la inicialización o carga del FXML.", e);
            throw e; // Relanzar la excepción para que el programa falle visiblemente si no puede arrancar
        }
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