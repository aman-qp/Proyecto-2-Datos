package com.example.proyecto_2_datos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    public void ordenarporNombre() {
        quicksort(documentos, 0, documentos.size() - 1);
    }
    
    private void quicksort(List<Documento> documentos, int inicio, int fin) {
        if (inicio < fin) {
            int pivote = particion(documentos, inicio, fin);
            quicksort(documentos, inicio, pivote - 1);
            quicksort(documentos, pivote + 1, fin);
        }
    }
    
    private int particion(List<Documento> documentos, int inicio, int fin) {
        Documento pivote = documentos.get(fin);
        int i = inicio - 1;
    
        for (int j = inicio; j < fin; j++) {
            if (documentos.get(j).getNombre().compareTo(pivote.getNombre()) < 0) {
                i++;
                intercambiar(documentos, i, j);
            }
        }
    
        intercambiar(documentos, i + 1, fin);
        return i + 1;
    }
    
    private void intercambiar(List<Documento> documentos, int i, int j) {
        Documento temp = documentos.get(i);
        documentos.set(i, documentos.get(j));
        documentos.set(j, temp);
    }

    public void ordenarporFechaCreacion() {
        int n = documentos.size();
        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-i-1; j++) {
                if (documentos.get(j).getFechaCreacion().isAfter(documentos.get(j+1).getFechaCreacion())) {
                    // Intercambiar documentos[j+1] y documentos[j]
                    Documento temp = documentos.get(j);
                    documentos.set(j, documentos.get(j+1));
                    documentos.set(j+1, temp);
                }
            }
        }
    }


    public void ordenarPorTamaño() {
        if (documentos.isEmpty()) return;
    
        // Encontrar el máximo tamaño para saber el número de dígitos
        long maxTamaño = documentos.stream().mapToLong(Documento::getTamaño).max().getAsLong();
    
        // Aplicar counting sort para cada dígito. El exponente indica qué dígito está siendo procesado
        for (int exponente = 1; maxTamaño / exponente > 0; exponente *= 10) {
            countingSortPorTamaño(exponente);
        }
    }
    
    private void countingSortPorTamaño(int exponente) {
        int n = documentos.size();
        List<Documento> documentosOrdenados = new ArrayList<>(Collections.nCopies(n, null));
        int[] count = new int[10]; // Array de conteo para los dígitos (0-9)
    
        // Contar ocurrencias de cada dígito
        for (Documento doc : documentos) {
            int index = (int) ((doc.getTamaño() / exponente) % 10);
            count[index]++;
        }
    
        // Cambiar count[i] para que contenga la posición actual en el array de salida
        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
        }
    
        // Construir el array de salida
        for (int i = n - 1; i >= 0; i--) {
            Documento doc = documentos.get(i);
            int index = (int) ((doc.getTamaño() / exponente) % 10);
            documentosOrdenados.set(count[index] - 1, doc);
            count[index]--;
        }
    
        // Copiar los elementos ordenados de vuelta a documentos
        for (int i = 0; i < n; i++) {
            documentos.set(i, documentosOrdenados.get(i));
        }
    }
}    