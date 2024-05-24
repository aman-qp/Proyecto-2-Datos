package com.example.proyecto_2_datos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private List<Documento> documentos;
    private AVLTree arbolAVL;

    public Biblioteca() {
        documentos = new ArrayList<>();
        arbolAVL = new AVLTree();
    }

    public AVLTree getArbolAVL() {
        return arbolAVL;
    }

    // Método para agregar un documento a la biblioteca
    public void agregarDocumento(Documento documento) throws IOException {
        documentos.add(documento);
        parsearDocumento(documento);
        imprimirArbolAVL();
    }

    private void parsearDocumento(Documento documento) throws IOException {
        String contenido = documento.obtenerContenido();
        String[] palabras = contenido.split("\\W+"); // Dividir el contenido en palabras
        int posicion = 0;

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                arbolAVL.insertarPalabra(palabra, documento.getNombre(), posicion);
                posicion += palabra.length() + 1; // Avanzar la posición
            }
        }
    }

    // Método para eliminar un documento de la biblioteca
    public void eliminarDocumento(Documento documento) {
        documentos.remove(documento);
    }

    // Método para actualizar un documento en la biblioteca
    public void actualizarDocumento(Documento documentoAntiguo, Documento documentoNuevo) throws IOException {
        int indice = documentos.indexOf(documentoAntiguo);
        if (indice != -1) {
            documentos.set(indice, documentoNuevo);
        }
    }

    // Método para obtener la lista de documentos
    public List<Documento> getDocumentos() {
        return documentos;
    }

    public void imprimirArbolAVL() {
        System.out.println("Contenido del árbol AVL:");
        imprimirNodoAVL(arbolAVL.root);
    }

    private void imprimirNodoAVL(AVLNode nodo) {
        if (nodo != null) {
            System.out.print("Palabra: " + nodo.palabra + ", Ocurrencias: ");
            for (Ocurrencia ocurrencia : nodo.ocurrencias) {
                System.out.print("[" + ocurrencia.documento + ", " + ocurrencia.posicion + "] ");
            }
            System.out.println();

            imprimirNodoAVL(nodo.left);
            imprimirNodoAVL(nodo.right);
        }
    }

    public boolean existeDocumentoConRuta(String rutaArchivo) {
        for (Documento documento : documentos) {
            if (documento.getRuta().equals(rutaArchivo)) {
                return true; // El documento con esta ruta ya existe
            }
        }
        return false; // El documento con esta ruta no existe
    }
}
