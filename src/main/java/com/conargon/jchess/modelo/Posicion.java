package com.conargon.jchess.modelo;

public class Posicion {

    private final static String[] letras = {"A","B","C","D","E","F","G","H"};
    private final static String[] numeros = {"8","7","6","5","4","3","2","1"};

    int columna;
    int fila;

    public Posicion(int columna, int fila) {
        this.columna = columna;
        this.fila = fila;
    }

    public Posicion(Posicion p) {
        this.columna = p.columna;
        this.fila = p.fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    @Override
    public String toString() {
        return letras[columna] + numeros[fila];
    }

    public static String toString(int columna, int fila) {
        return letras[columna] + numeros[fila];
    }

}
