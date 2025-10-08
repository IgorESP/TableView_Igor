package com.Igoresparza.bbdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream; // NUEVO
import java.util.Properties; // NUEVO

/**
 * Clase estática para gestionar la conexión a la base de datos MariaDB.
 * Ahora carga las credenciales desde el archivo application.properties.
 *
 * @author Igor Esparza
 * @version 1.1
 * @since 2025-10-04
 */
public class ConexionBBDD {

    private static final Logger logger = LoggerFactory.getLogger(ConexionBBDD.class);
    private static final Properties PROPS = new Properties();

    // Las credenciales ya NO están codificadas aquí.
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Bloque estático: Carga las propiedades al iniciar la clase (¡La solución al punto del profesor!).
    static {
        // Intentamos cargar el archivo de configuración.
        try (InputStream input = ConexionBBDD.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warn("No se encontró el archivo application.properties. Usando valores por defecto (SOLO PARA PRUEBAS).");
                // Valores de fallback (tus valores originales, por si acaso)
                URL = "jdbc:mysql://localhost:3307/Alumnos";
                USER = "root";
                PASSWORD = "admin";
            } else {
                PROPS.load(input);
                // Leemos del archivo, si no existen las propiedades, usamos los valores por defecto.
                URL = PROPS.getProperty("db.url", "jdbc:mysql://localhost:3307/Alumnos");
                USER = PROPS.getProperty("db.user", "root");
                PASSWORD = PROPS.getProperty("db.password", "admin");
                logger.info("Configuración de la BDD cargada desde application.properties.");
            }
        } catch (Exception ex) {
            logger.error("Error al cargar o parsear application.properties.", ex);
            // Si hay un error, usamos los valores por defecto para evitar una caída total
            URL = "jdbc:mysql://localhost:3307/Alumnos";
            USER = "root";
            PASSWORD = "admin";
        }
    }

    /**
     * Establece y devuelve una nueva conexión con la base de datos.
     *
     * @return Objeto Connection activo.
     * @throws SQLException Si ocurre un error de conexión.
     */
    public static Connection getConnection() throws SQLException {
        logger.debug("Intentando establecer conexión a la BDD: {}", URL);
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Conexión a la BDD establecida con éxito.");
            return connection;
        } catch (SQLException e) {
            logger.error("ERROR: Fallo al conectar con la BDD. Revise el servicio/credenciales.", e);
            throw e;
        }
    }
}