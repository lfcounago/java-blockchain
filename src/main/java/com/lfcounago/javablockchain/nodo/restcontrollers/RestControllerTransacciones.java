package com.lfcounago.javablockchain.nodo.restcontrollers;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lfcounago.javablockchain.commons.estructuras.PoolTransacciones;
import com.lfcounago.javablockchain.commons.estructuras.Transaccion;
import com.lfcounago.javablockchain.nodo.services.ServiceNodo;
import com.lfcounago.javablockchain.nodo.services.ServiceTransacciones;

import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("transaccion")
public class RestControllerTransacciones {

    private final ServiceTransacciones servicioTransacciones;
    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerTransacciones(ServiceTransacciones servicioTransacciones, ServiceNodo servicioNodo) {
        this.servicioTransacciones = servicioTransacciones;
        this.servicioNodo = servicioNodo;
    }

    /**
     * Obtiene el pool de transacciones pendientes de ser incluidas en un bloque.
     *
     * @return El pool de transacciones en formato JSON.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    PoolTransacciones getPoolTransacciones() {
        System.out.println("Request: getPoolTransacciones");
        return servicioTransacciones.getPoolTransacciones();
    }

    /**
     * Añade una transacción al pool de transacciones.
     * Si el parámetro 'propagar' es true, emite una petición POST a los nodos
     * vecinos para propagar la transacción.
     *
     * @param transaccion La transacción que se va a añadir al pool.
     * @param propagar    Si es true, propaga la transacción a los nodos vecinos.
     * @param response    La respuesta HTTP.
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirTransaccion(@RequestBody Transaccion transaccion, @RequestParam(required = false) Boolean propagar,
            HttpServletResponse response) {
        System.out.println("Request: Añadir transaccion " + Base64.encodeBase64String(transaccion.getHash()));
        boolean exito = servicioTransacciones.añadirTransaccion(transaccion);

        if (exito) {
            System.out.println("Transaccion validada y añadida.");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("transaccion", transaccion);
            }
        } else {
            System.out.println("Transaccion invalida y no añadida.");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}