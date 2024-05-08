module com.example.proyecto_2_datos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.proyecto_2_datos to javafx.fxml;
    exports com.example.proyecto_2_datos;
}