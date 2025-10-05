package com.Igoresparza.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que representa el modelo de datos para una persona en la aplicación.
 * Incluye datos de identificación, nombre, fecha de nacimiento y lógica de validación.
 *
 * @author Igor Esparza
 * @version 1.0
 * @since 2025-10-04
 */
public class Persona {
    /**
     * Secuencia atómica utilizada para generar identificadores únicos de persona.
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
    }

    /** Obtiene el ID de la persona. */
    public int getPersonId() { return personId; }
    /** Establece el ID de la persona. */
    public void setPersonId(int personId) { this.personId = personId; }

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
     * Verifica si la fecha de nacimiento proporcionada es válida, añadiendo errores a una lista mutable.
     * @param bdate Fecha de nacimiento a validar.
     * @param errorList Lista donde se añadirán los mensajes de error.
     * @return true si la fecha es válida o nula, false si está en el futuro.
     */
    public boolean isValidBirthDate(LocalDate bdate, List<String> errorList) {
        if (bdate == null) return true;
        if (bdate.isAfter(LocalDate.now())) {
            errorList.add("Birth date must not be in future.");
            return false;
        }
        return true;
    }

    /**
     * Verifica si la persona actual es válida (nombre y apellido no vacíos y fecha de nacimiento válida).
     * @param errorList Lista donde se añadirán los mensajes de error.
     * @return true si la persona es válida, false en caso contrario.
     */
    public boolean isValidPerson(List<String> errorList) {
        return isValidPerson(this, errorList);
    }

    /**
     * Verifica si un objeto Persona dado es válido, añadiendo errores a una lista mutable.
     * Valida nombre, apellido y fecha de nacimiento.
     * @param p Objeto Persona a validar.
     * @param errorList Lista donde se añadirán los mensajes de error.
     * @return true si la persona es válida, false en caso contrario.
     */
    public boolean isValidPerson(Persona p, List<String> errorList) {
        boolean isValid = true;
        String fn = p.getFirstName();
        if (fn == null || fn.trim().length() == 0) {
            errorList.add("First name must contain minimum one character.");
            isValid = false;
        }
        String ln = p.getLastName();
        if (ln == null || ln.trim().length() == 0) {
            errorList.add("Last name must contain minimum one character.");
            isValid = false;
        }
        if (!isValidBirthDate(p.getBirthDate(), errorList)) {
            isValid = false;
        }
        return isValid;
    }

    /**
     * Calcula la categoría de edad de la persona basándose en su fecha de nacimiento.
     * @return La categoría de edad (BABY, CHILD, TEEN, ADULT, SENIOR, UNKNOWN).
     */
    public AgeCategory getAgeCategory() {
        if (birthDate == null) return AgeCategory.UNKNOWN;
        long years = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        if (years >= 0 && years < 2) return AgeCategory.BABY;
        if (years >= 2 && years < 13) return AgeCategory.CHILD;
        if (years >= 13 && years <= 19) return AgeCategory.TEEN;
        if (years > 19 && years <= 50) return AgeCategory.ADULT;
        if (years > 50) return AgeCategory.SENIOR;
        return AgeCategory.UNKNOWN;
    }

    /**
     * Intenta simular el guardado de la persona. Solo procede si la persona es válida.
     * @param errorList Lista donde se añadirán los mensajes de error de validación.
     * @return true si la persona fue considerada válida (y se "guardó"), false en caso contrario.
     */
    public boolean save(List<String> errorList) {
        boolean isSaved = false;
        if (isValidPerson(errorList)) {
            System.out.println("Saved " + this.toString());
            isSaved = true;
        }
        return isSaved;
    }

    /**
     * Devuelve una representación en cadena de la Persona.
     * @return Una cadena que contiene el ID, nombre, apellido y fecha de nacimiento.
     */
    @Override
    public String toString() {
        return "[personId=" + personId + ", firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + "]";
    }
}