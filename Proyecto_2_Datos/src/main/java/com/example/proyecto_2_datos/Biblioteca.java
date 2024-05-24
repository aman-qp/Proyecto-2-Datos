package com.example.proyecto_2_datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    // Método para obtener un valor numérico para una palabra
    private int obtenerValorPalabra(String palabra) {
        int valor = 0;
        for (char c : palabra.toCharArray()) {
            valor += (int) c;
        }
        return valor;
    }
    // Otros métodos de la clase Biblioteca...

    // Método para eliminar un documento de la biblioteca
    public void eliminarDocumento(Documento documento) {
        documentos.remove(documento);
        // TODO: Implementar la eliminación de las ocurrencias del documento en el árbol
        // AVL
    }

    // Método para actualizar un documento en la biblioteca
    public void actualizarDocumento(Documento documentoAntiguo, Documento documentoNuevo) throws IOException {
        int indice = documentos.indexOf(documentoAntiguo);
        if (indice != -1) {
            documentos.set(indice, documentoNuevo);
            // TODO: Implementar la actualización de las ocurrencias del documento en el
            // árbol AVL
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

    // Método para obtener la palabra a partir del valor numérico
    private String obtenerPalabra(int valor) {
        StringBuilder palabra = new StringBuilder();
        while (valor > 0) {
            int valorASCII = valor % 256;
            palabra.insert(0, (char) valorASCII);
            valor /= 256;
        }
        return palabra.toString();
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
