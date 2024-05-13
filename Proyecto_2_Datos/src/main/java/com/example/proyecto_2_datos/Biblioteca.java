package com.example.proyecto_2_datos;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Documento> documentos;

    public Biblioteca() {
        documentos = new ArrayList<>();
    }

    // Método para agregar un documento a la biblioteca
    public void agregarDocumento(Documento documento) {
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

    // Método para cargar la biblioteca desde el almacenamiento
    public void cargarBiblioteca() {
        // Implementa la lógica para cargar la biblioteca desde el almacenamiento
    }

    // Método para guardar la biblioteca en el almacenamiento
    public void guardarBiblioteca() {
        // Implementa la lógica para guardar la biblioteca en el almacenamiento
    }

    // Otros métodos según sea necesario
}
