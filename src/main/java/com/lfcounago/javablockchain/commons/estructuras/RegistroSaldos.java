package com.lfcounago.javablockchain.commons.estructuras;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.codec.binary.Base64;

public class RegistroSaldos {

    // Registro de saldos <encoded public key, saldo>
    private Hashtable<String, Double> saldos = new Hashtable<String, Double>();

    public RegistroSaldos() {
    }

    public Hashtable<String, Double> getSaldos() {
        return saldos;
    }

    public void setSaldos(Hashtable<String, Double> saldos) {
        this.saldos = saldos;
    }

    public Double getSaldoCuenta(byte[] clavePublica) {
        return (this.saldos.containsKey(convertirClaveAString(clavePublica))
                ? this.saldos.get(convertirClaveAString(clavePublica))
                : 0.0);
    }

    public void setSaldoCuenta(byte[] clavePublica, Double saldo) {
        this.saldos.put(convertirClaveAString(clavePublica), saldo);
    }

    /**
     * Añade un saldo a la cuenta identificada por la clave pública de manera
     * sincronizada.
     *
     * @param clavePublica La clave pública que identifica la cuenta.
     * @param saldo        El saldo que se va a añadir a la cuenta.
     */
    public void añadeSaldoACuenta(byte[] clavePublica, Double saldo) {
        this.saldos.put(convertirClaveAString(clavePublica), getSaldoCuenta(clavePublica) + saldo);
    }

    /**
     * Liquida una transacción, actualizando los saldos de las cuentas involucradas.
     *
     * @param transaccion La transacción que se va a liquidar.
     * @throws Exception Si la transacción es inválida o si no hay suficiente saldo
     *                   en la cuenta del emisor.
     */
    public void liquidarTransaccion(Transaccion transaccion) throws Exception {
        if (transaccion.getEsCoinbase()) {
            this.añadeSaldoACuenta(transaccion.getDestinatario(), transaccion.getCantidad());
        } else {
            if (getSaldoCuenta(transaccion.getEmisor()) >= transaccion.getCantidad()) {
                this.añadeSaldoACuenta(transaccion.getEmisor(), -transaccion.getCantidad());
                this.añadeSaldoACuenta(transaccion.getDestinatario(), transaccion.getCantidad());
            } else {
                throw new Exception("No hay suficiente saldo en cuenta emisor.");
            }
        }
    }

    /**
     * Verifica si existe una cuenta asociada a la clave pública proporcionada.
     *
     * @param cuenta La clave pública que se va a verificar.
     * @return true si existe una cuenta asociada a la clave pública, false de lo
     *         contrario.
     */
    public boolean existeCuenta(byte[] cuenta) {
        return this.saldos.containsKey(convertirClaveAString(cuenta));
    }

    /**
     * Devuelve una representación de cadena de los saldos de las cuentas.
     *
     * @return Una cadena que representa los saldos de las cuentas.
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        Enumeration<String> cuentas = saldos.keys();
        buf.append("CLAVE PUBLICA | SALDO\n");
        buf.append("---------------------\n");
        while (cuentas.hasMoreElements()) {
            String cuenta = cuentas.nextElement();
            buf.append(cuenta.substring(0, 10) + "...");
            buf.append(" | ");
            buf.append(saldos.get(cuenta));
            if (cuentas.hasMoreElements())
                buf.append("\n");
        }
        return buf.toString();
    }

    /**
     * Convierte una clave pública representada como un arreglo de bytes a una
     * cadena Base64.
     *
     * @param clave La clave pública que se va a convertir.
     * @return La representación en cadena de la clave pública en formato Base64.
     */
    private String convertirClaveAString(byte[] clave) {
        return Base64.encodeBase64String(clave);
    }

}
