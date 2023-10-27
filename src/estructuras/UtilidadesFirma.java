package estructuras;

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

    // Se ejecuta una vez al cargar la clase
    static {
        try {
            keyFactory = KeyFactory.getInstance("DSA", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
        }
    }

    /**
     * Generar un par de claves publica-privada
     * 
     * @return KeyPair par de claves
     */
    public static KeyPair generarParClaves() throws NoSuchProviderException, NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        // SHA1PRNG: Algoritmo para la generación de números pseudoaleatorios con SUN.
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        return keyGen.generateKeyPair();
    }

    /**
     * Validar una firma para unos datos y clave publica dados
     * 
     * @return true si la firma es valida para los datos y clave publica dados
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
     * Firmar datos con la clave privada dada
     * 
     * @return firma de los datos, puede ser verificada con los datos y la
     *         clave pública
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

    private static Signature getInstanciaSignature() throws NoSuchProviderException, NoSuchAlgorithmException {
        // SHA1withDSA: Algoritmo de firma DSA (Digital Signature Algorithm) con SHA-1
        // (Secure Hash Algorithm 1) para crear y verificar firmas digitales.
        return Signature.getInstance("SHA1withDSA", "SUN");
    }

}
