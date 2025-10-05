package com.Igoresparza.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase que representa el modelo de datos para una persona en la aplicación.
 * Incluye datos de identificación, nombre, fecha de nacimiento y lógica de validación.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 */
public class Persona {

    private static final Logger logger = LoggerFactory.getLogger(Persona.class);

    /**
     * Secuencia atómica utilizada para generar los identificadores únicos de persona.
     */
    public static AtomicInteger personSequence = new AtomicInteger(0);
    private int personId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    /**
     * Enumeración que clasifica la edad de la persona en categorías.
     */
    public enum AgeCategory {
        BABY, CHILD, TEEN, ADULT, SENIOR, UNKNOWN
    }

    /**
     * Constructor por defecto que inicializa una persona con valores nulos.
     */
    public Persona() {
        this(null, null, null);
        logger.debug("Creación de objeto Persona con constructor por defecto (nulls).");
    }

    /**
     * Constructor que inicializa una persona con nombre, apellido y fecha de nacimiento.
     * @param firstName Nombre de la persona.
     * @param lastName Apellido de la persona.
     * @param birthDate Fecha de nacimiento de la persona.
     */
    public Persona(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        logger.debug("Creación de objeto Persona: {} {}", firstName, lastName);
    }

    // --- Getters y Setters omitidos por brevedad ---

    /** Obtiene el ID de la persona. */
    public int getPersonId() { return personId; }
    /** Establece el ID de la persona. */
    public void setPersonId(int personId) {
        this.personId = personId;
        logger.debug("ID de Persona establecido a {}.", personId);
    }

    /** Obtiene el nombre de la persona. */
    public String getFirstName() { return firstName; }
    /** Establece el nombre de la persona. */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** Obtiene el apellido de la persona. */
    public String getLastName() { return lastName; }
    /** Establece el apellido de la persona. */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /** Obtiene la fecha de nacimiento de la persona. */
    public LocalDate getBirthDate() { return birthDate; }
    /** Establece la fecha de nacimiento de la persona. */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }


    /**
     * Verifica si la fecha de nacimiento proporcionada es válida (no está en el futuro).
     * @param bdate Fecha de nacimiento a validar.
     * @return true si la fecha es válida o nula, false si está en el futuro.
     */
    public boolean isValidBirthDate(LocalDate bdate) {
        return isValidBirthDate(bdate, new ArrayList<>());
    }

    /**
     * Verifica si la fecha de nacimiento proporcionada es válida (no está en el futuro).
     * @param bdate Fecha de nacimiento a validar.
     * @param errorList Lista donde se añadirán los mensajes de error de validación.
     * @return true si la fecha es válida o nula, false si está en el futuro.
     */
    public boolean isValidBirthDate(LocalDate bdate, List<String> errorList) {
        if (bdate != null && bdate.isAfter(LocalDate.now())) {
            errorList.add("The birth date cannot be in the future.");
            logger.warn("Validación fallida: Fecha de nacimiento en el futuro: {}", bdate);
            return false;
        }
        return true;
    }

    /**
     * Realiza una validación completa de todos los campos de la persona.
     * @param errorList Lista mutable donde se añadirán los mensajes de error de validación.
     * @return true si la persona es válida, false en caso contrario.
     */
    public boolean isValidPerson(List<String> errorList) {
        logger.debug("Iniciando validación de la Persona: {}", this.toString());
        boolean isValid = true;

        // El ID no se valida ya que se asigna en la BDD.

        String fn = this.getFirstName();
        if (fn == null || fn.trim().length() == 0) {
            errorList.add("First name must contain minimum one character.");
            isValid = false;
        }
        String ln = this.getLastName();
        if (ln == null || ln.trim().length() == 0) {
            errorList.add("Last name must contain minimum one character.");
            isValid = false;
        }

        if (!isValidBirthDate(this.getBirthDate(), errorList)) {
            isValid = false;
        }

        if (isValid) {
            logger.debug("Validación de persona completada: Éxito.");
        } else {
            logger.warn("Validación de persona completada: FALLO. Errores: {}", errorList);
        }
        return isValid;
    }

    /**
     * Obtiene la clasificación de edad de la persona
     * (BABY, CHILD, TEEN, ADULT, SENIOR, UNKNOWN).
     */
    public AgeCategory getAgeCategory() {
        if (birthDate == null) {
            logger.debug("Categoría de edad: UNKNOWN (Fecha de nacimiento nula).");
            return AgeCategory.UNKNOWN;
        }
        long years = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        AgeCategory category;

        if (years >= 0 && years < 2) category = AgeCategory.BABY;
        else if (years >= 2 && years < 13) category = AgeCategory.CHILD;
        else if (years >= 13 && years <= 19) category = AgeCategory.TEEN;
        else if (years > 19 && years <= 50) category = AgeCategory.ADULT;
        else if (years > 50) category = AgeCategory.SENIOR;
        else category = AgeCategory.UNKNOWN;

        logger.debug("Categoría de edad calculada: {} (Edad: {} años).", category, years);
        return category;
    }

    /**
     * Intenta simular el guardado de la persona. Solo procede si la persona es válida.
     * @param errorList Lista donde se añadirán los mensajes de error de validación.
     * @return true si la persona fue considerada válida (y se "guardó"), false en caso contrario.
     */
    public boolean save(List<String> errorList) {
        boolean isSaved = false;
        if (isValidPerson(errorList)) {
            logger.info("Simulación de guardado exitosa para: {}", this.toString());
            isSaved = true;
        } else {
            logger.warn("Simulación de guardado fallida debido a errores de validación.");
        }
        return isSaved;
    }

    /**
     * Devuelve una representación en cadena de la Persona.
     * @return Una cadena que contiene el ID, nombre y apellido.
     */
    @Override
    public String toString() {
        return "Persona{" +
                "personId=" + personId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                '}';
    }
}