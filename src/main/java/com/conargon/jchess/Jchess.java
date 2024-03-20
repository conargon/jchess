package com.conargon.jchess;

import com.conargon.jchess.dialogs.PartidaDialog;
import com.conargon.jchess.modelo.*;
import com.conargon.jchess.tipo.ColorPieza;
import com.conargon.jchess.tipo.ModoJuego;
import com.conargon.jchess.swt.SWTResourceManager;
import com.conargon.jchess.tipo.Estado;
import com.conargon.jchess.util.IntegerWrapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class Jchess {

    private static final boolean DEBUG = false;
    private static final int WIDTH = 800;
    private static final int HEIGTH = 546;
    public static final int TABLERO_X = 15;
    public static final int TABLERO_Y = 15;

    private static Shell shell;
    private static Label labelTurno;
    private static Label labelMensaje;
    private static Tablero tablero;
    private static Canvas canvas;
    private static ColorPieza turno = ColorPieza.BLANCO;
    private static Estado estado = Estado.NINGUNO;
    private static Pieza piezaActual;
    private static Text historico;
    private static ModoJuego modoJuego = ModoJuego.MAQUINA;
    private static ColorPieza colorJugador = ColorPieza.BLANCO;
    private static ColorPieza colorAdversario = ColorPieza.NEGRO;
    private static SeleccionarPiezaMouseListener mouseListener;
    private static Display display;

    private static PaintListener normalPaintListener = new PaintListener() {
        @Override
        public void paintControl(PaintEvent e) {
            e.gc.drawImage (tablero.getImagen(), TABLERO_X, TABLERO_Y);
        }
    };


    public static void main(String[] args) {
        display = new Display();

        shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE));
        shell.setText("J-Ajedrez (c) Constantino Argüello González 2020");
        shell.setSize (WIDTH, HEIGTH);
        shell.setMinimumSize(WIDTH,HEIGTH);


        // layout principal
        GridLayout gridLayoutShell = new GridLayout();
        gridLayoutShell.verticalSpacing = 0;
        gridLayoutShell.horizontalSpacing = 0;
        gridLayoutShell.verticalSpacing = 0;
        gridLayoutShell.marginBottom =0 ;
        gridLayoutShell.marginTop = 0;
        gridLayoutShell.marginRight = 0;
        gridLayoutShell.marginLeft = 0;
        gridLayoutShell.marginHeight=0;
        gridLayoutShell.marginWidth=0;

        shell.setLayout(gridLayoutShell);

        // layout composite
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = false;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginBottom =0 ;
        gridLayout.marginTop = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginHeight=0;
        gridLayout.marginWidth=0;

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL_HORIZONTAL;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;

        Composite shellComposite = new Composite(shell, SWT.NONE);
        shellComposite.setLayout(gridLayout);
        shellComposite.setLayoutData(gridData);

        crearMenu();

        crearTablero(shellComposite);

        crearPanelInfo(shellComposite);

        crearStatusBar(shellComposite);

        valoresInicio();

        mostrarEstado();

        mostrarTurno();

        shell.pack();
        shell.open();

        while (!shell.isDisposed()) {

            if(Estado.JAQUE_MATE == estado || Estado.TABLAS == estado) {
                canvas.removeMouseListener(mouseListener);
            }

            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        SWTResourceManager.dispose();
        display.dispose();
    }

    /**
     * Restaura los valores iniciales para una nueva partida
     */
    private static void valoresInicio() {
        turno = ColorPieza.BLANCO;
        estado = Estado.SELECCIONAR_ORIGEN;
        historico.setText("");
        mouseListener = new SeleccionarPiezaMouseListener();
        canvas.addMouseListener(mouseListener);
    }

    /**
     * Genera el movimiento de la máquina
     */
    private static void movimientoMaquina() {
        Posicion posicionOrigen = null;
        Posicion posicionDestino = null;
        int valoracion = 0;
        Tablero copia = tablero.copia();
        if(DEBUG) {
            historizar("---------------");
        }
        java.util.List<Pieza> clones = copia.getPiezas(colorAdversario);
        for(Pieza clon : clones) {
            if(DEBUG) {
                historizar("Movimientos de " + clon);
            }
            Posicion oldPos = new Posicion(clon.getColumna(), clon.getFila());
            java.util.List<Posicion> movimientosPosibles = clon.getMovimientosPosibles();
            for(Posicion p: movimientosPosibles) {
                if(!clon.poneEnJaque(p)) {
                    clon.moverA(p);
                    int v = copia.getEvaluacion(clon.getColorPieza());
                    if (DEBUG) {
                        int v1 = copia.getEvaluacionTurno(clon.getColorPieza());
                        int v2 = copia.getEvaluacionTurno(clon.getColorPieza().contraria());
                        historizar(" -> " + p + " = " + v + " " + v1 + " " + v2);
                    }
                    if (v > valoracion || posicionDestino == null) {
                        valoracion = v;
                        posicionOrigen = new Posicion(oldPos);
                        posicionDestino = new Posicion(p);
                    }
                    clon.moverA(oldPos);
                    clon.setMovimientos(0);
                }
            }
        }

        if(DEBUG) {
            historizar("Elección: " + posicionOrigen + " " + posicionDestino);
            historizar("---------------");
        }
        //
        if(posicionOrigen != null &&  posicionDestino != null) {
            Pieza pieza = tablero.getPieza(posicionOrigen.getColumna(), posicionOrigen.getFila());
            // animación del movimiento
            animarPieza(pieza,  posicionDestino.getColumna(), posicionDestino.getFila());
        } else if (estado.equals(Estado.JAQUE)) {
            // si no hay sitios donde mover y estamos en jaque, entonces jaque mate
            cambiarEstado(Estado.JAQUE_MATE);
        }
    }

    /**
     * Controlador para eventos de ratón
     */
    private static class SeleccionarPiezaMouseListener implements MouseListener {
        @Override
        public void mouseDoubleClick(MouseEvent mouseEvent) {}
        @Override
        public void mouseDown(MouseEvent mouseEvent) {
            if(modoJuego.equals(ModoJuego.MAQUINA) && turno.equals(colorAdversario)) {
                return;
            }
            if(tablero != null) {
                int col = getColumna(mouseEvent.x);
                int fila = getFila(mouseEvent.y);
                // si hemos hecho clic en el tablero
                if(col > -1 && fila > -1) {
                    switch(estado) {
                        case NINGUNO:
                            break;
                        case SELECCIONAR_ORIGEN:
                        case JAQUE:
                            seleccionarOrigen(col, fila);
                            break;
                        case SELECCIONAR_DESTINO:
                            seleccionarDestino(col, fila);
                            break;
                    }

                } else {
                    tablero.deseleccionar();
                }
                canvas.redraw();
            }
        }
        @Override
        public void mouseUp(MouseEvent mouseEvent) {}
    }

    /**
     * El usuario selecciona una pieza
     * @param col
     * @param fila
     */
    private static void seleccionarOrigen(int col, int fila) {
        Pieza p = tablero.getPieza(col, fila);
        if(p != null && p.getColorPieza().equals(turno) && !tablero.piezaSeleccionada(p)) {
            piezaActual = p;
            tablero.seleccionar(col, fila);
            cambiarEstado(Estado.SELECCIONAR_DESTINO);
        } else {
            tablero.deseleccionar();
        }
    }

    /**
     * Mueve la pieza seleccionada al destino indicado siempre que sea posible
     * @param col
     * @param fila
     */
    private static void seleccionarDestino(int col, int fila) {

        // vemos si hay una pieza en el destino
        Pieza p = tablero.getPieza(col, fila);

        if(p != null && p.getColorPieza().equals(turno)) {

            // se ha seleccionado otra pieza del mismo color
            seleccionarOrigen(col, fila);

        } else if(piezaActual.poneEnJaque(col, fila)) {

            // no se puede permitir el movimiento en jaque
            showMessageBox("No permitido: el movimiento pone al rey en jaque.");
            cambiarEstado(Estado.SELECCIONAR_ORIGEN);

        } else if(piezaActual.puedeMoverA(col, fila)) {

            // movemos la pieza al destino
            animarPieza(piezaActual, col, fila);
        }
    }

    /**
     * Animación del movimiento de una pieza
     * @param pieza
     * @param col
     * @param fila
     */
    private static void animarPieza(Pieza pieza, int col, int fila) {

        final int TIMER_INTERVAL = 1;
        final int max=Math.max(Math.abs(pieza.getColumna() - col), Math.abs(pieza.getFila() - fila)) * Tablero.CASILLA;
        final IntegerWrapper i = new IntegerWrapper(0);
        final IntegerWrapper j = new IntegerWrapper(0);
        final IntegerWrapper count = new IntegerWrapper(0);
        final IntegerWrapper deltaColumna = new IntegerWrapper(0);
        final IntegerWrapper deltaFila = new IntegerWrapper(0);
        final boolean esCaballo = TipoPieza.CABALLO.equals(pieza.getTipoPieza());
        final boolean dosColumnasCaballo = esCaballo && Math.abs(col - pieza.getColumna()) == 2;

        // eliminamos el dibujado actual
        canvas.removePaintListener(normalPaintListener);

        if(pieza.getColumna() == col) {
            deltaColumna.setValue(0);
        } else {
            deltaColumna.setValue(pieza.getColumna() > col ? -1 : 1);
        }

        if(pieza.getFila() == fila) {
            deltaFila.setValue(0);
        } else {
            deltaFila.setValue(pieza.getFila() > fila ? -1 : 1);
        }


        final Image imagePieza = pieza.getImagen();
        final Image imageTablero = tablero.getImagen(pieza);

        PaintListener animarPiezaPaintListener = new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                e.gc.drawImage (imageTablero, Jchess.TABLERO_X, Jchess.TABLERO_Y);
                e.gc.drawImage(imagePieza,
                        Jchess.TABLERO_X+pieza.getColumna()*Tablero.CASILLA + Tablero.BORDE_TABLERO+i.getValue(),
                        Jchess.TABLERO_Y+pieza.getFila()*Tablero.CASILLA + Tablero.BORDE_TABLERO+j.getValue());
            }
        };

        canvas.addPaintListener(animarPiezaPaintListener);

        Runnable runnable = new Runnable() {
            public void run() {
                if(count.getValue() < max) {
                    if(esCaballo && count.getValue() > Tablero.CASILLA) {
                        if(dosColumnasCaballo) {
                            i.inc(deltaColumna);
                        } else {
                            j.inc(deltaFila);
                        }
                    } else {
                        i.inc(deltaColumna);
                        j.inc(deltaFila);
                    }
                    count.inc();
                    canvas.redraw();
                    display.timerExec(TIMER_INTERVAL, this);
                } else {
                    // fin
                    display.timerExec(-1, this);
                    canvas.removePaintListener(animarPiezaPaintListener);
                    canvas.addPaintListener(normalPaintListener);
                    imageTablero.dispose();
                    moverPieza(pieza, col, fila);
                }
            }
        };

        display.timerExec(TIMER_INTERVAL, runnable);
    }

    /**
     * Efectua el movimiento de una pieza
     * @param pieza
     * @param col
     * @param fila
     */
    private static void moverPieza(Pieza pieza, int col, int fila) {

        historizar(pieza+" a " + new Posicion(col, fila));

        // actualizamos la posición
        pieza.moverA(col, fila);

        // movemos la pieza y quitamos la selección
        tablero.deseleccionar();

        // comprobamos jaque
        if(tablero.esJaque(turno.contraria())) {
            if(tablero.esJaqueMate(turno.contraria())) {
                cambiarEstado(Estado.JAQUE_MATE);
                return;
            } else {
                cambiarEstado(Estado.JAQUE);
            }
        } // comprobamos tablas
        else if(tablero.esTablas(turno.contraria())) {
            cambiarEstado(Estado.TABLAS);
            return;
        } else {
            cambiarEstado(Estado.SELECCIONAR_ORIGEN);
        }

        // cambio de turno
        turno = turno.contraria();
        mostrarTurno();

        canvas.redraw();

        if(modoJuego.equals(ModoJuego.MAQUINA) && turno.equals(colorAdversario)) {
            movimientoMaquina();
        }

    }

    /**
     * Retorna la fila del tablero correspondiente al punto X de la pantalla
     * @param p
     * @return
     */
    private static int getColumna(int p) {
        if(p > TABLERO_X + Tablero.BORDE_TABLERO && p < TABLERO_X + Tablero.DIM_TABLERO + Tablero.BORDE_TABLERO) {
            return (p - (TABLERO_X  + Tablero.BORDE_TABLERO)) / Tablero.CASILLA;
        } else {
            return -1;
        }
    }

    /**
     * Retorna la columna del tablero correspondiente al punto Y de la pantalla
     * @param p
     * @return
     */
    private static int getFila(int p) {
        if(p > TABLERO_Y + Tablero.BORDE_TABLERO && p < TABLERO_Y + Tablero.DIM_TABLERO + Tablero.BORDE_TABLERO) {
            return (p - (TABLERO_Y  + Tablero.BORDE_TABLERO)) / Tablero.CASILLA;
        } else {
            return -1;
        }
    }

    private static void mostrarEstado() {
        switch (estado) {
            case NINGUNO:
                labelMensaje.setText("");
                break;
            case SELECCIONAR_ORIGEN:
                labelMensaje.setText("Seleccione una pieza");
                break;
            case SELECCIONAR_DESTINO:
                labelMensaje.setText("Seleccione la casilla destino de " + piezaActual);
                break;
            case JAQUE:
                labelMensaje.setText("JAQUE AL REY");
                historizar("JAQUE AL REY");
                break;
            case JAQUE_MATE:
                labelMensaje.setText("JAQUE MATE");
                historizar("JAQUE MATE");
                break;
            case TABLAS:
                Display.getCurrent().beep();
                labelMensaje.setText("TABLAS");
                historizar("TABLAS");
                break;
        }
    }

    private static void mostrarAyuda(String mensaje) {
        labelMensaje.setText(" " + mensaje);
    }

    private static void mostrarTurno() {
        labelTurno.setText(" Turno: " + turno.nombre() + " ");
    }

    private static void cambiarEstado(Estado _estado) {
        estado = _estado;
        mostrarEstado();
    }

    private static void historizar(String mensaje) {
        historico.insert(mensaje + "\n");
        // position the cursor after the last character
        historico.setSelection(historico.getCharCount());
    }

    private static void showMessageBox(String mensaje) {
        int style = SWT.ICON_INFORMATION | SWT.OK;
        MessageBox dia = new MessageBox(shell, style);
        dia.setText("Información");
        dia.setMessage(mensaje);
        dia.open();
    }

    private static void crearPanelInfo(Composite owner) {
        Composite composite = new Composite(owner, SWT.NONE);

        GridLayout gridLayoutComposite = new GridLayout();
        gridLayoutComposite.numColumns = 1;
        gridLayoutComposite.horizontalSpacing = 0;
        gridLayoutComposite.verticalSpacing = 0;
        gridLayoutComposite.makeColumnsEqualWidth = false;
        gridLayoutComposite.marginHeight = 0;
        gridLayoutComposite.marginWidth = 0;
        composite.setLayout(gridLayoutComposite);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalIndent = TABLERO_Y;
        gridData.horizontalIndent = TABLERO_X;
        composite.setLayoutData(gridData);

//        Canvas banner = new Canvas(composite, SWT.NONE);
//
//        GridData gridDataBanner = new GridData();
//        gridDataBanner.horizontalAlignment = GridData.FILL;
//        gridDataBanner.grabExcessHorizontalSpace = true;
//        gridDataBanner.verticalIndent = 0;
//        gridDataBanner.horizontalIndent = 0;
//        gridDataBanner.heightHint=20;
//        banner.setLayoutData(gridDataBanner);
//
//        banner.addListener (SWT.Paint, e -> {
//            Rectangle rect = banner.getClientArea();
//            Image newImage = new Image(display, Math.max(1, rect.width), 1);
//            GC gc = new GC(newImage);
//            gc.setForeground(SWTResourceManager.getColor(85, 85, 85));
//            gc.setBackground(SWTResourceManager.getColor(231, 201, 138));
//            gc.fillGradientRectangle(rect.x, rect.y, rect.width, 1, false);
//            gc.dispose();
//            banner.setBackgroundImage(newImage);
//            if (oldImage != null){
//                oldImage.dispose();
//            }
//            oldImage = newImage;
//        });

        historico = new Text (composite, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
        GridData gridDataHistorico = new GridData();
        gridDataHistorico.verticalAlignment = GridData.FILL;
        gridDataHistorico.horizontalAlignment = GridData.FILL;
        gridDataHistorico.grabExcessVerticalSpace = true;
        gridDataHistorico.grabExcessHorizontalSpace = true;
        gridDataHistorico.verticalIndent = 0;
        gridDataHistorico.horizontalIndent = 0;
        historico.setLayoutData(gridDataHistorico);
    }


    private static void crearTablero(Composite owner) {
        tablero = new Tablero();
        canvas = new Canvas (owner, SWT.DOUBLE_BUFFERED);
        canvas.addPaintListener (normalPaintListener);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        gridData.minimumWidth = Tablero.TOTAL_DIM_TABLERO+TABLERO_X;
        gridData.minimumHeight = Tablero.TOTAL_DIM_TABLERO+TABLERO_Y;
        gridData.widthHint = Tablero.TOTAL_DIM_TABLERO+TABLERO_X;
        gridData.heightHint = Tablero.TOTAL_DIM_TABLERO+TABLERO_Y;
        canvas.setLayoutData(gridData);
    }

    private static void crearStatusBar(Composite owner) {

        Composite composite = new Composite(owner, SWT.NONE);

        GridLayout gridLayoutComposite = new GridLayout();
        gridLayoutComposite.numColumns = 2;
        gridLayoutComposite.horizontalSpacing = 2;
        gridLayoutComposite.verticalSpacing = 0;
        gridLayoutComposite.makeColumnsEqualWidth = false;
        gridLayoutComposite.marginTop = 10;
        gridLayoutComposite.marginBottom = 0;
        gridLayoutComposite.marginLeft = TABLERO_X;
        gridLayoutComposite.marginRight = 0;
        gridLayoutComposite.marginWidth = 0;
        gridLayoutComposite.marginHeight = 0;
        composite.setLayout(gridLayoutComposite);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL_HORIZONTAL;
        gridData.verticalAlignment = GridData.FILL_VERTICAL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = false;
        gridData.horizontalSpan = 2;
        gridData.widthHint = WIDTH - TABLERO_X;
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 0;
        composite.setLayoutData(gridData);

        labelMensaje = new Label(composite, SWT.BORDER);

        GridData gridDataLabelMensaje = new GridData();
        gridDataLabelMensaje.horizontalAlignment = GridData.FILL;
        gridDataLabelMensaje.grabExcessHorizontalSpace = true;
        gridDataLabelMensaje.horizontalIndent = 0;

        labelMensaje.setLayoutData(gridDataLabelMensaje);

        labelTurno = new Label(composite, SWT.BORDER);

        GridData gridDataLabelTurno = new GridData();
        gridDataLabelTurno.horizontalAlignment = GridData.FILL;
        gridDataLabelTurno.grabExcessHorizontalSpace = false;
        gridDataLabelTurno.horizontalIndent = 0;

        labelTurno.setLayoutData(gridDataLabelTurno);
    }

    private static void crearMenu() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);

        MenuItem fileItem = new MenuItem(menuBar, SWT.CASCADE);
        fileItem.setText("Archivo");
        fileItem.setMenu(fileMenu);

        MenuItem nuevaPartidaItem = new MenuItem(fileMenu, SWT.PUSH);
        nuevaPartidaItem.setText("Nueva partida");
        nuevaPartidaItem.addListener (SWT.Selection, e -> {
            PartidaDialog pd = new PartidaDialog(shell);
            if(pd.open() == SWT.OK) {
                tablero.reiniciar();
                canvas.redraw();
                valoresInicio();
            }
        });

        MenuItem separatorItem = new MenuItem(fileMenu, SWT.SEPARATOR);

        MenuItem salirPartidaItem = new MenuItem(fileMenu, SWT.PUSH);
        salirPartidaItem.setText("Salir");
        salirPartidaItem.addListener (SWT.Selection, e -> {
            SWTResourceManager.dispose();
            shell.dispose();
            System.exit(0);
        });

        //        Button btnDebug = new Button(composite, SWT.PUSH);
