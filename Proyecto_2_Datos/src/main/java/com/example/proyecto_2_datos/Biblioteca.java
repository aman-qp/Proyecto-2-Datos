package com.example.proyecto_2_datos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public void ordenarporTamaño() {
    int m = obtenerMaximoTamaño();

    for (int exp = 1; m/exp > 0; exp *= 10)
    countingSort(exp);
    }

    private int obtenerMaximoTamaño() {
        long max = documentos.get(0).getTamaño();
        for (Documento doc : documentos) {
            if (doc.getTamaño() > max)
                max = doc.getTamaño();
        }
        return (int) max;
    }

    private void countingSort(int exp) {
        Documento output[] = new Documento[documentos.size()];
        int i;
        int count[] = new int[10];
        Arrays.fill(count, 0);

        for (i = 0; i < documentos.size(); i++)
            count[(int) (documentos.get(i).getTamaño()/exp)%10]++;

        for (i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (i = documentos.size() - 1; i >= 0; i--) {
            output[count[(int) (documentos.get(i).getTamaño()/exp)%10] - 1] = documentos.get(i);
            count[(int) (documentos.get(i).getTamaño()/exp)%10]--;
        }

        for (i = 0; i < documentos.size(); i++)
            documentos.set(i, output[i]);
    }

        
    
}
