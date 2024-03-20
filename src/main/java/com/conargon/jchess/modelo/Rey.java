package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

public class Rey extends Pieza {

    private static final int VALOR_BASE = 0;

    public Rey(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.REY, colorPieza, columna, fila);
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        boolean ok = (this.fila == filaDestino || this.fila+1 == filaDestino || this.fila-1 == filaDestino)
                &&
                (this.columna == columnaDestino  || this.columna+1 == columnaDestino || this.columna-1 == columnaDestino);
        if(ok) {
            ok = caminoLibre(columnaDestino, filaDestino);
        }
        return ok;
    }

    @Override
    protected boolean caminoLibre(int columnaDestino, int filaDestino) {
        return !piezaMismoEquipoEn(columnaDestino, filaDestino);
    }

    @Override
    public int getValor() {
        return VALOR_BASE;
    }
}
