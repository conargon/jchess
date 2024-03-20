package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

public class Peon extends Pieza {

    private static final int VALOR_BASE = 100;
    protected int delta; // indica si avanzar es 1 o -1 (negras: 1, blancas: -1)

    public Peon(Tablero tablero, ColorPieza colorPieza, int columna, int fila) {
        super(tablero, TipoPieza.PEON, colorPieza, columna, fila);
        this.delta = ColorPieza.esBlanca(colorPieza) ? -1 : +1;
    }

    @Override
    public boolean puedeMoverA(int columnaDestino, int filaDestino) {
        // misma posición
        if(enPosicion(columnaDestino, filaDestino)) {
            return false;
        }
        boolean ok = false;
        boolean avanzarUnaCasilla = (this.fila + delta == filaDestino)
                &&  (this.columna == columnaDestino || this.columna == columnaDestino-1 || this.columna == columnaDestino+1);
        boolean avanzarDosCasillas =false;
        if(movimientos > 0) {
            // avanzar 1 casilla
            ok = avanzarUnaCasilla;
        } else {
            // avanzar 1 ó 2 casillas
            avanzarDosCasillas = ((this.fila + 2*delta == filaDestino) && (this.columna == columnaDestino));
            ok = avanzarUnaCasilla || avanzarDosCasillas;;
        }
        if(ok) {
            ok = caminoLibre(columnaDestino, filaDestino, avanzarDosCasillas);
        }
        return ok;
    }

    private boolean caminoLibre(int columnaDestino, int filaDestino, boolean avanzarDosCasillas) {
        if(avanzarDosCasillas) {
            Pieza pieza = tablero.getPieza(columnaDestino, this.fila+delta);
            if(pieza != null) {
                return false;
            }
        }
        return caminoLibre(columnaDestino, filaDestino);
    }

    @Override
    protected boolean caminoLibre(int columnaDestino, int filaDestino) {
        Pieza pieza = tablero.getPieza(columnaDestino, filaDestino);
        if(pieza != null) {
            if(!pieza.getColorPieza().equals(this.colorPieza) && this.columna != columnaDestino) {
                return true;
            } else {
                return false;
            }
        } else if (this.columna != columnaDestino) {
            return false;
        }
        return true;
    }

    @Override
    public int getValor() {
        int result = VALOR_BASE;
        // Si está en el centro del tablero 12 puntos más
        if(enCentroTablero()) {
            result += 12;
        }
        // 2 puntos por cada casilla que haya avanzado
        result += this.movimientos*2;
        return result;
    }
}