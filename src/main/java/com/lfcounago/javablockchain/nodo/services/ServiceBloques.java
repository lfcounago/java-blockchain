package com.lfcounago.javablockchain.nodo.services;

import java.net.URL;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.CadenaDeBloques;
import com.lfcounago.javablockchain.Configuracion;

@Service
public class ServiceBloques {

    private final ServiceTransacciones servicioTransacciones;

    private CadenaDeBloques cadenaDeBloques = new CadenaDeBloques();

    @Autowired
    public ServiceBloques(ServiceTransacciones servicioTransacciones) {
        this.servicioTransacciones = servicioTransacciones;
    }

    /**
     * Obtiene la cadena de bloques actual.
     *
     * @return La cadena de bloques actual.
     */
    public CadenaDeBloques getCadenaDeBloques() {
        return cadenaDeBloques;
    }

    /**
     * Añade un bloque a la cadena de bloques si es válido.
     * Si el bloque es válido, también elimina las transacciones incluidas en el
     * bloque del pool de transacciones.
     *
     * @param bloque El bloque que se va a añadir a la cadena de bloques.
     * @return true si el bloque es válido y se ha añadido a la cadena de bloques,
     *         false en caso contrario.
     */
    public synchronized boolean añadirBloque(Bloque bloque) {
        if (validarBloque(bloque)) {
            this.cadenaDeBloques.añadirBloque(bloque);

            // eliminar las transacciones incluidas en el bloque del pool de transacciones
            bloque.getTransacciones().forEach(servicioTransacciones::eliminarTransaccion);
            return true;
        }
        return false;
    }

    /**
     * Descarga la cadena de bloques desde otro nodo.
     *
     * @param urlNodo      La URL del nodo del que se va a descargar la cadena de
     *                     bloques.
     * @param restTemplate El RestTemplate a usar para la petición HTTP.
     */
    public void obtenerCadenaDeBloques(URL urlNodo, RestTemplate restTemplate) {
        CadenaDeBloques cadena = restTemplate.getForObject(urlNodo + "/bloque", CadenaDeBloques.class);
        this.cadenaDeBloques = cadena;
        System.out.println("Obtenida cadena de bloques de nodo " + urlNodo);
    }

    /**
     * Valida un bloque que se va a añadir a la cadena de bloques.
     * Comprueba que el bloque tiene un formato válido, que el hash del bloque
     * anterior hace referencia al último bloque en la cadena,
     * que el número de transacciones en el bloque no supera el límite, que todas
     * las transacciones en el bloque están en el pool de transacciones,
     * y que la dificultad del bloque es la correcta.
     *
     * @param bloque El bloque que se va a validar.
     * @return true si el bloque es válido, false en caso contrario.
     */
    private boolean validarBloque(Bloque bloque) {
        // comprobar que el bloque tiene un formato v�lido
        if (!bloque.esValido()) {
            return false;
        }

        // el hash de bloque anterior hace referencia al ultimo bloque en mi cadena
        if (!cadenaDeBloques.estaVacia()) {
            byte[] hashUltimoBloque = cadenaDeBloques.getUltimoBloque().getHash();
            if (!Arrays.equals(bloque.getHashBloqueAnterior(), hashUltimoBloque)) {
                System.out.println("Bloque anterior invalido");
                return false;
            }
        } else {
            if (bloque.getHashBloqueAnterior() != null) {
                System.out.println("Bloque anterior invalido");
                return false;
            }
        }

        // max. numero de transacciones en un bloque
        if (bloque.getTransacciones().size() > Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque()) {
            System.out.println("Numero de transacciones supera el limite.");
            return false;
        }

        // verificar que todas las transacciones estaban en mi pool
        if (!servicioTransacciones.contieneTransacciones(bloque.getTransacciones())) {
            System.out.println("Algunas transacciones no en pool");
            return false;
        }

        // la dificultad coincide
        if (bloque.getNumeroDeCerosHash() < Configuracion.getInstancia().getDificultad()) {
            System.out.println("Bloque con dificultad inválida");
            return false;
        }

        return true;
    }
}