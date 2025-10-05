package com.Igoresparza.bbdd;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase estática para gestionar la conexión a la base de datos MariaDB.
 * Proporciona el método único para obtener una conexión JDBC.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 */
public class ConexionBBDD {

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
        // DriverManager gestionará la conexión usando la URL, usuario y contraseña.
        // El driver de MySQL/MariaDB se carga automáticamente en JDK modernos.
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}