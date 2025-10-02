# TableView_Igor

Aplicación JavaFX que permite gestionar una lista de personas, con funcionalidades de agregar, eliminar y restaurar registros. Los datos se muestran en un `TableView` y se validan antes de ser añadidos.

## Estructura del proyecto

TableView_Igor/
├── src/
│ └── main/
│ ├── java/com/igoresparza/
│ │ ├── Lanzador.java # Clase main que lanza la aplicación
│ │ ├── MainApp.java # Clase que extiende Application
│ │ ├── controladores/
│ │ │ └── ControladorVentana.java
│ │ └── modelo/
│ │ └── Persona.java
│ └── resources/com/igoresparza/fxml/
│ └── tableView.fxml
├── pom.xml
└── target/

markdown
Copiar código

## Requisitos

- JDK 11 o superior (probado con JDK 23)
- JavaFX SDK 17 o superior (probado con 24.0.2)
- Maven 3.8 o superior

## Ejecutar desde IntelliJ

1. Configura el JDK del proyecto a la versión correcta (ej. 23).
2. Configura VM options para JavaFX:  
--module-path C:\Users\IGOR\Downloads\javafx-sdk-24.0.2\lib --add-modules javafx.controls,javafx.fxml

r
Copiar código
3. Ejecuta la clase `Lanzador`.

## Ejecutar desde terminal

1. Abre terminal en la carpeta `target`.
2. Ejecuta el JAR generado por Maven:
```bash
"C:\Program Files\Java\jdk-23\bin\java.exe" -jar TableView_Igor-1.0-SNAPSHOT.jar
Nota: Este JAR funciona desde terminal, pero no se abrirá con doble clic debido a dependencias de JavaFX.

Nota: He probado a crear un jpackage y un .exe para ejecutar el programa pero no lo he logrado

Uso de la aplicación
Introduce First Name, Last Name y Birth Date.

Pulsa Add para agregar la persona a la tabla.

Selecciona filas y pulsa Delete para eliminar.

Pulsa Restore para restaurar los eliminados.

Notas
Los datos no se guardan en disco; permanecen en memoria mientras la aplicación está abierta.

El proyecto usa ObservableList para que los cambios en la tabla se reflejen automáticamente.

Autor
Igor Esparza
