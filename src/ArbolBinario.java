import java.util.ArrayList;
import java.util.function.Function;

public class ArbolBinario<T> {

    private class Nodo {
        String clave;
        T dato;
        Nodo izquierdo;
        Nodo derecho;

        Nodo(String clave, T dato) {
            this.clave = clave;
            this.dato = dato;
        }
    }

    private Nodo raiz;
    private Function<T, String> obtenerClave;

    public ArbolBinario(Function<T, String> obtenerClave) {
        this.obtenerClave = obtenerClave;
    }

    public void limpiar() {
        raiz = null;
    }

    public boolean insertar(T dato) {
        String clave = dato == null ? "" : normalizar(obtenerClave.apply(dato));

        if (clave.isEmpty()) {
            return false;
        }

        if (raiz == null) {
            raiz = new Nodo(clave, dato);
            return true;
        }

        return insertar(raiz, clave, dato);
    }

    private boolean insertar(Nodo actual, String clave, T dato) {
        int comparacion = clave.compareTo(actual.clave);

        if (comparacion == 0) {
            return false;
        }

        if (comparacion < 0) {
            if (actual.izquierdo == null) {
                actual.izquierdo = new Nodo(clave, dato);
                return true;
            }

            return insertar(actual.izquierdo, clave, dato);
        }

        if (actual.derecho == null) {
            actual.derecho = new Nodo(clave, dato);
            return true;
        }

        return insertar(actual.derecho, clave, dato);
    }

    public T buscar(String clave) {
        return buscar(raiz, normalizar(clave));
    }

    private T buscar(Nodo actual, String clave) {
        if (actual == null || clave.isEmpty()) {
            return null;
        }

        int comparacion = clave.compareTo(actual.clave);

        if (comparacion == 0) {
            return actual.dato;
        }

        if (comparacion < 0) {
            return buscar(actual.izquierdo, clave);
        }

        return buscar(actual.derecho, clave);
    }

    public boolean contiene(String clave) {
        return buscar(clave) != null;
    }

    public ArrayList<T> listarInOrden() {
        ArrayList<T> datos = new ArrayList<>();
        inOrden(raiz, datos);
        return datos;
    }

    private void inOrden(Nodo actual, ArrayList<T> datos) {
        if (actual == null) {
            return;
        }

        inOrden(actual.izquierdo, datos);
        datos.add(actual.dato);
        inOrden(actual.derecho, datos);
    }

    private String normalizar(String clave) {
        return clave == null ? "" : clave.trim().toUpperCase();
    }
}