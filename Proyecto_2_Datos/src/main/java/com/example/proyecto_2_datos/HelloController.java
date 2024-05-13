package com.example.proyecto_2_datos;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class HelloController {
    @FXML
    private TableView<Documento> tablaDocumentos;
    private Biblioteca biblioteca = new Biblioteca();

    @FXML
    private TableColumn<Documento, String> tabla_docu;

    @FXML
    private TextArea areaContenido;

    @FXML
    private void initialize() {
        // Configurar la columna de la tabla para mostrar los nombres de los documentos
        tabla_docu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));

        // Vincular el evento de selección de la tabla para mostrar el contenido del documento
        tablaDocumentos.setOnMouseClicked(this::onDocumentoSeleccionado);
    }

    // Método para manejar el evento de agregar documentos
    @FXML
    private void onAgregarDocumentoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx", "*.txt")
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            for (File selectedFile : selectedFiles) {
                Documento documento = new Documento(selectedFile.getName(), selectedFile.getAbsolutePath());
                try {
                    biblioteca.agregarDocumento(documento);
                } catch (IOException e) {
                    mostrarAlerta("Error", "Error al agregar el documento", e.getMessage());
                }
            }
            tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
        }
    }

    // Método para manejar el evento de agregar una carpeta de documentos
    @FXML
    private void onAgregarCarpetaClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            File[] files = selectedDirectory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String nombre = file.getName();
                        String extension = obtenerExtension(nombre);
                        if (extension.equals("pdf") || extension.equals("docx") || extension.equals("txt")) {
                            Documento documento = new Documento(nombre, file.getAbsolutePath());
                            try {
                                biblioteca.agregarDocumento(documento);
                            } catch (IOException e) {
                                mostrarAlerta("Error", "Error al agregar el documento", e.getMessage());
                            }
                        }
                    }
                }
                tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
            }
        }
    }

    // Método para manejar el evento de eliminar documentos
    @FXML
    private void onEliminarDocumentoClick() {
        Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (documentoSeleccionado != null) {
            biblioteca.eliminarDocumento(documentoSeleccionado);
            tablaDocumentos.getItems().remove(documentoSeleccionado);
        }
    }

    // Método para manejar el evento de seleccionar un documento en la tabla
    private void onDocumentoSeleccionado(MouseEvent event) {
        Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (documentoSeleccionado != null) {
            try {
                String contenido = documentoSeleccionado.obtenerContenido();
                areaContenido.setText(contenido);
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al obtener contenido del documento", e.getMessage());
            }
        }
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para obtener la extensión de un archivo
    private String obtenerExtension(String nombreArchivo) {
        int puntoIndex = nombreArchivo.lastIndexOf(".");
        if (puntoIndex > 0 && puntoIndex < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(puntoIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }
}
