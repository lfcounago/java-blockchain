package com.lfcounago.javablockchain;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public final class Configuracion {

    private XMLConfiguration xmlConfiguracion = null;
    private static Configuracion configuracion = null;

    /**
     * Este método se utiliza para obtener una instancia de la clase Configuracion.
     *
     * @return La única instancia de la clase Configuracion.
     * @throws ConfigurationException si hay un error al cargar el archivo de
     *                                configuración.
     */
    public static final Configuracion getInstancia() {
        if (configuracion == null) {
            configuracion = new Configuracion();
            configuracion.xmlConfiguracion = new XMLConfiguration();
            configuracion.xmlConfiguracion.setFileName("configuracion.xml");
            try {
                configuracion.xmlConfiguracion.load();
            } catch (ConfigurationException e) {
                System.out.println("Error al leer el archivo de configuracion: " + e);
            }
        }
        return configuracion;
    }

    /**
     * Este método se utiliza para obtener la URL del nodo master.
     * 
     * @return La URL del nodo master
     */
    public String getUrlNodoMaster() {
        // return configuracion.xmlConfiguracion.getString("urlNodoMaster");
        return "http://localhost:8080";
    }

    /**
     * Este método se utiliza para obtener el número máximo de transacciones que se
     * pueden incluir en un bloque.
     *
     * @return El número máximo de transacciones que se pueden incluir en un bloque.
     */
    public int getMaxNumeroTransaccionesEnBloque() {
        // return configuracion.xmlConfiguracion.getInt("maxTransaccionesPorBloque");
        return 10;
    }

    /**
     * Este método se utiliza para obtener la dificultad de la prueba de trabajo
     * requerida para minar un bloque.
     *
     * @return La dificultad de la prueba de trabajo requerida para minar un bloque.
     */
    public int getDificultad() {
        // return configuracion.xmlConfiguracion.getInt("dificultad");
        return 0;
    }
}
