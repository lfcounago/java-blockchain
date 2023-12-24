package com.lfcounago.javablockchain.commons.estructuras;

import java.util.ArrayList;
import java.util.List;

public class CadenaDeBloques {

	// Lista de bloques en la cadena ordenados por altura
	private List<Bloque> bloques = new ArrayList<Bloque>();

	public CadenaDeBloques() {
	}

	public CadenaDeBloques(List<Bloque> bloques) {
		this.bloques = bloques;
	}

	public List<Bloque> getBloques() {
		return bloques;
	}

	public void setBloques(List<Bloque> bloques) {
		this.bloques = bloques;
	}

	public boolean estaVacia() {
		return this.bloques == null || this.bloques.isEmpty();
	}

	public int getNumeroBloques() {
		return (estaVacia() ? 0 : this.bloques.size());
	}

	/**
	 * Este método se utiliza para obtener el último bloque de la cadena de bloques.
	 *
	 * @return El último bloque de la cadena de bloques, o null si la cadena de
	 *         bloques está vacía.
	 */
	public Bloque getUltimoBloque() {
		if (estaVacia()) {
			return null;
		}
		return this.bloques.get(this.bloques.size() - 1);
	}

	/**
	 * Este método se utiliza para añadir un bloque a la cadena de bloques.
	 *
	 * @param bloque El bloque que se va a añadir a la cadena de bloques.
	 */
	public void añadirBloque(Bloque bloque) {
		this.bloques.add(bloque);
	}

	/**
	 * Compara esta cadena de bloques con el objeto especificado.
	 *
	 * @param o el objeto con el que se debe comparar esta cadena de bloques.
	 * @return true si este objeto es el mismo que el objeto argumento; false en
	 *         caso contrario.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CadenaDeBloques cadena = (CadenaDeBloques) o;

		if (bloques.size() != cadena.getBloques().size())
			return false;

		for (int i = 0; i < bloques.size(); i++) {
			if (bloques.get(i) != cadena.getBloques().get(i))
				return false;
		}

		return true;
	}

	/**
	 * Devuelve una representación de cadena de esta cadena de bloques.
	 *
	 * @return una representación de cadena de esta cadena de bloques.
	 */
	@Override
	public String toString() {
		return bloques.toString();
	}

}
