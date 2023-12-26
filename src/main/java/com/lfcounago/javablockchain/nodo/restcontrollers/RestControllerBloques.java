package com.lfcounago.javablockchain.nodo.restcontrollers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lfcounago.javablockchain.Configuracion;
import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.CadenaDeBloques;
import com.lfcounago.javablockchain.nodo.services.ServiceBloques;
import com.lfcounago.javablockchain.nodo.services.ServiceNodo;
import com.lfcounago.javablockchain.nodo.services.ServiceMinado;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("bloque")
public class RestControllerBloques {

    private final ServiceBloques servicioBloques;
    private final ServiceNodo servicioNodo;
    private final ServiceMinado servicioMinado;

    private List<Bloque> bufferBloques = new ArrayList<Bloque>();

    /**
     * Constructor de la clase RestControllerBloques, utilizado para inyectar
     * dependencias.
     *
     * @param servicioCadenaDeBloques El servicio que gestiona la lógica de la
     *                                cadena de bloques.
     * @param servicioNodo            El servicio que gestiona la lógica del nodo.
     * @param servicioMinado          El servicio que gestiona la lógica de minado.
     */
    @Autowired
    public RestControllerBloques(ServiceBloques servicioCadenaDeBloques, ServiceNodo servicioNodo,
            ServiceMinado servicioMinado) {
        this.servicioBloques = servicioCadenaDeBloques;
        this.servicioNodo = servicioNodo;
        this.servicioMinado = servicioMinado;

        if (Configuracion.getInstancia().getMinar()) {
            servicioMinado.startMinado();
        }
    }

    /**
     * Maneja las solicitudes HTTP GET para obtener la cadena de bloques del sistema
     * blockchain.
     *
     * @return La cadena de bloques actual del sistema.
     */
    @RequestMapping(method = RequestMethod.GET)
    CadenaDeBloques getCadenaDeBloques() {
        System.out.println("PETICION CADENA DE BLOQUES\n");
        return servicioBloques.getCadenaDeBloques();
    }

    /**
     * Maneja las solicitudes HTTP POST para añadir un bloque al sistema blockchain.
     *
     * @param bloque   El bloque que se va a añadir, proporcionado en el cuerpo de
     *                 la solicitud.
     * @param propagar Indica si se debe propagar el bloque a nodos vecinos
     *                 (opcional).
     * @param response La respuesta HTTP que se enviará al cliente.
     */
    @RequestMapping(method = RequestMethod.POST)
    void añadirBloque(@RequestBody Bloque bloque, @RequestParam(required = false) Boolean propagar,
            HttpServletResponse response) {
        System.out.println("NUEVO BLOQUE RECIBIDO:");
        System.out.println(bloque);
        System.out.println("\n");

        if (!servicioNodo.inicializado) {
            this.bufferBloques.add(bloque);
            System.out.println("Bloque añadido a buffer.\n");
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        }
        try {
            for (Bloque b : this.bufferBloques) {
                this.servicioBloques.añadirBloque(b);
            }
            this.bufferBloques = new ArrayList<Bloque>();

            servicioBloques.añadirBloque(bloque);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            // servicioMinado.restartMinado();
            if (propagar != null && propagar) {
                servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
                System.out.println("Bloque propagado.\n");
            }
        } catch (Exception e) {
            System.out.println("Bloque invalido y no añadido. Error: " + e + "\n");
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        }

    }

}