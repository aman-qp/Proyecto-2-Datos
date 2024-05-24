package com.example.proyecto_2_datos;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Objects;



public class Documento {
    private String nombre;
    private String ruta; // Ruta del archivo asociado
    private long tamaño;
    private LocalDateTime fechaCreacion;


    public Documento(String nombre, String ruta, LocalDateTime fechaCreacion, long tamaño) {
        this.nombre = nombre;
        this.ruta = ruta;
        this.fechaCreacion = fechaCreacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public long getTamaño() {
        return tamaño;
    }


    // Método para obtener el contenido del documento
    public String obtenerContenido() throws IOException {
        StringBuilder contenido = new StringBuilder();
        if (ruta != null) {
            File archivo = new File(ruta);
            String extension = obtenerExtension(nombre);
            if (extension.equals("pdf")) {
                contenido.append(obtenerContenidoPDF(archivo));
            } else if (extension.equals("docx")) {
                contenido.append(obtenerContenidoDOCX(archivo));
            } else if (extension.equals("txt")) {
                contenido.append(obtenerContenidoTXT(archivo));
            } else {
                contenido.append("Tipo de archivo no compatible.");
            }
        }
        return contenido.toString();
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

    // Método para obtener el contenido de un archivo .docx
    private String obtenerContenidoDOCX(File archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        FileInputStream fis = new FileInputStream(archivo);
        XWPFDocument docx = new XWPFDocument(fis);
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        contenido.append(extractor.getText());
        extractor.close();
        docx.close();
        fis.close();
        return contenido.toString();
    }

    // Método para obtener el contenido de un archivo .txt
    private String obtenerContenidoTXT(File archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(archivo));
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
    private String obtenerContenidoPDF(File archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        PDDocument document = null;
        try {
            document = PDDocument.load(archivo);
            PDFTextStripper stripper = new PDFTextStripper();
            contenido.append(stripper.getText(document));
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return contenido.toString();
    }
 
}
