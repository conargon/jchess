package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

public class Caballo extends Pieza {

    private static final int VALOR_BASE = 315;

    public Caballo(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.CABALLO, colorPieza, columna, fila);
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        boolean ok =
                (this.columna+1 == columnaDestino && this.fila+2 == filaDestino)
                ||
                (this.columna+1 == columnaDestino && this.fila-2 == filaDestino)
                ||
                (this.columna-1 == columnaDestino && this.fila+2 == filaDestino)
                ||
                (this.columna-1 == columnaDestino && this.fila-2 == filaDestino)
                ||
                (this.columna+2 == columnaDestino && this.fila+1 == filaDestino)
                ||
                (this.columna+2 == columnaDestino && this.fila-1 == filaDestino)
                ||
                (this.columna-2 == columnaDestino && this.fila+1 == filaDestino)
                ||
                (this.columna-2 == columnaDestino && this.fila-1 == filaDestino)
                ;
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
        int result = VALOR_BASE;
        // dependiendo de lo cercano que esté del centro
        // añadimos o quitamos puntos
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