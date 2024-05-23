module com.example.proyecto_2_datos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires javafx.web;


    opens com.example.proyecto_2_datos to javafx.fxml;
    exports com.example.proyecto_2_datos;
}