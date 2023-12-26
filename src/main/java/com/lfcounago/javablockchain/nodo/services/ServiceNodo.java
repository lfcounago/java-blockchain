package com.lfcounago.javablockchain.nodo.services;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.security.KeyPair;

import org.apache.commons.codec.binary.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lfcounago.javablockchain.Configuracion;
import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.Transaccion;
import com.lfcounago.javablockchain.commons.utilidades.UtilidadesFirma;

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
    public boolean inicializado = false;

    @Autowired
    public ServiceNodo(ServiceBloques servicioCadenaDeBloques, ServiceTransacciones servicioTransacciones) {
        this.servicioBloques = servicioCadenaDeBloques;
        this.servicioTransacciones = servicioTransacciones;
    }

    /**
     * Maneja el evento de inicialización del servidor web y realiza las operaciones
     * necesarias para configurar
     * y sincronizar el nodo en la red blockchain. Se ejecuta automáticamente al
     * iniciar la aplicación.
     *
     * @param webServerInitializedEvent El evento de inicialización del servidor
     *                                  web.
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
            // crear bloque genesis
            /*
             * try { Bloque genesis = this.getBloqueGenesis();
             * //servicioBloques.añadirBloque(genesis); } catch (Exception e) {
             * System.out.println("No se pudo añadir el bloque génesis"); }
             */

        } else {
            nodosVecinos.add(urlNodoMaster);

            // obtener lista de nodos, bloques y transacciones
            obtenerNodosVecinos(urlNodoMaster, restTemplate);
            servicioBloques.obtenerCadenaDeBloques(urlNodoMaster, restTemplate);
            servicioTransacciones.obtenerPoolTransacciones(urlNodoMaster, restTemplate);

            // dar de alta mi nodo en el resto de nodos en la red
            emitirPeticionPostNodosVecinos("nodo", miUrlNodo);
        }

        inicializado = true;
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

    /**
     * Genera y devuelve un bloque génesis con una única transacción coinbase. La
     * transacción coinbase
     * es generada a partir de la configuración del sistema y se establece el
     * timestamp y el hash correspondiente.
     *
     * @return El bloque génesis generado.
     * @throws Exception Si hay un error al generar el bloque génesis.
     */
    private Bloque getBloqueGenesis() throws Exception {
        // Clave pública de la transacción coinbase en formato Base64
        String coinbasePublicKey = Configuracion.getInstancia().getCoinbase();

        // Crear una transacción coinbase
        Transaccion txCoinbase = new Transaccion(Base64.decodeBase64(coinbasePublicKey));
        txCoinbase.setTimestamp(System.currentTimeMillis());
        txCoinbase.setHash(txCoinbase.calcularHashTransaccion());

        // Crear un bloque génesis con la transacción coinbase
        List<Transaccion> transacciones = new ArrayList<>(Arrays.asList(txCoinbase));
        Bloque bloqueGenesis = new Bloque(null, transacciones, 0);

        return bloqueGenesis;
    }

    /**
     * Devuelve la URL del nodo actual.
     *
     * @return URL del nodo.
     */
    public URL getMyURL() {
        return this.miUrlNodo;
    }

}