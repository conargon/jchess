package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

import static java.lang.Math.abs;

public class Alfil extends Pieza {

    private static final int VALOR_BASE = 330;

    public Alfil(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.ALFIL, colorPieza, columna, fila);
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        boolean ok = (abs(this.columna - columnaDestino) == abs(this.fila - filaDestino));
        if(ok) {
            ok = caminoLibre(columnaDestino, filaDestino);
        }
        return ok;
    }

    @Override
    protected boolean caminoLibre(int columnaDestino, int filaDestino) {
        int deltaFila = filaDestino > this.fila ? 1 : -1;
        int deltaColumna = columnaDestino > this.columna ? 1 : -1;
        int i=this.columna+deltaColumna;
        int j=this.fila+deltaFila;
        for(int c=0; c < abs(columnaDestino-this.columna); c++) {
            Pieza pieza = tablero.getPieza(i, j);
            if(pieza != null) {
                if(i==columnaDestino) {
                    return !this.colorPieza.equals(pieza.colorPieza);
                }
                return false;
            }
            i += deltaColumna;
            j += deltaFila;
        }
        return true;
    }

    @Override
    public int getValor() {
        int result = VALOR_BASE;
        // por cada movimiento que podamos hacer, añadimos un punto
        result += getMovimientosPosibles().size();
        return result;
    }
}