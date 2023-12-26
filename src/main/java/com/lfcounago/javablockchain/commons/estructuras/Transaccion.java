package com.lfcounago.javablockchain.commons.estructuras;

import com.google.common.primitives.Longs;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.lfcounago.javablockchain.commons.utilidades.UtilidadesFirma;
import com.lfcounago.javablockchain.Configuracion;

import java.util.Arrays;
import java.util.Date;

public class Transaccion {

    // Hash de la transacción e identificador único de esta
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

    // Timestamp de la creación de la transacción en milisegundos desde el 1/1/1970
    private long timestamp;

    private boolean esCoinbase;

    public Transaccion() {
    }

    public Transaccion(byte[] emisor, byte[] receptor, double cantidad, byte[] firma) {
        this.esCoinbase = false;
        this.emisor = emisor;
        this.destinatario = receptor;
        this.cantidad = cantidad;
        this.firma = firma;
        this.timestamp = System.currentTimeMillis();
        this.hash = calcularHashTransaccion();
    }

    // coinbase
    public Transaccion(byte[] receptor) {
        this.esCoinbase = true;
        this.destinatario = receptor;
        this.cantidad = Configuracion.getInstancia().getCantidadCoinbase();
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

    public boolean getEsCoinbase() {
        return esCoinbase;
    }

    public void setEsCoinbase(boolean esCoinbase) {
        this.esCoinbase = esCoinbase;
    }

    /**
     * Obtiene el contenido de la transacción como un arreglo de bytes.
     *
     * @return Un arreglo de bytes que representa el contenido de la transacción,
     *         incluyendo la cantidad, emisor, destinatario y marca de tiempo.
     */
    public byte[] getContenidoTransaccion() {
        byte[] contenido = ArrayUtils.addAll(String.valueOf(cantidad).getBytes());
        contenido = ArrayUtils.addAll(contenido, emisor);
        contenido = ArrayUtils.addAll(contenido, destinatario);
        contenido = ArrayUtils.addAll(contenido, Longs.toByteArray(timestamp));
        return contenido;
    }

    /**
     * Calcula y devuelve el hash SHA-256 del contenido de la transacción.
     *
     * @return Un arreglo de bytes que representa el hash SHA-256 del contenido de
     *         la transacción.
     */
    public byte[] calcularHashTransaccion() {
        return DigestUtils.sha256(getContenidoTransaccion());
    }

    /**
     * Verifica si la transacción es válida, realizando diversas comprobaciones,
     * incluyendo la validez del destinatario,
     * la cantidad, la firma y el hash de la transacción.
     *
     * @return true si la transacción es válida, false de lo contrario.
     */
    public boolean esValida() {

        if (this.destinatario == null) {
            System.out.println("Destinatario inválido");
            return false;
        }

        if (this.cantidad > 0) {
            System.out.println("Cantidad inválida");
            return false;
        }

        if (this.firma == null) {
            System.out.println("Firma inválida");
            return false;
        }
        // Verificar hash
        if (!Arrays.equals(getHash(), calcularHashTransaccion())) {
            System.out.println("Hash de transacción inválido");
            return false;
        }

        // No coinbase tx
        if (!this.esCoinbase) {
            if (this.emisor == null) {
                System.out.println("Emisor inválido");
                return false;
            }

            // Verificar firma
            if (!UtilidadesFirma.validarFirma(getContenidoTransaccion(), getFirma(), emisor)) {
                System.out.println("Firma de transacción inválida");
                return false;
            }
        }
        // Coinbase tx
        else {
            if (this.cantidad != Configuracion.getInstancia().getCantidadCoinbase()) {
                System.out.println("Cantidad inválida");
                return false;
            }
        }
        return true;
    }

    /**
     * Compara esta transacción con otro objeto para determinar si son iguales.
     *
     * @param o El objeto con el que se va a comparar.
     * @return true si son iguales, false de lo contrario.
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
     * Calcula y devuelve el código hash de esta transacción.
     *
     * @return El código hash de la transacción.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    /**
     * Devuelve una representación de cadena de esta transacción.
     *
     * @return Una cadena que representa la transacción en formato JSON.
     */
    @Override
    public String toString() {
        return "{\nHash: " + Base64.encodeBase64String(hash) + ",\nEmisor: " + Base64.encodeBase64String(emisor)
                + ",\nDestinatario: "
                + Base64.encodeBase64String(destinatario) + ",\nCantidad: " + cantidad + ",\nFirma: "
                + Base64.encodeBase64String(firma)
                + ",\nTimestamp: " + new Date(timestamp) + "\n}";
    }

}
