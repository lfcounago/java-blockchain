package com.lfcounago.javablockchain.nodo.services;

import java.net.URL;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.commons.estructuras.PoolTransacciones;
import com.lfcounago.javablockchain.commons.estructuras.RegistroSaldos;
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
     * Añade una transacción al pool de transacciones de manera sincronizada.
     *
     * @param transaccion La transacción que se va a añadir al pool.
     * @throws Exception Si ocurre un error al añadir la transacción al pool.
     */
    public synchronized void añadirTransaccion(Transaccion transaccion) throws Exception {
        poolTransacciones.añadirTransaccion(transaccion);
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
     * Obtiene el pool de transacciones desde un nodo remoto utilizando un objeto
     * RestTemplate.
     *
     * @param urlNodo      La URL del nodo remoto del cual se va a obtener el pool
     *                     de transacciones.
     * @param restTemplate El objeto RestTemplate utilizado para realizar la
     *                     solicitud HTTP.
     */
    public void obtenerPoolTransacciones(URL urlNodo, RestTemplate restTemplate) {
        PoolTransacciones poolTransacciones = restTemplate.getForObject(urlNodo + "/transaccion",
                PoolTransacciones.class);
        this.poolTransacciones = poolTransacciones;
        System.out.println("Obtenido pool de transacciones de nodo " + urlNodo + ".\n");
    }
}