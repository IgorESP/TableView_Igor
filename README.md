# 👨‍👩‍👧‍👦 Gestión de Personas con JavaFX y MariaDB

## 🚀 1. Visión General del Proyecto

Este proyecto es una aplicación de escritorio desarrollada con **JavaFX** y gestionada por **Maven** cuyo objetivo principal es la administración de registros de personas.

Utiliza una arquitectura de **capas (MVC + DAO)** para sincronizar la interfaz gráfica (`TableView`) con una base de datos **MariaDB/MySQL**, permitiendo realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar, Restaurar) de manera persistente.

### Características Clave
* **Interfaz de Usuario:** Desarrollada con JavaFX (FXML y `ControladorVentana`).
* **Persistencia:** Utiliza `PersonaDAO` y `ConexionBBDD` para interactuar con MariaDB.
* **Logging:** Implementación de **SLF4J + Logback** en las 6 clases principales para trazar el flujo de la aplicación y diagnosticar errores.
* **Internacionalización (i18n):** Preparado para múltiples idiomas mediante Resource Bundles (`.properties`).

---

## 🏗️ 2. Arquitectura de Código

El proyecto sigue una estructura limpia basada en paquetes para separar responsabilidades:

| Paquete | Clases Clave | Responsabilidad |
| :--- | :--- | :--- |
| `com.Igoresparza` | `Lanzador`, `MainApp` | Inicialización del entorno JavaFX y punto de entrada. |
| `com.Igoresparza.controladores` | `ControladorVentana` | Lógica de la interfaz de usuario, manejo de eventos y validación inicial. |
| `com.Igoresparza.modelo` | `Persona` | Modelo de datos (entidad), validación de datos y lógica de negocio. |
| `com.Igoresparza.dao` | `PersonaDAO` | Interacción directa con la BBDD (CRUD). |
| `com.Igoresparza.bbdd` | `ConexionBBDD` | Gestión de la conexión JDBC a la BBDD. |

---

## ⚙️ 3. Requisitos y Configuración

### 3.1. Requisitos de Software
* **JDK 11** o superior (Versión configurada en `pom.xml` es la `11`).
* **Maven 3.8+**
* **MariaDB/MySQL Server** (Debe estar corriendo y accesible).

### 3.2. Configuración de la Base de Datos

El archivo `ConexionBBDD.java` contiene los parámetros de conexión. **Antes de ejecutar**, asegúrate de que el servidor MySQL o MariaDB está activo y has creado la base de datos necesaria:

| Parámetro | Valor Configurado | Notas |
| :--- | :--- | :--- |
| **URL** | `jdbc:mysql://localhost:3307/Alumnos` | Se asume que el puerto `3307` está siendo usado (ej. Docker). |
| **Usuario** | `root` | |
| **Contraseña** | `admin` | |

Se requiere la tabla `persona` con al menos las columnas `person_id` (PK, AUTO_INCREMENT), `first_name`, `last_name`, y `birth_date`.

### 3.3. Logging

El registro de eventos se gestiona mediante el archivo de configuración **`src/main/resources/logback.xml`**.

* Los logs de nivel `INFO`, `WARN` y `ERROR` se muestran en la consola.
* Los logs completos (incluyendo `DEBUG`) se guardan en un archivo (configurado dentro de `logback.xml`).

---

## 🛠️ 4. Construcción y Ejecución

### 4.1. Construir el JAR Ejecutable (Maven)

El proyecto utiliza el `maven-jar-plugin` y el `maven-dependency-plugin` para generar un JAR ejecutable que incluye todas las dependencias (`.jar`s de JavaFX, MySQL, SLF4J) en una carpeta interna `libs/`.

---
### 4.2. Ejecutar la Aplicación

Una vez generado el JAR en la carpeta `target/`, la aplicación es completamente portable y se puede ejecutar directamente usando el comando `java -jar`.

Este comando utiliza la clase `com.Igoresparza.Lanzador` como punto de entrada (definida en el `pom.xml`) para lanzar la aplicación JavaFX:

```bash
# Navega a la carpeta raíz del proyecto y ejecuta el JAR generado
java -jar target/TableView_Igor-1.0-SNAPSHOT.jar

```bash
mvn clean install