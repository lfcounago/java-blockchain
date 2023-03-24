package estructuras;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    
    public static void main(String[] args) {

        Transaccion trans1 = new Transaccion("A".getBytes(), "B".getBytes(), 100, "AB".getBytes());
        Transaccion trans2 = new Transaccion("A".getBytes(), "C".getBytes(), 50, "AC".getBytes());

        Bloque block = new Bloque("0x0".getBytes(), new ArrayList<Transaccion>(Arrays.asList(trans1, trans2)), 43);
        CadenaDeBloques cadBloques = new CadenaDeBloques(new ArrayList<Bloque>(Arrays.asList(block)));

        System.out.println(cadBloques);

    }

}
