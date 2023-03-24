package estructuras;

import com.google.common.primitives.Longs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

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

    // Firma con la clave privada para verificar que la transacción fue realmente enviada por el emisor
    private byte[] firma;

    // Marca temporal de la creación de la transacción en milisegundos desde el 1/1/1970
    private long marcaTemporal;

    public Transaccion() {
    }

    public Transaccion(byte[] emisor, byte[] receptor, double cantidad, byte[] firma) {
        this.emisor = emisor;
        this.destinatario = receptor;
        this.cantidad = cantidad;
        this.firma = firma;
        this.marcaTemporal = System.currentTimeMillis();
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

    public long getMarcaTemporal() {
        return marcaTemporal;
    }

    public void setMarcaTemporal(long marcaTemporal) {
        this.marcaTemporal = marcaTemporal;
    }

    /**
     * El contenido de la transaccion que es firmado por el emisor con su clave privada
     * @return byte[] Array de bytes representando el contenido de la transaccion
     */
    public byte[] getContenidoTransaccion() {
        //Adds all the elements of the given arrays into a new array.
    	byte[] contenido = ArrayUtils.addAll(String.valueOf(cantidad).getBytes());
    	contenido = ArrayUtils.addAll(contenido, emisor);
    	contenido = ArrayUtils.addAll(contenido, destinatario);
    	contenido = ArrayUtils.addAll(contenido, firma);
    	contenido = ArrayUtils.addAll(contenido, Longs.toByteArray(marcaTemporal));        
        return contenido;
    }

    /**
     * Calcular el hash del contenido de la transacción (identificador de la transacción)
     * @return Hash SHA256
     */
    public byte[] calcularHashTransaccion() {
        return DigestUtils.sha256(getContenidoTransaccion());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        } 
        if (obj == null || getClass() != obj.getClass()){
            return false;  
        } 

        Transaccion tran = (Transaccion) obj;

        return Arrays.equals(hash, tran.hash);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(hash);
    }

    @Override
    public String toString() {
        return "{" + hash + ", " + emisor + ", " + destinatario + ", " + cantidad + ", " + firma + ", " + new Date(marcaTemporal) + "}";
    }

}
