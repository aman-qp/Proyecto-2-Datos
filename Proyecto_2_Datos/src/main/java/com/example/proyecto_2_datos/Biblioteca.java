package com.example.proyecto_2_datos;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Documento> documentos;

    public Biblioteca() {
        documentos = new ArrayList<>();
    }

    // Método para agregar un documento a la biblioteca
    public void agregarDocumento(Documento documento) throws IOException {
        documentos.add(documento);
    }

    // Método para eliminar un documento de la biblioteca
    public void eliminarDocumento(Documento documento) {
        documentos.remove(documento);
    }

    // Método para actualizar un documento en la biblioteca
    public void actualizarDocumento(Documento documentoAntiguo, Documento documentoNuevo) {
        int indice = documentos.indexOf(documentoAntiguo);
        if (indice != -1) {
            documentos.set(indice, documentoNuevo);
        }
    }

    // Método para obtener el contenido de un documento en la biblioteca
    public String obtenerContenidoDocumento(Documento documento) throws IOException {
        return documento.obtenerContenido();
    }

    // Método para obtener la lista de documentos
    public List<Documento> getDocumentos() {
        return documentos;
    }
}


