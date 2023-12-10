package com.lfcounago.javablockchain.commons.estructuras;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PoolTransacciones {

    private Set<Transaccion> pool = new HashSet<>();

    public PoolTransacciones() {

    }

    public PoolTransacciones(List<Transaccion> transacciones) {
        this.pool.addAll(transacciones);
    }

    /**
     * Añade una transacción al pool de transacciones si es válida.
     *
     * @param transaccion La transacción que se va a añadir al pool.
     * @return true si la transacción es válida y se ha añadido al pool, false en
     *         caso contrario.
     */
    public synchronized boolean añadirTransaccion(Transaccion transaccion) {
        if (transaccion.esValida()) {
            pool.add(transaccion);
            return true;
        }
        return false;
    }

    /**
     * Elimina una transacción del pool de transacciones.
     *
     * @param transaccion La transacción que se va a eliminar del pool.
     */
    public void eliminarTransaccion(Transaccion transaccion) {
        pool.remove(transaccion);
    }

    /**
     * Comprueba si el pool de transacciones contiene todas las transacciones de una
     * colección dada.
     *
     * @param transacciones La colección de transacciones que se va a comprobar.
     * @return true si todas las transacciones de la colección están en el pool,
     *         false en caso contrario.
     */
    public boolean contieneTransacciones(Collection<Transaccion> transacciones) {
        return pool.containsAll(transacciones);
    }
}
