package com.example.proyecto_2_datos;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.web.*;
import org.apache.commons.text.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.regex.Pattern;

public class HelloController {
    @FXML
    private TableView<Documento> tablaDocumentos;
    private Biblioteca biblioteca = new Biblioteca();
    private AVLTree arbolAVL; 

    @FXML
    private TableColumn<Documento, String> tabla_docu;

    @FXML
    private TextArea areaContenido;

    @FXML
    private Button botonBuscar;

    @FXML
    private TextField campoBusqueda;
    @FXML
    private WebView vistaContenido;

    @FXML
    private Label actual;

    @FXML
    private void initialize() {
        // Configurar la columna de la tabla para mostrar los nombres de los documentos
        tabla_docu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));

        // Vincular el evento de selección de la tabla para mostrar el contenido del documento
        tablaDocumentos.setOnMouseClicked(this::onDocumentoSeleccionado);

        arbolAVL = biblioteca.getArbolAVL();
    }

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
                String rutaArchivo = selectedFile.getAbsolutePath();
                // Verificar si la ruta del archivo ya ha sido agregada anteriormente
                if (!biblioteca.existeDocumentoConRuta(rutaArchivo)) {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(selectedFile.toPath(), BasicFileAttributes.class);
                        LocalDateTime fechaCreacion = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
                        long tamaño = attrs.size();
                        Documento documento = new Documento(selectedFile.getName(), rutaArchivo, fechaCreacion, tamaño);
                        biblioteca.agregarDocumento(documento);
                    } catch (IOException e) {
                        mostrarAlerta("Error", "Error al agregar el documento", e.getMessage());
                    }
                }
            }
            tablaDocumentos.getItems().clear(); // Limpiar la tabla antes de agregar los documentos nuevamente
            tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
        }
    }

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
                            try {
                                BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                                LocalDateTime fechaCreacion = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
                                long tamaño = attrs.size();
                                Documento documento = new Documento(nombre, file.getAbsolutePath(), fechaCreacion, tamaño);
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

    @FXML
    private void onEliminarDocumentoClick() {
        Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (documentoSeleccionado != null) {
            biblioteca.eliminarDocumento(documentoSeleccionado);
            tablaDocumentos.getItems().remove(documentoSeleccionado);
        }
    }

    @FXML
    private void onDocumentoSeleccionado(MouseEvent event) {
        Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
        if (documentoSeleccionado != null) {
            cargarContenidoDocumento(documentoSeleccionado);
            actual.setText(documentoSeleccionado.getNombre());
        }
    }

    @FXML
    private void onSiguienteClick() {
        int currentIndex = tablaDocumentos.getSelectionModel().getSelectedIndex();
        if (currentIndex < tablaDocumentos.getItems().size() - 1) {
            tablaDocumentos.getSelectionModel().select(currentIndex + 1);
            Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
            cargarContenidoDocumento(documentoSeleccionado);
            actual.setText(documentoSeleccionado.getNombre());
        }
    }

    @FXML
    private void onAnteriorClick() {
        int currentIndex = tablaDocumentos.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            tablaDocumentos.getSelectionModel().select(currentIndex - 1);
            Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();
            cargarContenidoDocumento(documentoSeleccionado);
            actual.setText(documentoSeleccionado.getNombre());
        }
    }

    @FXML
    private void onActualizarClick() {
        for (Documento documento : tablaDocumentos.getItems()) {
            try {
                BasicFileAttributes attrs = Files.readAttributes(new File(documento.getRuta()).toPath(), BasicFileAttributes.class);
                LocalDateTime fechaCreacion = LocalDateTime.ofInstant(attrs.creationTime().toInstant(), ZoneId.systemDefault());
                long tamaño = attrs.size();
                Documento documentoNuevo = new Documento(documento.getNombre(), documento.getRuta(), fechaCreacion, tamaño);
                biblioteca.actualizarDocumento(documento, documentoNuevo);
            } catch (IOException e) {
                mostrarAlerta("Error", "Error al actualizar el documento", e.getMessage());
            }
        }
    }

    private void cargarContenidoDocumento(Documento documento) {
        try {
            String contenido = documento.obtenerContenido();
            contenido = StringEscapeUtils.escapeHtml4(contenido);
            contenido = contenido.replaceAll("\n", "<br>"); // Reemplazar saltos de línea con etiquetas <br>
            String palabraBuscada = campoBusqueda.getText().trim();
            if (!palabraBuscada.isEmpty()) {
                contenido = resaltarPalabrasOFrases(contenido, palabraBuscada);
            }
            contenido = "<html><body>" + contenido + "</body></html>";
            System.out.println("Contenido HTML: " + contenido); // Verificar el contenido en la consola
            vistaContenido.getEngine().loadContent(contenido);
        } catch (IOException e) {
            mostrarAlerta("Error", "Error al obtener contenido del documento", e.getMessage());
        } catch (Exception ex) {
            // Capturar cualquier excepción y mostrarla en una alerta
            mostrarAlerta("Error", "Error al cargar contenido en el WebView", ex.getMessage());
        }
    }

    private String resaltarPalabrasOFrases(String contenido, String palabraBuscada) {
        // Asegurarse de escapar caracteres especiales en la palabra o frase buscada
        String regex = "(?i)(" + Pattern.quote(palabraBuscada) + ")";
        return contenido.replaceAll(regex, "<mark>$1</mark>");
    }

    private void mostrarAlerta(String titulo, String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String obtenerExtension(String nombreArchivo) {
        int puntoIndex = nombreArchivo.lastIndexOf(".");
        if (puntoIndex > 0 && puntoIndex < nombreArchivo.length() - 1) {
            return nombreArchivo.substring(puntoIndex + 1).toLowerCase();
        } else {
            return "";
        }
    }

    @FXML
    private void onBuscarPalabraClick() {
        String palabra = campoBusqueda.getText().trim();
        if (!palabra.isEmpty()) {
            // Actualizar el contenido del documento con la nueva palabra buscada
            cargarContenidoDocumento(tablaDocumentos.getSelectionModel().getSelectedItem());
            // Resaltar las palabras buscadas nuevamente
            resaltarPalabrasEnDocumento(palabra);
        } else {
            mostrarAlerta("Advertencia", "Campo vacío", "Por favor, ingrese una palabra para buscar.");
        }
    }

    private void resaltarPalabrasEnDocumento(String palabraBuscada) {
        String contenido = vistaContenido.getEngine().getDocument().toString();
        contenido = StringEscapeUtils.unescapeHtml4(contenido); // Deshacer el escape del contenido HTML
        contenido = resaltarPalabrasOFrases(contenido, palabraBuscada);
        contenido = "<html><body>" + contenido + "</body></html>";
        vistaContenido.getEngine().loadContent(contenido);
    }

    @FXML
    private void ordenarPorTamano() {
        biblioteca.ordenarporTamaño();
        tablaDocumentos.getItems().clear();
        tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
    }

    @FXML
    private void ordenarPorFecha() {
        biblioteca.ordenarporFechaCreacion();
        tablaDocumentos.getItems().clear();
        tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
    }

    @FXML
    private void ordenarPorNombre() {
        biblioteca.ordenarporNombre();
        tablaDocumentos.getItems().clear();
        tablaDocumentos.getItems().addAll(biblioteca.getDocumentos());
    }

    
}
