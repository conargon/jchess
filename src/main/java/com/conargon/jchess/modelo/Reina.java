package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

import static java.lang.Math.abs;

public class Reina extends Pieza {

    private static final int VALOR_BASE = 940;

    public Reina(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.REINA, colorPieza, columna, fila);
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        boolean ok = (this.columna == columnaDestino && this.fila != filaDestino)
                ||
                (this.columna != columnaDestino && this.fila == filaDestino)
                ||
                (abs(this.columna - columnaDestino) == abs(this.fila - filaDestino));
        if(ok) {
            ok = caminoLibre(columnaDestino, filaDestino);
        }
        return ok;
    }

    @Override
    protected boolean caminoLibre(int columnaDestino, int filaDestino) {
        // ........alfil
        if((abs(this.columna - columnaDestino) == abs(this.fila - filaDestino))) {
            int deltaFila = filaDestino > this.fila ? 1 : -1;
            int deltaColumna = columnaDestino > this.columna ? 1 : -1;
            int i = this.columna + deltaColumna;
            int j = this.fila + deltaFila;
            for (int c = 0; c < abs(columnaDestino - this.columna); c++) {
                Pieza pieza = tablero.getPieza(i, j);
                if (pieza != null) {
                    if(i==columnaDestino) {
                        return !this.colorPieza.equals(pieza.colorPieza);
                    }
                    return false;
                }
                i += deltaColumna;
                j += deltaFila;
            }
        } else {
            // .........torre
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
        }
        return true;
    }

    @Override
    public int getValor() {
        int result = VALOR_BASE;
        // dependiendo de lo cercano que esté del centro
        // añadimos o quitamos puntos (igual que el caballo)
        if(enCentroTablero()) {
            result += 15;
        } else if(cercaCentroTablero()) {
            result += 5;
        } else if(lejosCentroTablero()) {
            result -= 5;
        } else if(enBordeTablero()) {
            result -= 15;
        }
        return result;
    }
}
