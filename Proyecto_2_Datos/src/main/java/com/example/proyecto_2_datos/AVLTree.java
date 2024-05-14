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

    // Get height of node
    private int height(AVLNode node) {
        if (node == null)
            return 0;
        return node.height;
    }

    // Get balance factor of node
    private int getBalance(AVLNode node) {
        if (node == null)
            return 0;
        return height(node.left) - height(node.right);
    }

    // Right rotate subtree rooted with y
    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Left rotate subtree rooted with x
    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
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

        // Update height of this ancestor node
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Get the balance factor of this ancestor node
        int balance = getBalance(node);

        // If node becomes unbalanced, perform rotations
        // Left Left Case
        if (balance > 1 && palabra.compareTo(node.left.palabra) < 0)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && palabra.compareTo(node.right.palabra) > 0)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && palabra.compareTo(node.left.palabra) >= 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && palabra.compareTo(node.right.palabra) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }
}