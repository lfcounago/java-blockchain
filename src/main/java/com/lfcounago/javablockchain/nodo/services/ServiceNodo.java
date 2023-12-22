package com.lfcounago.javablockchain.nodo.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.Configuracion;

import jakarta.annotation.PreDestroy;

@Service
public class ServiceNodo implements ApplicationListener<WebServerInitializedEvent> {

    private final ServiceBloques servicioBloques;
    private final ServiceTransacciones servicioTransacciones;

    // URL de mi nodo (host + port)
    private URL miUrlNodo;

    // nodos en la red
    private Set<URL> nodosVecinos = new HashSet<>();

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ServiceNodo(ServiceBloques servicioCadenaDeBloques, ServiceTransacciones servicioTransacciones) {
        this.servicioBloques = servicioCadenaDeBloques;
        this.servicioTransacciones = servicioTransacciones;
    }

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * Obtiene la lista de nodos en la red, la cadena de bloques, las transacciones
     * en el pool y registra el nodo en la red.
     *
     * @param webServerInitializedEvent Evento que se dispara cuando el servidor web
     *                                  se ha inicializado.
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        // obtener la url del nodo master
        URL urlNodoMaster = getNodoMaster();

        // calcular mi url (host y puerto)
        String host = getIpPublica(urlNodoMaster, restTemplate);
        int port = webServerInitializedEvent.getWebServer().getPort();

        miUrlNodo = getMiUrlNodo(host, port);

        // descargar cadena de bloques y transacciones en pool si no soy nodo master
        if (miUrlNodo.equals(urlNodoMaster)) {
            System.out.println("Ejecutando nodo master");
        } else {
            nodosVecinos.add(urlNodoMaster);

            // obtener lista de nodos, bloques y transacciones
            obtenerNodosVecinos(urlNodoMaster, restTemplate);
            servicioBloques.obtenerCadenaDeBloques(urlNodoMaster, restTemplate);
            servicioTransacciones.obtenerPoolTransacciones(urlNodoMaster, restTemplate);

            // dar de alta mi nodo en el resto de nodos en la red
            emitirPeticionPostNodosVecinos("nodo", miUrlNodo);
        }
    }

    /**
     * Método que se ejecuta antes de que la aplicación se cierre.
     * Envía una petición para que el resto de nodos den de baja este nodo.
     */
    @PreDestroy
    public void shutdown() {
        System.out.println("Parando nodo...");
        // enviar peticion para que el resto de nodos den de baja mi nodo
        emitirPetitionDeleteNodosVecinos("nodo", miUrlNodo);
    }

    /**
     * Obtiene los nodos vecinos en la red.
     *
     * @return Un conjunto de URLs de los nodos vecinos.
     */
    public Set<URL> getNodosVecinos() {
        return nodosVecinos;
    }

    /**
     * Añade un nodo a la lista de nodos vecinos.
     *
     * @param urlNodo La URL del nodo a añadir.
     */
    public synchronized void altaNodo(URL urlNodo) {
        nodosVecinos.add(urlNodo);
    }

    /**
     * Elimina un nodo de la lista de nodos vecinos.
     *
     * @param urlNodo La URL del nodo a eliminar.
     */
    public synchronized void bajaNodo(URL urlNodo) {
        nodosVecinos.remove(urlNodo);
    }

    /**
     * Envía una petición PUT a todos los nodos vecinos.
     *
     * @param endpoint El endpoint para la petición.
     * @param datos    Los datos a enviar con la petición.
     */
    public void emitirPeticionPutNodosVecinos(String endpoint, Object datos) {
        nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.put(urlNodo + "/" + endpoint, datos));
    }

    /**
     * Envía una petición POST a todos los nodos vecinos.
     *
     * @param endpoint El endpoint para la petición.
     * @param datos    Los datos a enviar con la petición.
     */
    public void emitirPeticionPostNodosVecinos(String endpoint, Object data) {
        nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.postForLocation(urlNodo + "/" + endpoint, data));
    }

    /**
     * Envía una petición DELETE a todos los nodos vecinos.
     *
     * @param endpoint El endpoint para la petición.
     * @param datos    Los datos a enviar con la petición.
     */
    public void emitirPetitionDeleteNodosVecinos(String endpoint, Object data) {
        nodosVecinos.parallelStream().forEach(urlNodo -> restTemplate.delete(urlNodo + "/" + endpoint, data));
    }

    /**
     * Obtiene la lista de nodos vecinos de un nodo dado.
     *
     * @param urlNodoVecino La URL del nodo vecino.
     * @param restTemplate  El RestTemplate a usar para la petición HTTP.
     */
    public void obtenerNodosVecinos(URL urlNodoVecino, RestTemplate restTemplate) {
        URL[] nodos = restTemplate.getForObject(urlNodoVecino + "/nodo", URL[].class);
        Collections.addAll(nodosVecinos, nodos);
    }

    /**
     * Obtiene la IP pública del nodo.
     *
     * @param urlNodoVecino La URL del nodo vecino.
     * @param restTemplate  El RestTemplate a usar para la petición HTTP.
     * @return La IP pública del nodo.
     */
    private String getIpPublica(URL urlNodoVecino, RestTemplate restTemplate) {
        return restTemplate.getForObject(urlNodoVecino + "/nodo/ip", String.class);
    }

    /**
     * Construye la URL del nodo a partir del host y el puerto.
     *
     * @param host El host público.
     * @param port El puerto en el que se lanza el servicio.
     * @return La URL del nodo.
     */
    private URL getMiUrlNodo(String host, int port) {
        try {
            return new URL("http", host, port, "");
        } catch (MalformedURLException e) {
            System.out.println("Invalida URL Nodo:" + e);
            return null;
        }
    }

    /**
     * Obtiene la URL del nodo maestro desde el archivo de configuración.
     *
     * @return La URL del nodo maestro.
     */
    private URL getNodoMaster() {
        try {
            return new URL(Configuracion.getInstancia().getUrlNodoMaster());
        } catch (MalformedURLException e) {
            System.out.println("Invalida URL Nodo Master:" + e);
            return null;
        }
    }

}