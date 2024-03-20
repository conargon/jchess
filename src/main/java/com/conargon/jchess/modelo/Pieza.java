package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;
import com.conargon.jchess.swt.SWTResourceManager;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Pieza implements Cloneable {

    protected Tablero tablero;
    protected TipoPieza tipoPieza;
    protected ColorPieza colorPieza;
    protected Image imagen;
    protected int fila; // 0..7
    protected int columna; // 0..7
    protected int movimientos = 0;


    public Pieza(Tablero tablero, TipoPieza tipoPieza, ColorPieza colorPieza, int columna, int fila) {
        this.tablero = tablero;
        this.tipoPieza = tipoPieza;
        this.colorPieza = colorPieza;
        this.columna = columna;
        this.fila = fila;
        this.movimientos = 0;
        loadImage();
    }

    private void loadImage() {
        this.imagen = SWTResourceManager.getImage(Pieza.class, tipoPieza.imagen(colorPieza));
    }

    public String getNombre() {
        return tipoPieza.nombre();
    }

    public TipoPieza getTipoPieza() {
        return tipoPieza;
    }

    public ColorPieza getColorPieza() {
        return colorPieza;
    }

    public Image getImagen() {
        return imagen;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public void moverA(int columna, int fila) {
        // si hay una pieza en el destino la eliminamos
        Pieza p = tablero.getPieza(columna, fila);
        if(p!= null && !p.getColorPieza().equals(colorPieza)) {
            tablero.eliminarPieza(p);
        }
        this.columna = columna;
        this.fila = fila;
        movimientos++;
        tablero.calcularCuadricula();
    }

    public void moverA(Posicion posicion) {
        moverA(posicion.getColumna(), posicion.getFila());
    }

    public boolean enPosicion(int columna, int fila) {
        return this.columna == columna && this.fila == fila;
    }

    public abstract boolean puedeMoverA(int columnaDestino, int filaDestino);

    public boolean puedeMoverA(Posicion posicion) {
        return puedeMoverA(posicion.getColumna(), posicion.getFila());
    }

    /**
     * Determina si el movimiento indicado pone en jaque
     * @param columnaDestino
     * @param filaDestino
     * @return
     */
    public boolean poneEnJaque(int columnaDestino, int filaDestino) {
        Tablero copia = tablero.copia();
        Pieza pieza = copia.getPieza(this.columna, this.fila);
        pieza.moverA(columnaDestino, filaDestino);
        return copia.esJaque(pieza.colorPieza);
    }

    /**
     * Determina si el movimiento indicado pone en jaque
     * @param posicion
     * @return
     */
    public boolean poneEnJaque(Posicion posicion) {
        return poneEnJaque(posicion.columna, posicion.fila);
    }

    /**
     * Determina si en la ruta indicada por la posición final hay otra pieza que impida el movimiento.
     * Asume que la posición final es alcanzable por la pieza.
     * @param columnaDestino
     * @param filaDestino
     * @return true si no hay piezas que obstaculicen el camino y false en otro caso
     */
    protected abstract boolean caminoLibre(int columnaDestino, int filaDestino);


    /**
     * Determina si en la posición indicada hay una pieza del adversario
     * @param columnaDestino
     * @param filaDestino
     * @return
     */
    protected boolean piezaContrariaEn(int columnaDestino, int filaDestino) {
        Pieza p = tablero.getPieza(columnaDestino, filaDestino);
        return p!= null && !this.colorPieza.equals(p.colorPieza);
    }

    /**
     * Determina si en la posición indicada hay una pieza del mismo ejercito
     * @param columnaDestino
     * @param filaDestino
     * @return
     */
    protected boolean piezaMismoEquipoEn(int columnaDestino, int filaDestino) {
        Pieza p = tablero.getPieza(columnaDestino, filaDestino);
        return p!= null && this.colorPieza.equals(p.colorPieza);
    }

    /**
     * Retorna las posiciones posibles a las que se puede mover la pieza
     * @return
     */
    public List<Posicion> getMovimientosPosibles() {
        List<Posicion> result = new ArrayList<>();
        for(int i=0; i < Tablero.COLUMNAS; i++) {
            for(int j=0; j < Tablero.FILAS; j++) {
                if(puedeMoverA(i, j)) {
                    result.add(new Posicion(i, j));
                }
            }
        }
        return result;
    }

    /**
     * Comprueba si puede mover la pieza (hay movimientos posibles)
     * @return
     */
    public boolean puedeMover() {
        return !getMovimientosPosibles().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pieza pieza = (Pieza) o;
        return fila == pieza.fila &&
                columna == pieza.columna &&
                tipoPieza == pieza.tipoPieza &&
                colorPieza == pieza.colorPieza;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoPieza, colorPieza, fila, columna);
    }

    @Override
    public String toString() {
        return  tipoPieza.nombre() + " " + colorPieza.nombre() + " " + Posicion.toString(columna, fila);
    }

    public Object clone()
    {
        Object obj=null;
        try{
            obj=super.clone();
        }catch(CloneNotSupportedException ex){
            System.out.println(" no se puede duplicar");
        }
        return obj;
    }

    public String getNombreColor() {
        return this.colorPieza == ColorPieza.BLANCO ? "blanco" : "negro";
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    /**
     * Retorna el valor actual de la pieza
     * @return
     */
    public abstract int getValor();

    protected boolean enCentroTablero() {
        return (this.fila == 3 || this.fila == 4) && (this.columna == 3 || this.columna == 4);
    }

    protected boolean cercaCentroTablero() {
        return (this.fila == 2 || this.fila == 5) && (this.columna == 2 || this.columna == 5);
    }

    protected boolean lejosCentroTablero() {
        return (this.fila == 1 || this.fila == 6) && (this.columna == 1 || this.columna == 6);
    }

    protected boolean enBordeTablero() {
        return (this.fila == 0 || this.fila == 7) && (this.columna == 0 || this.columna == 7);
    }

    public Posicion getPosicion() {
        return new Posicion(this.columna, this.fila);
    }

    public int getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(int movimientos) {
        this.movimientos = movimientos;
    }
}
