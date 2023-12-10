package com.lfcounago.javablockchain.commons.estructuras;

import java.util.List;

public class CadenaDeBloques {

	// Lista de bloques en la cadena ordenados por altura
	private List<Bloque> bloques;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		CadenaDeBloques cadena = (CadenaDeBloques) obj;

		if (bloques.size() != cadena.getBloques().size()) {
			return false;
		}

		for (int i = 0; i < bloques.size(); i++) {
			if (bloques.get(i) != cadena.getBloques().get(i)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return bloques.toString();
	}

}
