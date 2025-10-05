package com.Igoresparza;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase lanzadora principal de la aplicación JavaFX.
 * Esta clase actúa como el punto de entrada principal para el JAR ejecutable (Main-Class en el MANIFEST),
 * delegando la ejecución a la clase {@link MainApp} que extiende Application.
 *
 * <p>Este patrón es esencial para la correcta inicialización de aplicaciones JavaFX
 * con Maven y JDKs recientes.</p>
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 * @see MainApp
 * @see javafx.application.Application
 */
public class Lanzador {

    private static final Logger logger = LoggerFactory.getLogger(Lanzador.class);

    /**
     * Punto de entrada principal para el JAR ejecutable de la aplicación.
     * Delega la ejecución a la clase principal de JavaFX, {@link MainApp}.
     *
     * @param args Argumentos de línea de comandos pasados a la aplicación.
     */
    public static void main(String[] args) {
        logger.info("El Lanzador ha comenzado. Delegando el inicio a MainApp.");
        MainApp.main(args);
        logger.info("Aplicación finalizada o cerrada desde el entorno JavaFX.");
    }
}