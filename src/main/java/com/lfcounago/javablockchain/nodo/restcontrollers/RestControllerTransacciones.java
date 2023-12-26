package com.lfcounago.javablockchain.nodo.restcontrollers;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.lfcounago.javablockchain.commons.estructuras.RegistroSaldos;
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
        System.out.println("PETICION POOL DE TRANSACCIONES\n");
        return servicioTransacciones.getPoolTransacciones();
    }

    /**
     * Maneja las solicitudes HTTP POST para añadir una transacción al pool de
     * transacciones del sistema blockchain.
     *
     * @param transaccion La transacción que se va a añadir, proporcionada en el
     *                    cuerpo de la solicitud.
     * @param propagar    Indica si se debe propagar la transacción a nodos vecinos
     *                    (opcional).
     * @param response    La respuesta HTTP que se enviará al cliente.
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirTransaccion(@RequestBody Transaccion transaccion, @RequestParam(required = false) Boolean propagar,
            HttpServletResponse response) {
        System.out.println("NUEVA TRANSACCION RECIBIDA:");
        System.out.println(transaccion);
        System.out.println("\n");
        try {
            // Comprobar si la transacción es válida
            servicioTransacciones.añadirTransaccion(transaccion);

            System.out.println("Transacción añadida al pool.\n");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("transaccion", transaccion);
                System.out.println("Transacción propagada.\n");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }
    }

}