package com.Igoresparza.dao;

import com.Igoresparza.bbdd.ConexionBBDD;
import com.Igoresparza.modelo.Persona;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*; // IMPORTANTE: NUEVAS IMPORTACIONES
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) para la clase Persona, ahora con soporte ASÍNCRONO.
 *
 * @author Igor Esparza
 * @version 1.1
 * @since 2025-10-04
 */
public class PersonaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PersonaDAO.class);
    // [NUEVO] Pool de hilos para ejecutar operaciones de BDD de forma asíncrona.
    private static final ExecutorService dbExecutor = Executors.newFixedThreadPool(5);

    // --- MÉTODOS ASÍNCRONOS (Public) ---

    /**
     * [ASÍNCRONO] Lee todas las personas de la base de datos.
     * @return Un Future que devolverá la lista de Personas.
     */
    public Future<List<Persona>> getAllPersonasAsync() {
        return dbExecutor.submit(this::getAllPersonasSync);
    }

    /**
     * [ASÍNCRONO] Inserta una nueva persona en la base de datos.
     * @param p El objeto Persona a insertar.
     * @return Un Future que devolverá el objeto Persona con el ID actualizado, o null si falla.
     */
    public Future<Persona> insertPersonaAsync(Persona p) {
        return dbExecutor.submit(() -> insertPersonaSync(p));
    }

    /**
     * [ASÍNCRONO] Elimina una persona por su ID de la base de datos.
     * @param personId El ID de la persona a eliminar.
     * @return Un Future que devolverá true si se eliminó al menos una fila, false en caso contrario.
     */
    public Future<Boolean> deletePersonaAsync(int personId) {
        return dbExecutor.submit(() -> deletePersonaSync(personId));
    }

    // --- MÉTODOS SÍNCRONOS (Internal, ahora llamados por los métodos Async) ---

    /**
     * [SÍNCRONO] Implementación interna para leer todas las personas.
     */
    private List<Persona> getAllPersonasSync() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        final String SQL = "SELECT person_id, first_name, last_name, birth_date FROM persona ORDER BY person_id";
        logger.debug("Ejecutando consulta SÍNCRONA: {}", SQL);

        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Persona p = new Persona(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getObject("birth_date", LocalDate.class)
                );
                p.setPersonId(rs.getInt("person_id"));
                personas.add(p);
            }
            logger.info("Consulta exitosa. Se recuperaron {} registros de la tabla 'persona'.", personas.size());
        } catch (SQLException e) {
            logger.error("ERROR SÍNCRONO al obtener todas las personas de la BDD.", e);
            throw e; // Re-lanzar para que el Callable lo capture en el thread de background
        }
        return personas;
    }

    /**
     * [SÍNCRONO] Implementación interna para insertar una nueva persona.
     */
    private Persona insertPersonaSync(Persona p) throws SQLException {
        final String SQL = "INSERT INTO persona (first_name, last_name, birth_date) VALUES (?, ?, ?)";
        logger.debug("Intentando insertar nueva persona SÍNCRONAMENTE: {} {}", p.getFirstName(), p.getLastName());

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, p.getFirstName());
            pstmt.setString(2, p.getLastName());
            pstmt.setObject(3, p.getBirthDate());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                logger.warn("Inserción fallida, 0 filas afectadas para la persona: {}", p.getFirstName());
                return null;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    p.setPersonId(newId);
                    logger.info("Inserción exitosa. Nueva persona con ID: {}", newId);
                    return p;
                }
                logger.error("Fallo al obtener el ID generado después de la inserción.");
            }
        } catch (SQLException e) {
            logger.error("ERROR SÍNCRONO al insertar persona {} en la BDD.", p.getFirstName(), e);
            throw e;
        }
        return null;
    }

    /**
     * [SÍNCRONO] Implementación interna para eliminar una persona por su ID.
     */
    private boolean deletePersonaSync(int personId) throws SQLException {
        final String SQL = "DELETE FROM persona WHERE person_id = ?";
        logger.debug("Intentando eliminar persona SÍNCRONAMENTE con ID: {}", personId);

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, personId);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Borrado exitoso. Persona ID {} eliminada de la BDD.", personId);
                return true;
            } else {
                logger.warn("Borrado fallido. Persona ID {} no encontrada en la BDD (0 filas afectadas).", personId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("ERROR SÍNCRONO al eliminar persona ID {} de la BDD.", personId, e);
            throw e;
        }
    }

    /**
     * Cierra el pool de hilos utilizado para las operaciones de base de datos.
     * Debe ser llamado al cerrar la aplicación principal (en el método stop()).
     */
    public static void shutdown() {
        if (!dbExecutor.isShutdown()) {
            dbExecutor.shutdown();
            logger.info("El pool de hilos del PersonaDAO se ha cerrado.");
        }
    }
}