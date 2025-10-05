package com.Igoresparza.bbdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase estática para gestionar la conexión a la base de datos MariaDB.
 * Proporciona el método único para obtener una conexión JDBC.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 */
public class ConexionBBDD {

    private static final Logger logger = LoggerFactory.getLogger(ConexionBBDD.class);

    // Contenedor Docker de MariaDB
    private static final String URL = "jdbc:mysql://localhost:3307/Alumnos";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    /**
     * Establece y devuelve una nueva conexión con la base de datos.
     *
     * @return Objeto Connection activo.
     * @throws SQLException Si ocurre un error de conexión (credenciales, URL, base de datos no disponible, etc.).
     */
    public static Connection getConnection() throws SQLException {
        logger.debug("Intentando establecer conexión a la BDD: {}", URL);
        try {
            // DriverManager gestionará la conexión usando la URL, usuario y contraseña.
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Conexión a la BDD establecida con éxito.");
            return connection;
        } catch (SQLException e) {
            logger.error("ERROR: Fallo al conectar con la BDD. Revise el servicio/credenciales.", e);
            // Re-lanza la excepción para que el DAO pueda manejarla
            throw e;
        }
    }
}