//        btnDebug.setText ("Debug");
//        btnDebug.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                if(piezaActual == null) {
//                    return;
//                }
//                Tablero copia = tablero.copia();
//                Pieza clon = copia.getPieza(piezaActual.getColumna(), piezaActual.getFila());
//                java.util.List<Posicion> listaMovimientos = clon.getMovimientosPosibles();
//                for(Posicion p: listaMovimientos) {
//                    int oldFila = p.getFila();
//                    int oldCol = p.getColumna();
//                    clon.moverA(p);
//                    int v = copia.getEvaluacion(piezaActual.getColorPieza());
//                    historizar(clon+" valoracion="+v);
//                    clon.moverA(oldCol, oldFila);
//                }
//            }
//        });

        Menu debugMenu = new Menu(shell, SWT.DROP_DOWN);

        MenuItem verItem = new MenuItem(menuBar, SWT.CASCADE);
        verItem.setText("Ver");
        verItem.setMenu(debugMenu);

        MenuItem debugItem = new MenuItem(debugMenu, SWT.PUSH);
        debugItem.setText("Debug");
        debugItem.addListener (SWT.Selection, e -> {
                if(piezaActual == null) {
                    return;
                }
                Tablero copia = tablero.copia();
                Pieza clon = copia.getPieza(piezaActual.getColumna(), piezaActual.getFila());
                java.util.List<Posicion> listaMovimientos = clon.getMovimientosPosibles();
                for(Posicion p: listaMovimientos) {
                    int oldFila = p.getFila();
                    int oldCol = p.getColumna();
                    clon.moverA(p);
                    int v = copia.getEvaluacion(piezaActual.getColorPieza());
                    historizar(clon+" valoracion="+v);
                    clon.moverA(oldCol, oldFila);
                }
        });

        Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);

        MenuItem helpItem = new MenuItem(menuBar, SWT.CASCADE);
        helpItem.setText("Ayuda");
        helpItem.setMenu(helpMenu);

        MenuItem acercaDeItem = new MenuItem(helpMenu, SWT.PUSH);
        acercaDeItem.setText("Acerca de...");
        acercaDeItem.addListener (SWT.Selection, e -> {
            showMessageBox("J-Ajedrez \n(c) Constantino Argüello González 2020");
        });

    }
}
