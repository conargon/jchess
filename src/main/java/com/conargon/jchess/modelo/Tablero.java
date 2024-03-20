package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;
import com.conargon.jchess.swt.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import java.util.ArrayList;
import java.util.List;

public class Tablero {

    public static final int VALOR_JAQUE_MATE = 20_000;
    public static final int VALOR_JAQUE = 10_000;


    public static final int DIM_TABLERO = 400;
    public static final int BORDE_TABLERO = 17;
    public static final int TOTAL_DIM_TABLERO = DIM_TABLERO + BORDE_TABLERO*2;

    public static final int CASILLA = 50;
    public static final int FILAS = 8;
    public static final int COLUMNAS = 8;

    private Pieza[][] cuadricula = new Pieza[8][8];
    private List<Pieza> piezas = new ArrayList<>();
    private boolean seleccion;
    private int columnaSeleccionada=-1;
    private int filaSeleccionada=-1;

    private Image imageTablero;
    private Display display;

    public Tablero() {
        display = Display.getCurrent();
        imageTablero =  SWTResourceManager.getImage(Tablero.class, "img/tablero.png");
        crearPiezas();
    }

    private void crearPiezas() {
        crearPiezas(ColorPieza.NEGRO, 0, 1);
        crearPiezas(ColorPieza.BLANCO, 7, 6);
        calcularCuadricula();
    }

    private void crearPiezas(ColorPieza colorPieza, int filaInicial, int filaPeones) {
        piezas.add(new Torre(this, colorPieza, 0, filaInicial));
        piezas.add(new Caballo(this, colorPieza, 1, filaInicial));
        piezas.add(new Alfil(this, colorPieza, 2, filaInicial));
        piezas.add(new Reina(this, colorPieza, 3, filaInicial));
        piezas.add(new Rey(this, colorPieza, 4, filaInicial));
        piezas.add(new Alfil(this, colorPieza, 5, filaInicial));
        piezas.add(new Caballo(this, colorPieza, 6, filaInicial));
        piezas.add(new Torre(this, colorPieza, 7, filaInicial));
        for(int i=0; i<8; i++) {
            piezas.add(new Peon(this, colorPieza, i, filaPeones));
        }
    }

    private void clearCuadricula() {
        for(int col=0; col < Tablero.COLUMNAS; col++) {
            for(int fila=0; fila < Tablero.FILAS; fila++) {
                cuadricula[col][fila] = null;
            }
        }
    }

