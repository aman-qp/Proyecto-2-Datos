package com.example.proyecto_2_datos;

import java.util.ArrayList;
import java.util.List;

class AVLNode {
    String palabra;
    int height;
    AVLNode left, right;
    List<Ocurrencia> ocurrencias;

    AVLNode(String palabra) {
        this.palabra = palabra;
        this.height = 1;
        this.left = this.right = null;
        this.ocurrencias = new ArrayList<>();
    }
}

class Ocurrencia {
    String documento;
    int posicion;

    Ocurrencia(String documento, int posicion) {
        this.documento = documento;
        this.posicion = posicion;
    }
}

public class AVLTree {
    public AVLNode root;

    // Obtener la altura del nodo
    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // Obtener el factor de equilibrio del nodo
    private int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    // Rotación a la derecha del subárbol con raíz en y
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Realizar la rotación
        x.right = y;
        y.left = T2;

        // Actualizar las alturas
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Rotación a la izquierda del subárbol con raíz en x
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Realizar la rotación
        y.left = x;
        x.right = T2;

        // Actualizar las alturas
        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // Agregar una ocurrencia a un nodo
    private void agregarOcurrencia(AVLNode node, String documento, int posicion) {
        if (node != null) {
            node.ocurrencias.add(new Ocurrencia(documento, posicion));
        }
    }

    public void insertarPalabra(String palabra, String documento, int posicion) {
        root = insertRecursive(root, palabra, documento, posicion);
    }

    private AVLNode insertRecursive(AVLNode node, String palabra, String documento, int posicion) {
        // Realizar la inserción normal del árbol AVL
        if (node == null) {
            AVLNode nuevoNodo = new AVLNode(palabra);
            nuevoNodo.ocurrencias.add(new Ocurrencia(documento, posicion));
            return nuevoNodo;
        }

        if (palabra.compareTo(node.palabra) < 0) {
            node.left = insertRecursive(node.left, palabra, documento, posicion);
        } else if (palabra.compareTo(node.palabra) > 0) {
            node.right = insertRecursive(node.right, palabra, documento, posicion);
        } else {
            // Agregar la ocurrencia al nodo existente
            agregarOcurrencia(node, documento, posicion);
            return node;
        }

        // Actualizar la altura de este nodo ancestro
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Obtener el factor de equilibrio de este nodo ancestro
        int balance = getBalance(node);

        // Si el nodo se desequilibra, realizar rotaciones
        // Caso Izquierda Izquierda
        if (balance > 1 && palabra.compareTo(node.left.palabra) < 0)
            return rightRotate(node);

        // Caso Derecha Derecha
        if (balance < -1 && palabra.compareTo(node.right.palabra) > 0)
            return leftRotate(node);

        // Caso Izquierda Derecha
        if (balance > 1 && palabra.compareTo(node.left.palabra) >= 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Caso Derecha Izquierda
        if (balance < -1 && palabra.compareTo(node.right.palabra) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    public void buscarPalabra(String palabra) {
        buscarPalabraRecursive(root, palabra);
    }

    private void buscarPalabraRecursive(AVLNode node, String palabra) {
        if (node != null) {
            if (node.palabra.equals(palabra)) {
                System.out.println("\033[0;1m" + node.palabra + "\033[0m"); // Resalta la palabra en negrita
                for (Ocurrencia ocurrencia : node.ocurrencias) {
                    System.out.println("Documento: " + ocurrencia.documento + ", Posición: " + ocurrencia.posicion);
                }
            } else if (palabra.compareTo(node.palabra) < 0) {
                buscarPalabraRecursive(node.left, palabra);
            } else {
                buscarPalabraRecursive(node.right, palabra);
            }
        }
    }

}
