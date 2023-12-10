package com.lfcounago.javablockchain.commons.estructuras;

import com.google.common.primitives.Longs;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.lfcounago.javablockchain.commons.utilidades.UtilidadesFirma;

import java.util.Arrays;
import java.util.Date;

public class Transaccion {

    // Hash de la transacción e identificador único de ésta
    private byte[] hash;

    // Clave pública del emisor de la transacción
    private byte[] emisor;

    // Clave pública del destinatario de la transacción
    private byte[] destinatario;

    // Valor a ser transferido
    private double cantidad;

    // Firma con la clave privada para verificar que la transacción fue realmente
    // enviada por el emisor
    private byte[] firma;

    // Marca temporal de la creación de la transacción en milisegundos desde el
    // 1/1/1970
    private long timestamp;

    public Transaccion() {
    }

    public Transaccion(byte[] emisor, byte[] receptor, double cantidad, byte[] firma) {
        this.emisor = emisor;
        this.destinatario = receptor;
        this.cantidad = cantidad;
        this.firma = firma;
        this.timestamp = System.currentTimeMillis();
        this.hash = calcularHashTransaccion();
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getEmisor() {
        return emisor;
    }

    public void setEmisor(byte[] emisor) {
        this.emisor = emisor;
    }

    public byte[] getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(byte[] destinatario) {
        this.destinatario = destinatario;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public byte[] getFirma() {
        return firma;
    }

    public void setFirma(byte[] firma) {
        this.firma = firma;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Este método se utiliza para obtener el contenido de la transacción como un
     * array de bytes.
     *
     * @return El contenido de la transacción como un array de bytes.
     */
    public byte[] getContenidoTransaccion() {
        byte[] contenido = ArrayUtils.addAll(String.valueOf(cantidad).getBytes());
        contenido = ArrayUtils.addAll(contenido, emisor);
        contenido = ArrayUtils.addAll(contenido, destinatario);
        contenido = ArrayUtils.addAll(contenido, Longs.toByteArray(timestamp));
        return contenido;
    }

    /**
     * Este método se utiliza para calcular el hash SHA256 del contenido de la
     * transacción.
     *
     * @return El hash SHA256 del contenido de la transacción.
     */
    public byte[] calcularHashTransaccion() {
        return DigestUtils.sha256(getContenidoTransaccion());
    }

    /**
     * Este método se utiliza para verificar si una transacción es válida.
     * Una transacción es válida si tiene un hash válido y la firma es válida.
     *
     * @return true si la transacción es válida, false en caso contrario.
     */
    public boolean esValida() {

        // verificar hash
        if (!Arrays.equals(getHash(), calcularHashTransaccion())) {
            System.out.println("Hash de transacción inválido");
            return false;
        }

        // verificar firma
        try {
            if (!UtilidadesFirma.validarFirma(getContenidoTransaccion(), getFirma(), emisor)) {
                System.out.println("Firma de transacción inválida");
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Compara esta transacción con el objeto especificado.
     *
     * @param o el objeto con el que se debe comparar esta transacción.
     * @return true si este objeto es el mismo que el objeto argumento; false en
     *         caso contrario.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Transaccion tr = (Transaccion) o;

        return Arrays.equals(hash, tr.hash);
    }

    /**
     * Devuelve un valor hash para esta transacción.
     *
     * @return un valor hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    /**
     * Devuelve una representación de cadena de esta transacción.
     *
     * @return una representación de cadena de esta transacción.
     */
    @Override
    public String toString() {
        return "{" + Base64.encodeBase64String(hash) + ", " + Base64.encodeBase64String(emisor) + ", "
                + Base64.encodeBase64String(destinatario) + ", " + cantidad + ", " + Base64.encodeBase64String(firma)
                + ", " + new Date(timestamp) + "}";
    }

}
