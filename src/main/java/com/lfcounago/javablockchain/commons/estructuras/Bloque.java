package com.lfcounago.javablockchain.commons.estructuras;

import com.google.common.primitives.Longs;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Bloque {
	// Hash del bloque e identificador único de éste. Usado para enlazar bloques.
	private byte[] hash;

	// Hash del bloque anterior.
	private byte[] hashBloqueAnterior;

	// Nonce calculado como solución a la prueba de trabajo
	private long nonce;

	// Marca temporal de creación del bloque
	private long marcaTemporal;

	// Root del arbol de merkle calculado a partir de las transacciones en el
	// bloque.
	private byte[] raizArbolMerkle;

	// Lista de transacciones incluidas en este bloque
	private List<Transaccion> transacciones;

	public Bloque() {
	}

	public Bloque(byte[] hashBloqueAnterior, List<Transaccion> transacciones, long nonce) {
		this.hashBloqueAnterior = hashBloqueAnterior;
		this.transacciones = transacciones;
		this.nonce = nonce;
		this.marcaTemporal = System.currentTimeMillis();
		this.raizArbolMerkle = calcularRaizArbolMerkle();
		this.hash = calcularHash();
	}

	public byte[] getHash() {
		return hash;
	}

	public void setHash(byte[] hash) {
		this.hash = hash;
	}

	public byte[] getHashBloqueAnterior() {
		return hashBloqueAnterior;
	}

	public void setHashBloqueAnterior(byte[] hashBloqueAnterior) {
		this.hashBloqueAnterior = hashBloqueAnterior;
	}

	public List<Transaccion> getTransacciones() {
		return transacciones;
	}

	public void setTransactions(List<Transaccion> transacciones) {
		this.transacciones = transacciones;
	}

	public byte[] getRaizArbolMerkle() {
		return raizArbolMerkle;
	}

	public void setRaizArbolMerkle(byte[] raizArbolMerkle) {
		this.raizArbolMerkle = raizArbolMerkle;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public long getMarcaTemporal() {
		return marcaTemporal;
	}

	public void setMarcaTemporal(long marcaTemporal) {
		this.marcaTemporal = marcaTemporal;
	}

	/**
	 * Calcular el hash del bloque a partir de la información de la cabecera del
	 * bloque (sin transacciones)
	 * 
	 * @return Hash SHA256
	 */
	public byte[] calcularHash() {
		byte[] hashableData = ArrayUtils.addAll(hashBloqueAnterior, raizArbolMerkle);
		hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(nonce));
		hashableData = ArrayUtils.addAll(hashableData, Longs.toByteArray(marcaTemporal));
		return DigestUtils.sha256(hashableData);
	}

	/**
	 * Calcular la raiz del arbol de merkle formado con las transacciones
	 * https://en.wikipedia.org/wiki/Merkle_tree
	 * 
	 * @return Hash SHA256
	 */
	public byte[] calcularRaizArbolMerkle() {
		Queue<byte[]> colaHashes = new LinkedList<>(
				transacciones.stream().map(Transaccion::getHash).collect(Collectors.toList()));
		while (colaHashes.size() > 1) {
			// calcular hash a partir de dos hashes previos
			byte[] info = ArrayUtils.addAll(colaHashes.poll(), colaHashes.poll());
			// añadir a la cola
			colaHashes.add(DigestUtils.sha256(info));
		}
		return colaHashes.poll();
	}

	/**
	 * Numero de ceros al principio del hash del bloque
	 * 
	 * @return int number of leading zeros
	 */
	public int getNumeroDeCerosHash() {
		for (int i = 0; i < getHash().length; i++) {
			if (getHash()[i] != 0) {
				return i;
			}
		}
		return getHash().length;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		Bloque bloque = (Bloque) obj;

		return Arrays.equals(hash, bloque.hash);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(hash);
	}

	@Override
	public String toString() {
		return "{Hash:" + hash + ", Previo:" + hashBloqueAnterior + ", RaizMerkle:" + raizArbolMerkle + ", Nonce:"
				+ nonce + ", marcaTemporal:" + new Date(marcaTemporal) + ", Transacciones:" + transacciones.toString()
				+ "}";
	}
}
