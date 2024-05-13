package com.example.proyecto_2_datos;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class HelloController {
    @FXML
    private TableView<Documento> tablaDocumentos;
    private Biblioteca biblioteca = new Biblioteca();

    @FXML
    private TableColumn<Documento, String> tabla_docu;

    @FXML
    private Button ag_docu;

    @FXML
    private Button eli_docu;

    @FXML
    private TextArea areaContenido;

    @FXML
    private void initialize() {
        // Configurar la columna de la tabla para mostrar los nombres de los documentos
        tabla_docu.setCellValueFactory(cellData -> {
            String nombre = cellData.getValue().getNombre();
            return new SimpleStringProperty(nombre);
        });

        // Manejar la selección de documentos en la tabla
        tablaDocumentos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Obtener el contenido del documento seleccionado
                String contenido = obtenerContenidoDocumento(newValue);
                // Mostrar el contenido en el área de texto
                areaContenido.setText(contenido);
            }
        });
    }

    // Método para obtener el contenido de un documento
    private String obtenerContenidoDocumento(Documento documento) {
        StringBuilder contenido = new StringBuilder();
        try {
            // Obtener la ruta del archivo asociado al documento
            String rutaArchivo = obtenerRutaDocumento(documento);

            // Verificar el tipo de archivo y obtener su contenido
            if (rutaArchivo.endsWith(".pdf")) {
                contenido.append(obtenerContenidoPDF(rutaArchivo));
            } else if (rutaArchivo.endsWith(".docx")) {
                contenido.append(obtenerContenidoDOCX(rutaArchivo));
            } else if (rutaArchivo.endsWith(".txt")) {
                contenido.append(obtenerContenidoTXT(rutaArchivo));
            } else {
                contenido.append("Tipo de archivo no compatible.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            contenido.append("Error al leer el archivo.");
        }
        return contenido.toString();
    }

    // Método para obtener el contenido de un archivo .docx
    private String obtenerContenidoDOCX(String rutaArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        FileInputStream fis = new FileInputStream(rutaArchivo);
        XWPFDocument docx = new XWPFDocument(fis);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        contenido.append(extractor.getText());
        extractor.close();
        docx.close();
        fis.close();
        return contenido.toString();
    }

    // Método para obtener el contenido de un archivo .txt
    private String obtenerContenidoTXT(String rutaArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(rutaArchivo));
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } finally {
            if (lector != null) {
                lector.close();
            }
        }
        return contenido.toString();
    }

    // Método para obtener el contenido de un archivo PDF
    private String obtenerContenidoPDF(String rutaArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        PDDocument document = null;
        try {
            document = PDDocument.load(new File(rutaArchivo));
            PDFTextStripper stripper = new PDFTextStripper();
            contenido.append(stripper.getText(document));
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return contenido.toString();
    }


    private String obtenerRutaDocumento(Documento documento) {
        return documento.getRuta();
    }

    @FXML
    private void onAgregarDocumentoClick() {
        // Mostrar el diálogo para seleccionar archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar documento");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Directorio inicial
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documentos", "*.pdf", "*.docx", "*.txt") // Filtros de extensiones
        );
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if (selectedFiles != null) {
            for (File selectedFile : selectedFiles) {
                agregarDocumento(selectedFile);
            }
        }
    }

    @FXML
    private void onAgregarCarpetaClick() {
        // Mostrar el diálogo para seleccionar carpetas
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // Directorio inicial
        File selectedFolder = directoryChooser.showDialog(null);

        if (selectedFolder != null) {
            // Agregar todos los archivos dentro de la carpeta seleccionada
            File[] filesInFolder = selectedFolder.listFiles();
            if (filesInFolder != null) {
                for (File file : filesInFolder) {
                    agregarDocumento(file);
                }
            }
        }
    }

    // Método para agregar un documento a la biblioteca
    private void agregarDocumento(File archivo) {
        String nombreArchivo = archivo.getName();
        String extension = obtenerExtension(nombreArchivo);

        if (extension.equals("pdf") || extension.equals("docx") || extension.equals("txt")) {
            Documento documento = new Documento(nombreArchivo);
            documento.setRuta(archivo.getAbsolutePath());
            biblioteca.agregarDocumento(documento);
            tablaDocumentos.getItems().add(documento);
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Formato no admitido");
            alert.setHeaderText(null);
            alert.setContentText("Solo se pueden agregar archivos con extensiones .pdf, .docx o .txt.");
            alert.showAndWait();
        }
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

    @FXML
    private void onEliminarDocumentoClick() {
        // Obtener el documento seleccionado en la tabla
        Documento documentoSeleccionado = tablaDocumentos.getSelectionModel().getSelectedItem();

        if (documentoSeleccionado != null) {
            // Eliminar el documento de la tabla
            tablaDocumentos.getItems().remove(documentoSeleccionado);

            // Aquí puedes agregar lógica adicional, como eliminar el documento de la biblioteca
        }
    }
}
