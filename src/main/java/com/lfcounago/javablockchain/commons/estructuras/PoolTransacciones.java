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

    public Set<Transaccion> getPool() {
        return this.pool;
    }

    public void setPool(Set<Transaccion> pool) {
        this.pool = pool;
    }

    /**
     * Añade una transacción al pool de transacciones de manera sincronizada.
     *
     * @param transaccion La transacción que se va a añadir al pool.
     * @throws Exception Si la transacción no es válida, se lanza una excepción con
     *                   el mensaje "Transacción inválida".
     */
    public synchronized void añadirTransaccion(Transaccion transaccion) throws Exception {
        if (transaccion.esValida()) {
            pool.add(transaccion);
        } else {
            throw new Exception("Transacción inválida");
        }
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

    /**
     * Comprueba si el pool está vacío.
     *
     * @return true si el pool es nulo o está vacío, false si contiene elementos.
     */
    public boolean estaVacio() {
        return this.pool == null || this.pool.isEmpty();
    }
}
