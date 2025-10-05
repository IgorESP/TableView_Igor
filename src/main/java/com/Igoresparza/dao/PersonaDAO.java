package com.Igoresparza.dao;

import com.Igoresparza.bbdd.ConexionBBDD;
import com.Igoresparza.modelo.Persona;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Lee todas las personas de la base de datos y las mapea a una lista de objetos Persona.
     *
     * @return Una lista con todas las personas de la BDD.
     */
    public List<Persona> getAllPersonas() {
        List<Persona> personas = new ArrayList<>();
        final String SQL = "SELECT person_id, first_name, last_name, birth_date FROM persona ORDER BY person_id";

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
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las personas: " + e.getMessage());
            e.printStackTrace();
        }
        return personas;
    }

    /**
     * Inserta una nueva persona en la BDD.
     * El objeto Persona de entrada se actualiza con el ID generado por la base de datos (AUTO_INCREMENT).
     *
     * @param p El objeto Persona a insertar.
     * @return La persona con su ID actualizado, o null si la inserción falló.
     */
    public Persona insertPersona(Persona p) {
        final String SQL = "INSERT INTO persona (first_name, last_name, birth_date) VALUES (?, ?, ?)";
        try (Connection conn = ConexionBBDD.getConnection();
             // CLAVE: Solicitamos a la BDD que devuelva las claves generadas (el ID)
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, p.getFirstName());
            pstmt.setString(2, p.getLastName());
            pstmt.setObject(3, p.getBirthDate());

            pstmt.executeUpdate();

            // Recuperar el ID asignado por MariaDB
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setPersonId(rs.getInt(1));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar persona: " + e.getMessage());
            e.printStackTrace();
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
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, personId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar persona: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}