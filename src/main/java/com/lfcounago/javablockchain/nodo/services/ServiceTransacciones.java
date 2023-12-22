package com.lfcounago.javablockchain.nodo.services;

import java.net.URL;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.commons.estructuras.PoolTransacciones;
import com.lfcounago.javablockchain.commons.estructuras.Transaccion;

@Service
public class ServiceTransacciones {

    // Pool de transacciones con transacciones pendientes de ser incluidas en un
    // bloque
    private PoolTransacciones poolTransacciones = new PoolTransacciones();

    @Autowired
    public ServiceTransacciones() {
    }

    /**
     * Obtiene el pool de transacciones actual.
     *
     * @return El pool de transacciones actual.
     */
    public PoolTransacciones getPoolTransacciones() {
        return poolTransacciones;
    }

    /**
     * Añade una transacción al pool de transacciones si es válida.
     *
     * @param transaccion La transacción que se va a añadir al pool.
     * @return true si la transacción es válida y se ha añadido al pool, false en
     *         caso contrario.
     */
    public synchronized boolean añadirTransaccion(Transaccion transaccion) {
        return poolTransacciones.añadirTransaccion(transaccion);
    }

    /**
     * Elimina una transacción del pool de transacciones.
     *
     * @param transaccion La transacción que se va a eliminar del pool.
     */
    public void eliminarTransaccion(Transaccion transaccion) {
        poolTransacciones.eliminarTransaccion(transaccion);
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
        return poolTransacciones.contieneTransacciones(transacciones);
    }

    /**
     * Descarga el pool de transacciones desde otro nodo.
     *
     * @param urlNodo      Nodo del que se van a obtener las transacciones.
     * @param restTemplate RestTemplate a usar para la petición HTTP.
     */
    public void obtenerPoolTransacciones(URL urlNodo, RestTemplate restTemplate) {
        PoolTransacciones poolTransacciones = restTemplate.getForObject(urlNodo + "/transaccion",
                PoolTransacciones.class);
        this.poolTransacciones = poolTransacciones;
    }
}