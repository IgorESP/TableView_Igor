module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.igoresparza.controladores to javafx.fxml;
    opens com.igoresparza.modelo to javafx.base;

    exports com.igoresparza; // <-- exportamos tu paquete principal
}
