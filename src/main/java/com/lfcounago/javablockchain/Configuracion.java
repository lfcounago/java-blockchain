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
        return configuracion.xmlConfiguracion.getString("urlNodoMaster");
    }

    /**
     * Este método se utiliza para obtener el número máximo de transacciones que se
     * pueden incluir en un bloque.
     *
     * @return El número máximo de transacciones que se pueden incluir en un bloque.
     */
    public int getMaxNumeroTransaccionesEnBloque() {
        return configuracion.xmlConfiguracion.getInt("maxTransaccionesPorBloque");
    }

    /**
     * Este método se utiliza para obtener la dificultad de la prueba de trabajo
     * requerida para minar un bloque.
     *
     * @return La dificultad de la prueba de trabajo requerida para minar un bloque.
     */
    public int getDificultad() {
        return configuracion.xmlConfiguracion.getInt("dificultad");
    }

    /**
     * Obtiene el valor booleano que indica si se debe minar según la configuración.
     *
     * @return true si se debe minar, false de lo contrario.
     */
    public boolean getMinar() {
        return configuracion.xmlConfiguracion.getBoolean("minar");
    }

    /**
     * Obtiene la dirección de la billetera (coinbase) según la configuración.
     *
     * @return La dirección de la billetera (coinbase).
     */
    public String getCoinbase() {
        return configuracion.xmlConfiguracion.getString("coinbase");
    }

    /**
     * Obtiene la cantidad de criptomoneda a minar según la configuración.
     *
     * @return La cantidad de criptomoneda a minar.
     */
    public double getCantidadCoinbase() {
        return configuracion.xmlConfiguracion.getDouble("cantidadCoinbase");
    }

}