    public void calcularCuadricula() {
        for(int col=0; col < Tablero.COLUMNAS; col++) {
            for(int fila=0; fila < Tablero.FILAS; fila++) {
                cuadricula[col][fila] = null;
                for(Pieza pieza: piezas) {
                    if(pieza.enPosicion(col, fila)) {
                        cuadricula[col][fila] = pieza;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Determina el color de la casilla indicada
     * @param col
     * @param fila
     * @return
     */
    public ColorPieza getColorCasilla(int col, int fila) {
        if(fila % 2 == 0) {
            return col % 2  == 0 ? ColorPieza.BLANCO : ColorPieza.NEGRO;
        } else {
            return col % 2  != 0 ? ColorPieza.BLANCO : ColorPieza.NEGRO;
        }
    }

    /**
     * Obtiene la imagen del tablero en el estado actual
     * @return
     */
    public Image getImagen() {
        Image image = new Image(display, imageTablero, SWT.IMAGE_COPY);
        GC gc = new GC(image);
        // piezas
        for(Pieza pieza: piezas) {
            Image imagePieza = pieza.getImagen();
            gc.drawImage(imagePieza, pieza.getColumna()*CASILLA + BORDE_TABLERO, pieza.getFila()*CASILLA + BORDE_TABLERO);
        }
        // pieza seleccionada
        if(seleccion) {
            gc.drawRectangle(BORDE_TABLERO+columnaSeleccionada*CASILLA, BORDE_TABLERO+filaSeleccionada*CASILLA,
                    CASILLA, CASILLA);
            gc.setForeground(SWTResourceManager.getColor(231, 76, 60));
            gc.setLineWidth(5);
            gc.drawRectangle (BORDE_TABLERO+columnaSeleccionada*CASILLA, BORDE_TABLERO+filaSeleccionada*CASILLA,
                            CASILLA, CASILLA);
            gc.setForeground(SWTResourceManager.getColor(88, 214, 141));
            gc.setLineWidth(3);
            Pieza pieza = this.getPieza(columnaSeleccionada, filaSeleccionada);
            if(pieza != null) {
                for (Posicion p : pieza.getMovimientosPosibles()) {
                    gc.drawOval(BORDE_TABLERO + p.columna * CASILLA + CASILLA / 4, BORDE_TABLERO + p.fila * CASILLA + CASILLA / 4,
                            CASILLA / 2, CASILLA / 2);
                }
            }
        }
        gc.dispose ();
        return image;
    }

    /**
     * Obtiene la imagen del tablero sin estado y excluyendo la pieza indicada
     * Usada para hacer las animaciones de las piezas.
     * @param excludePieza
     * @return
     */
    public Image getImagen(Pieza excludePieza) {
        Image image = new Image(display, imageTablero, SWT.IMAGE_COPY);
        GC gc = new GC(image);
        for(Pieza pieza: piezas) {
            if(excludePieza != null && !pieza.equals(excludePieza)) {
                Image imagePieza = pieza.getImagen();
                gc.drawImage(imagePieza, pieza.getColumna() * CASILLA + BORDE_TABLERO, pieza.getFila() * CASILLA + BORDE_TABLERO);
            }
        }
        gc.dispose ();
        return image;
    }

    /**
     * Retorna la pieza de la posición indicada,
     * null si no hay ninguna
     * @param columna
     * @param fila
     * @return
     */
    public Pieza getPieza(int columna, int fila) {
//        for(Pieza pieza: piezas) {
//            if(pieza.enPosicion(columna, fila)) {
//                return pieza;
//            }
//        }
//        return null;
        return cuadricula[columna][fila];
    }

    /**
     * Activa la posición indicada como seleccionada
     * @param columna
     * @param fila
     */
    public void seleccionar(int columna, int fila) {
        this.columnaSeleccionada = columna;
        this.filaSeleccionada = fila;
        this.seleccion = true;
    }

    /**
     * Desactiva la selección
     */
    public void deseleccionar() {
        this.columnaSeleccionada = -1;
        this.filaSeleccionada = -1;
        this.seleccion = false;
    }

    /**
     * Retorna la pieza seleccionada
     * @param pieza
     * @return
     */
    public boolean piezaSeleccionada(Pieza pieza) {
        return pieza.getColumna() == this.columnaSeleccionada && pieza.getFila() == this.filaSeleccionada;
    }

    /**
     * Elimina la pieza indicada del tablero
     * @param pieza
     * @return
     */
    public boolean eliminarPieza(Pieza pieza) {
        cuadricula[pieza.getColumna()][pieza.getFila()] = null;
        return piezas.remove(pieza);
    }

    /**
     * Retorna una copia identica del tablero
     * @return
     */
    public Tablero copia() {
        Tablero copia = new Tablero();
        copia.piezas.clear();
        for(Pieza pieza: piezas) {
            Pieza clon = (Pieza) pieza.clone();
            clon.setTablero(copia);
            copia.piezas.add(clon);
        }
        copia.calcularCuadricula();
        return copia;
    }

    /**
     * Retorna las piezas del color indicado
     * @param colorPieza
     * @return
     */
    public List<Pieza> getPiezas(ColorPieza colorPieza) {
        List<Pieza> result = new ArrayList<>();
        for(Pieza pieza: piezas) {
            if(pieza.getColorPieza().equals(colorPieza)) {
                result.add(pieza);
            }
        }
        return result;
    }

    /**
     * Retorna el rey del color indicado
     * @param colorPieza
     * @return
     */
    private Rey getRey(ColorPieza colorPieza) {
        for(Pieza pieza: piezas) {
            if(pieza.getColorPieza().equals(colorPieza) && TipoPieza.REY.equals(pieza.getTipoPieza())) {
                return (Rey) pieza;
            }
        }
        return null;
    }

    /**
     * Determina si en la situación actual, el rey queda en jaque
     * @param colorPieza turno actual
     * @return
     */
    public boolean esJaque(ColorPieza colorPieza) {
        Rey rey = getRey(colorPieza);
        List<Pieza> list = getPiezas(colorPieza.contraria());
        for(Pieza p: list) {
            if(p.puedeMoverA(rey.getColumna(), rey.getFila())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determina si la situación actual es de jaque mate
     * @param colorPieza turno actual
     * @return
     */
    public boolean esJaqueMate(ColorPieza colorPieza) {
        Tablero copia = copia();
        List<Pieza> listPiezas = copia.getPiezas(colorPieza);
        for(Pieza p: listPiezas) {
            for(Posicion movimiento: p.getMovimientosPosibles()) {
                int oldX = p.fila;
                int oldY = p.columna;
                p.moverA(movimiento.getColumna(), movimiento.getFila());
                if(!copia.esJaque(colorPieza)) {
                    return false;
                }
                p.moverA(oldY, oldX);
                p.setMovimientos(0);
            }
        }
        return true;
    }

    /**
     * Determina si el turno siguiente no puede mover
     * @param colorPieza turno siguiente
     * @return
     */
    public boolean esTablas(ColorPieza colorPieza) {
        List<Pieza> contrarios = getPiezas(colorPieza.contraria());
        for(Pieza contrario: contrarios) {
            if(contrario.puedeMover()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determina el valor de la situación actual de
     * las posiciones de las piezas
     *
     * @param colorTurno
     * @return
     */
    public int getEvaluacion(ColorPieza colorTurno) {
        if(esJaqueMate(colorTurno)) {
            return -VALOR_JAQUE_MATE;
        } else if(esJaque(colorTurno)) {
            return -VALOR_JAQUE;
        } else if(esJaqueMate(colorTurno.contraria())) {
            return VALOR_JAQUE_MATE;
        } else if(esJaque(colorTurno.contraria())) {
            return VALOR_JAQUE;
        }
        int valorTurno = 0;
        int valorContrario = 0;
        for(Pieza p: piezas) {
            if(colorTurno.equals(p.colorPieza)) {
                valorTurno += p.getValor();
            } else {
                valorContrario += p.getValor();
            }
        }
        return valorTurno - valorContrario;
    }

    public int getEvaluacionTurno(ColorPieza colorTurno) {
        int valorTurno = 0;
        for(Pieza p: piezas) {
            if(colorTurno.equals(p.colorPieza)) {
                valorTurno += p.getValor();
            }
        }
        return valorTurno;
    }


    /**
     * Reinicia el tablero para una nueva partida
     */
    public void reiniciar() {
        piezas.clear();
        clearCuadricula();
        crearPiezas();
        columnaSeleccionada=-1;
        filaSeleccionada=-1;
        seleccion = false;
    }

}
