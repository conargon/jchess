package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

import static java.lang.Math.abs;

public class Torre extends Pieza {

    private static final int VALOR_BASE = 500;

    public Torre(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.TORRE, colorPieza, columna, fila);
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        return caminoLibre(columnaDestino, filaDestino);
    }

    @Override
    protected boolean caminoLibre(int columnaDestino, int filaDestino) {
        if(this.columna == columnaDestino && this.fila != filaDestino) {
            // misma columna, distinta fila
            int deltaFila = filaDestino > this.fila ? 1 : -1;
            int j=this.fila+deltaFila;
            for(int c = 0; c < abs(filaDestino-this.fila); c++) {
                Pieza pieza = tablero.getPieza(columnaDestino, j);
                if(pieza != null) {
                    if(j == filaDestino) {
                        return !this.colorPieza.equals(pieza.colorPieza);
                    } else {
                        return false;
                    }
                }
                j += deltaFila;
            }
            return true;
        } else if (this.columna != columnaDestino && this.fila == filaDestino) {
            // misma fila, distinta columna
            int deltaColumna = columnaDestino > this.columna ? 1 : -1;
            int i=this.columna+deltaColumna;
            for(int c = 0; c < abs(columnaDestino-this.columna); c++) {
                Pieza pieza = tablero.getPieza(i, filaDestino);
                if(pieza != null) {
                    if(i == columnaDestino) {
                        return !this.colorPieza.equals(pieza.colorPieza);
                    } else {
                        return false;
                    }
                }
                i += deltaColumna;
            }
            return true;
        }
        return false;
    }

    @Override
    public int getValor() {
        int result = VALOR_BASE;
        // por cada movimiento que podamos hacer, añadimos un punto
        result += getMovimientosPosibles().size();
        return result;
    }

}
