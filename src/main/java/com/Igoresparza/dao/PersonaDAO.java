package com.Igoresparza.dao;

import com.Igoresparza.bbdd.ConexionBBDD;
import com.Igoresparza.modelo.Persona;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data Access Object (DAO) para la clase Persona.
 * Proporciona métodos para sincronizar el estado de los objetos Persona
 * con la tabla 'persona' de la base de datos MariaDB.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 */
public class PersonaDAO {

    private static final Logger logger = LoggerFactory.getLogger(PersonaDAO.class);

    /**
     * Lee todas las personas de la base de datos y las mapea a una lista de objetos Persona.
     *
     * @return Una lista con todas las personas de la BDD.
     */
    public List<Persona> getAllPersonas() {
        List<Persona> personas = new ArrayList<>();
        final String SQL = "SELECT person_id, first_name, last_name, birth_date FROM persona ORDER BY person_id";

        logger.debug("Ejecutando consulta: {}", SQL);

        // Usamos try-with-resources para asegurar el cierre automático de recursos (Connection, Statement, ResultSet)
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL)) {

            while (rs.next()) {
                Persona p = new Persona(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        // Uso seguro de fechas: rs.getObject(..., LocalDate.class)
                        rs.getObject("birth_date", LocalDate.class)
                );
                p.setPersonId(rs.getInt("person_id"));
                personas.add(p);
            }
            logger.info("Consulta exitosa. Se recuperaron {} registros de la tabla 'persona'.", personas.size());
        } catch (SQLException e) {
            logger.error("ERROR al obtener todas las personas de la BDD.", e);
        }
        return personas;
    }

    /**
     * Inserta una nueva persona en la base de datos.
     * El objeto Persona pasado se actualiza con el ID autogenerado.
     *
     * @param p El objeto Persona a insertar.
     * @return El objeto Persona con el ID actualizado si la inserción fue exitosa, o null en caso de error.
     */
    public Persona insertPersona(Persona p) {
        final String SQL = "INSERT INTO persona (first_name, last_name, birth_date) VALUES (?, ?, ?)";

        logger.debug("Intentando insertar nueva persona: {} {}", p.getFirstName(), p.getLastName());

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

            // Recuperar el ID asignado por MariaDB
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
            logger.error("ERROR al insertar persona {} en la BDD.", p.getFirstName(), e);
        }
        return null;
    }

    /**
     * Elimina una persona por su ID de la base de datos.
     *
     * @param personId El ID de la persona a eliminar.
     * @return true si se eliminó al menos una fila, false en caso contrario.
     */
    public boolean deletePersona(int personId) {
        final String SQL = "DELETE FROM persona WHERE person_id = ?";

        logger.debug("Intentando eliminar persona con ID: {}", personId);

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
            logger.error("ERROR al eliminar persona ID {} de la BDD.", personId, e);
            return false;
        }
    }
}