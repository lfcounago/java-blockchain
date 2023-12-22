package com.lfcounago.javablockchain.nodo.restcontrollers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lfcounago.javablockchain.nodo.services.ServiceNodo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("nodo")
public class RestControllerNodo {

    private final ServiceNodo servicioNodo;

    @Autowired
    public RestControllerNodo(ServiceNodo servicioNodo) {
        this.servicioNodo = servicioNodo;
    }

    /**
     * Obtiene la lista de nodos vecinos en la red.
     *
     * @return Un conjunto de URLs de los nodos vecinos en formato JSON.
     */
    @RequestMapping()
    Set<URL> getNodosVecinos() {
        return servicioNodo.getNodosVecinos();
    }

    /**
     * Da de alta un nodo en la red.
     *
     * @param urlNodo  La URL del nodo a dar de alta.
     * @param response La respuesta HTTP.
     */
    @RequestMapping(method = RequestMethod.POST)
    void altaNodo(@RequestBody String urlNodo, HttpServletResponse response) {
        System.out.println("Alta nodo " + urlNodo);
        try {
            servicioNodo.altaNodo(new URL(urlNodo));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (MalformedURLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Da de baja un nodo en la red.
     *
     * @param urlNodo  La URL del nodo a dar de baja.
     * @param response La respuesta HTTP.
     */
    @RequestMapping(method = RequestMethod.DELETE)
    void bajaNodo(@RequestBody String urlNodo, HttpServletResponse response) {
        System.out.println("Baja nodo " + urlNodo);
        try {
            servicioNodo.bajaNodo(new URL(urlNodo));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (MalformedURLException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Obtiene la IP pública del cliente que hace la petición.
     *
     * @param request La petición HTTP.
     * @return La IP pública del cliente.
     */
    @RequestMapping(path = "ip")
    String getIpPublica(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

}