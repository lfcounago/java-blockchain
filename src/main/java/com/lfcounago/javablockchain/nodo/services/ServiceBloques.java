package com.lfcounago.javablockchain.nodo.services;

import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.CadenaDeBloques;
import com.lfcounago.javablockchain.commons.estructuras.RegistroSaldos;
import com.lfcounago.javablockchain.commons.estructuras.Transaccion;
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
     * Añade un bloque a la cadena de bloques de manera sincronizada, validando el
     * bloque antes de agregarlo.
     *
     * @param bloque El bloque que se va a añadir a la cadena de bloques.
     * @throws Exception Si el bloque no es válido, se lanza una excepción con el
     *                   mensaje "Bloque inválido".
     */
    public synchronized void añadirBloque(Bloque bloque) throws Exception {
        if (validarBloque(bloque)) {
            this.cadenaDeBloques.añadirBloque(bloque);

            // Eliminar transacciones del pool excepto la primera transacción que es la
            // coinbase
            bloque.getTransacciones().subList(1, bloque.getTransacciones().size())
                    .forEach(servicioTransacciones::eliminarTransaccion);

            System.out.println("Bloque añadido a la cadena de bloques.\n");
        } else {
            throw new Exception("Bloque inválido");
        }
    }

    /**
     * Obtiene la cadena de bloques desde un nodo remoto utilizando un objeto
     * RestTemplate.
     *
     * @param urlNodo      La URL del nodo remoto del cual se va a obtener la cadena
     *                     de bloques.
     * @param restTemplate El objeto RestTemplate utilizado para realizar la
     *                     solicitud HTTP.
     */
    public void obtenerCadenaDeBloques(URL urlNodo, RestTemplate restTemplate) {
        CadenaDeBloques cadena = restTemplate.getForObject(urlNodo + "/bloque", CadenaDeBloques.class);
        System.out.println("Obtenida cadena de bloques de nodo " + urlNodo + ".\n");
        try {
            this.cadenaDeBloques = new CadenaDeBloques(cadena);
        } catch (Exception e) {
            System.out.println("Cadena de bloques inválida");
        }
    }

    /**
     * Valida un bloque verificando diversos aspectos, como el formato, el hash del
     * bloque anterior, el número de transacciones,
     * la presencia de transacciones en el pool, y la dificultad del bloque.
     *
     * @param bloque El bloque que se va a validar.
     * @return true si el bloque es válido, false de lo contrario.
     */
    private boolean validarBloque(Bloque bloque) {
        // Comprobar que el bloque tiene un formato válido
        if (!bloque.esValido()) {
            return false;
        }

        // El hash del bloque anterior hace referencia al último bloque en mi cadena
        if (!cadenaDeBloques.estaVacia()) {
            byte[] hashUltimoBloque = cadenaDeBloques.getUltimoBloque().getHash();
            if (!Arrays.equals(bloque.getHashBloqueAnterior(), hashUltimoBloque)) {
                System.out.println("Hash bloque anterior no coincide.");
                return false;
            }
        } else {
            if (bloque.getHashBloqueAnterior() != null) {
                System.out.println("Hash bloque anterior inválido. Debería ser null.");
                return false;
            }
        }

        // Máximo número de transacciones en un bloque
        if (bloque.getTransacciones().size() > Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque() + 1) {
            System.out.println("El número de transacciones supera el límite.");
            return false;
        }

        // Verificar que todas las transacciones estaban en mi pool
        if (!servicioTransacciones
                .contieneTransacciones(bloque.getTransacciones().subList(1, bloque.getTransacciones().size()))) {
            System.out.println("Algunas transacciones no están en el pool");
            return false;
        }

        // La dificultad coincide
        if (bloque.getNumeroDeCerosHash() < Configuracion.getInstancia().getDificultad()) {
            System.out.println("Bloque con dificultad inválida");
            return false;
        }

        return true;
    }

}