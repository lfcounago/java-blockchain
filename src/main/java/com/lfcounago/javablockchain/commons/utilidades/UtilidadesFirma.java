package com.lfcounago.javablockchain.commons.utilidades;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class UtilidadesFirma {

    /**
     * Factoria de claves que se inicializa con el algoritmo para generar los pares
     * de claves publica-privada
     */
    private static KeyFactory keyFactory = null;

    /**
     * Bloque de inicialización estático que se ejecuta una vaz al cargar la clase
     * para instanciar la KeyFactory con el algoritmo DSA y el proveedor SUN.
     */
    static {
        try {
            keyFactory = KeyFactory.getInstance("DSA", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
        }
    }

    /**
     * Genera un par de claves pública-privada utilizando el algoritmo DSA.
     *
     * @return Un par de claves pública-privada.
     * @throws NoSuchProviderException  Si el proveedor de seguridad "SUN" no está
     *                                  disponible.
     * @throws NoSuchAlgorithmException Si el algoritmo "DSA" no está disponible.
     */
    public static KeyPair generarParClaves() throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        // SHA1PRNG: Algoritmo para la generación de números pseudoaleatorios con SUN.
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        return keyGen.generateKeyPair();
    }

    /**
     * Valida una firma para unos datos y clave pública dados.
     *
     * @param datosVerificar Los datos que se deben verificar.
     * @param firmaVerificar La firma que se debe verificar.
     * @param clavePublica   La clave pública que se debe usar para la verificación.
     * @return true si la firma es válida para los datos y clave pública dados,
     *         false en caso contrario.
     * @throws InvalidKeySpecException  Si la clave pública proporcionada no es
     *                                  válida.
     * @throws NoSuchProviderException  Si el proveedor de seguridad "SUN" no está
     *                                  disponible.
     * @throws NoSuchAlgorithmException Si el algoritmo "DSA" no está disponible.
     * @throws SignatureException       Si hay un error al verificar la firma.
     * @throws InvalidKeyException      Si la clave pública proporcionada no es
     *                                  válida.
     */
    public static boolean validarFirma(byte[] datosVerificar, byte[] firmaVerificar, byte[] clavePublica)
            throws InvalidKeySpecException,
            NoSuchProviderException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        // crear un objeto PublicKey con la clave pública dada
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clavePublica);
        PublicKey publicKeyObj = keyFactory.generatePublic(keySpec);

        // validar firma
        Signature sig = getInstanciaSignature();
        sig.initVerify(publicKeyObj);
        sig.update(datosVerificar);
        return sig.verify(firmaVerificar);
    }

    /**
     * Firma datos con la clave privada dada.
     *
     * @param datosFirmar  Los datos que se deben firmar.
     * @param clavePrivada La clave privada que se debe usar para firmar los datos.
     * @return La firma de los datos, que puede ser verificada con los datos y la
     *         clave pública correspondiente.
     * @throws Exception Si hay un error al firmar los datos.
     */
    public static byte[] firmar(byte[] datosFirmar, byte[] clavePrivada) throws Exception {
        // crear objeto PrivateKey con la clave dada
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clavePrivada);
        PrivateKey privateKeyObj = keyFactory.generatePrivate(keySpec);

        // firmar
        Signature sig = getInstanciaSignature();
        sig.initSign(privateKeyObj);
        sig.update(datosFirmar);
        return sig.sign();
    }

    /**
     * Obtiene una instancia de Signature utilizando el algoritmo SHA1withDSA.
     *
     * @return Una instancia de Signature.
     * @throws NoSuchProviderException  Si el proveedor de seguridad "SUN" no está
     *                                  disponible.
     * @throws NoSuchAlgorithmException Si el algoritmo "SHA1withDSA" no está
     *                                  disponible.
     */
    private static Signature getInstanciaSignature() throws NoSuchProviderException, NoSuchAlgorithmException {
        // SHA1withDSA: Algoritmo de firma DSA (Digital Signature Algorithm) con SHA-1
        // (Secure Hash Algorithm 1) para crear y verificar firmas digitales.
        return Signature.getInstance("SHA1withDSA", "SUN");
    }

}
