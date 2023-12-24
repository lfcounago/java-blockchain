package com.lfcounago.javablockchain.nodo.restcontrollers;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.CadenaDeBloques;
import com.lfcounago.javablockchain.nodo.services.ServiceBloques;
import com.lfcounago.javablockchain.nodo.services.ServiceNodo;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("bloque")
public class RestControllerBloques {

    private final ServiceBloques servicioBloques;
    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerBloques(ServiceBloques servicioCadenaDeBloques, ServiceNodo servicioNodo) {
        this.servicioBloques = servicioCadenaDeBloques;
        this.servicioNodo = servicioNodo;
    }

    /**
     * Maneja las solicitudes GET para obtener la cadena de bloques.
     *
     * @return La cadena de bloques.
     */
    @RequestMapping(method = RequestMethod.GET)
    CadenaDeBloques getCadenaDeBloques() {
        System.out.println("Request: getCadenaDeBloques");
        return servicioBloques.getCadenaDeBloques();
    }

    /**
     * Maneja las solicitudes POST para añadir un bloque a la cadena de bloques.
     * Si el parámetro 'propagar' es true, emite una petición POST a los nodos
     * vecinos para propagar el bloque.
     *
     * @param bloque   El bloque que se va a añadir a la cadena de bloques.
     * @param propagar Si es true, propaga el bloque a los nodos vecinos.
     * @param response La respuesta HTTP.
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirBloque(@RequestBody Bloque bloque, @RequestParam(required = false) Boolean propagar,
            HttpServletResponse response) {
        System.out.println("Request: Añadir bloque " + Base64.encodeBase64String(bloque.getHash()));
        boolean exito = servicioBloques.añadirBloque(bloque);

        if (exito) {
            System.out.println("Bloque validado y añadido.");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
            }
        } else {
            System.out.println("Bloque invalido y no añadido.");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}