package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;
import com.conargon.jchess.tipo.Dificultad;
import com.conargon.jchess.tipo.ModoJuego;

public class Opciones {

    private ModoJuego modoJuego;
    private ColorPieza colorPieza;
    private Dificultad dificultad;

    public Opciones(ModoJuego modoJuego, ColorPieza colorPieza, Dificultad dificultad) {
        this.modoJuego = modoJuego;
        this.colorPieza = colorPieza;
        this.dificultad = dificultad;
    }

    public ModoJuego getModoJuego() {
        return modoJuego;
    }

    public void setModoJuego(ModoJuego modoJuego) {
        this.modoJuego = modoJuego;
    }

    public ColorPieza getColorPieza() {
        return colorPieza;
    }

    public void setColorPieza(ColorPieza colorPieza) {
        this.colorPieza = colorPieza;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }
}
