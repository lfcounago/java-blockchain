package com.lfcounago.javablockchain.nodo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lfcounago.javablockchain.Configuracion;
import com.lfcounago.javablockchain.commons.estructuras.Bloque;
import com.lfcounago.javablockchain.commons.estructuras.RegistroSaldos;
import com.lfcounago.javablockchain.commons.estructuras.Transaccion;

@Service
public class ServiceMinado implements Runnable {
    private final ServiceTransacciones servicioTransacciones;
    private final ServiceNodo servicioNodo;
    private final ServiceBloques servicioBloques;

    private AtomicBoolean runMinado = new AtomicBoolean(false);

    /**
     * Constructor del servicio de minado.
     *
     * @param servicioTransacciones El servicio de transacciones.
     * @param servicioNodo          El servicio del nodo.
     * @param servicioBloques       El servicio de bloques.
     */
    @Autowired
    public ServiceMinado(ServiceTransacciones servicioTransacciones, ServiceNodo servicioNodo,
            ServiceBloques servicioBloques) {
        this.servicioTransacciones = servicioTransacciones;
        this.servicioNodo = servicioNodo;
        this.servicioBloques = servicioBloques;
    }

    /**
     * Comienza el servicio de minado si no está en ejecución.
     */
    public void startMinado() {
        if (runMinado.compareAndSet(false, true)) {
            System.out.println("Comenzando minado...");
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Detiene el servicio de minado.
     */
    public void pararMinado() {
        System.out.println("Parando minado...");
        runMinado.set(false);
    }

    /**
     * Reinicia el servicio de minado deteniéndolo y volviéndolo a iniciar.
     */
    public void restartMinado() {
        System.out.println("Restarting minado...");
        this.pararMinado();
        this.startMinado();
    }

    /**
     * Búsqueda de bloque válido y propagación en la red.
     */
    @Override
    public void run() {
        while (runMinado.get()) {
            Bloque bloque = minarBloque();
            if (bloque != null) {
                System.out.println("NUEVO BLOQUE MINADO:");
                System.out.println(bloque);
                System.out.println("\n");

                // Añadir el bloque a la cadena y propagarlo
                try {
                    servicioBloques.añadirBloque(bloque);
                    servicioNodo.emitirPeticionPostNodosVecinos("bloque", bloque);
                } catch (Exception e) {
                    // Bloque inválido
                }
            }
        }
    }

    /**
     * Itera sobre el nonce hasta encontrar un bloque válido según la dificultad
     * configurada.
     *
     * @return El bloque minado.
     */
    private Bloque minarBloque() {
        long nonce = 0;

        Bloque ultimoBloque = servicioBloques.getCadenaDeBloques().getUltimoBloque();
        byte[] hashUltimoBloque = ultimoBloque != null
                ? ultimoBloque.getHash()
                : null;

        // Saldos temporales para verificar si una transacción hace doble gasto
        RegistroSaldos saldosTemporales = new RegistroSaldos();
        RegistroSaldos saldosActuales = servicioBloques.getCadenaDeBloques().getSaldos();
        Iterator<Transaccion> it = servicioTransacciones.getPoolTransacciones().getPool().iterator();
        while (it.hasNext()) {
            Transaccion transaccion = it.next();
            if (saldosActuales.existeCuenta(transaccion.getEmisor())) {
                saldosTemporales.setSaldoCuenta(transaccion.getEmisor(),
                        saldosActuales.getSaldoCuenta(transaccion.getEmisor()));
            }
        }

        List<Transaccion> transaccionesBloque = new ArrayList<>();

        // Iterar sobre las transacciones y añadirlas al bloque si el emisor tiene saldo
        it = servicioTransacciones.getPoolTransacciones().getPool().iterator();
        while (transaccionesBloque.size() < Configuracion.getInstancia().getMaxNumeroTransaccionesEnBloque()
                && it.hasNext()) {
            Transaccion transaccion = it.next();
            try {
                if (saldosTemporales.existeCuenta(transaccion.getEmisor()))
                    saldosTemporales.liquidarTransaccion(transaccion);
                else
                    throw new Exception("La cuenta del emisor " + Base64.encodeBase64String(transaccion.getEmisor())
                            + " no existe");
            } catch (Exception e) {
                // No incluir transacción si hace doble gasto
                System.out.println("Transacción " + transaccion.getHash() + " no incluida por saldo insuficiente");
            }
        }

        // Añadir transacción coinbase como recompensa por resolver la prueba de trabajo
        Transaccion txCoinbase = new Transaccion(Base64.decodeBase64(Configuracion.getInstancia().getCoinbase()));
        txCoinbase.setTimestamp(System.currentTimeMillis());
        txCoinbase.setHash(txCoinbase.calcularHashTransaccion());

        transaccionesBloque.add(0, txCoinbase);

        // Iterar sobre el nonce hasta encontrar la solución
        while (runMinado.get()) {
            if (ultimoBloque != servicioBloques.getCadenaDeBloques().getUltimoBloque())
                return null;
            Bloque bloque = new Bloque(hashUltimoBloque, transaccionesBloque, nonce);
            if (bloque.getNumeroDeCerosHash() >= Configuracion.getInstancia().getDificultad()) {
                return bloque;
            }
            nonce++;
        }
        return null;
    }
}
