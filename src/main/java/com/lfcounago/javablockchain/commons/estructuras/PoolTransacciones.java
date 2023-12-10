package com.lfcounago.javablockchain.commons.estructuras;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PoolTransacciones {

    private Set<Transaccion> pool = new HashSet<>();

    /**
     * Añadir una transaccion al pool
     * 
     * @return true si la transaccion es válida y es añadida al pool
     */
    public synchronized boolean add(Transaccion transaccion) {
        if (transaccion.esValida()) {
            pool.add(transaccion);
            return true;
        }
        return false;
    }

    /**
     * Eliminar una transaccion del pool
     */
    public void eliminar(Transaccion transaccion) {
        pool.remove(transaccion);
    }

    /**
     * Comprobar si el pool contiene todas las transacciones de una lista de
     * transacciones
     * 
     * @return true si todas las transacciones de la coleccion están en el pool
     */
    public boolean contieneTransacciones(Collection<Transaccion> transacciones) {
        return pool.containsAll(transacciones);
    }
}
