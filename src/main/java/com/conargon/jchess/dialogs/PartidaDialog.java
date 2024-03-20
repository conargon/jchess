package com.conargon.jchess.dialogs;

import com.conargon.jchess.modelo.Opciones;
import com.conargon.jchess.tipo.ColorPieza;
import com.conargon.jchess.tipo.Dificultad;
import com.conargon.jchess.tipo.ModoJuego;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;

import java.awt.*;

public class PartidaDialog extends Dialog {

    private Shell dialogShell;
    private int modalResult = SWT.CANCEL;
    private Button btnOk;
    private Button btnDosJugadores;
    private Button btnContraMaquina;
    private Button btnBlancas;
    private Button btnNegras;
    private Button btnFacil;
    private Button btnMedio;
    private Button btnDificil;
    private Opciones opciones;

    public PartidaDialog(Shell parent) {
        super(parent);
    }

    private Opciones setOpciones() {
        ModoJuego modoJuego = btnDosJugadores.getSelection() ? ModoJuego.HUMANO : ModoJuego.MAQUINA;
        ColorPieza colorPieza = btnNegras.getSelection() ? ColorPieza.BLANCO : ColorPieza.NEGRO ;
        Dificultad dificultad = btnFacil.getSelection() ? Dificultad.FACIL : (btnMedio.getSelection() ? Dificultad.MEDIO : Dificultad.MEDIO);
        return new Opciones(modoJuego, colorPieza, dificultad);
    }

    public int open() {
        try {
            dialogShell = new Shell(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

            dialogShell.setText("Nueva partida");

            GridLayout dialogShellLayout = new GridLayout();
            dialogShellLayout.numColumns = 1;
            dialogShell.setLayout(dialogShellLayout);
            dialogShell.layout();
            dialogShell.pack();
            dialogShell.setSize(240, 320);

            Group group = new Group(dialogShell, SWT.NONE);
            group.setText("Opciones");
            GridLayout compositeLayout = new GridLayout();
            compositeLayout.makeColumnsEqualWidth = false;
            compositeLayout.numColumns = 1; 		//Este me marca la posicion de los botones
            GridData compositeLData = new GridData();
            compositeLData.horizontalAlignment = GridData.FILL;
            compositeLData.grabExcessVerticalSpace = true;
            compositeLData.verticalAlignment = GridData.FILL;
            compositeLData.grabExcessHorizontalSpace=true;
            group.setLayoutData(compositeLData);
            group.setLayout(compositeLayout);

            Group groupJugadores = new Group(group, SWT.NONE);
            groupJugadores.setText("Jugadores");
            GridData compositeLData2 = new GridData();
            compositeLData2.horizontalAlignment = GridData.FILL;
            compositeLData2.grabExcessVerticalSpace = true;
            compositeLData2.verticalAlignment = GridData.FILL;
            compositeLData2.grabExcessHorizontalSpace=true;
            groupJugadores.setLayoutData(compositeLData2);
            groupJugadores.setLayout(new GridLayout());

            btnDosJugadores = new Button (groupJugadores, SWT.RADIO);
            btnDosJugadores.setText ("Dos jugadores");
            btnDosJugadores.setSelection (false);

            btnContraMaquina = new Button (groupJugadores, SWT.RADIO);
            btnContraMaquina.setText ("Contra la máquina");
            btnContraMaquina.setSelection (true);

            Group groupColor = new Group(group, SWT.NONE);
            groupColor.setText("Color");
            GridData compositeLData3 = new GridData();
            compositeLData3.horizontalAlignment = GridData.FILL;
            compositeLData3.grabExcessVerticalSpace = true;
            compositeLData3.verticalAlignment = GridData.FILL;
            compositeLData3.grabExcessHorizontalSpace=true;
            groupColor.setLayoutData(compositeLData3);
            groupColor.setLayout(new GridLayout());

            btnBlancas = new Button (groupColor, SWT.RADIO);
            btnBlancas.setText ("Blancas");
            btnBlancas.setSelection (true);

            btnNegras = new Button (groupColor, SWT.RADIO);
            btnNegras.setText ("Negras");
            btnNegras.setSelection (false);

            Group groupNivel = new Group(group, SWT.NONE);
            groupNivel.setText("Nivel");
            GridData compositeLData4 = new GridData();
            compositeLData4.horizontalAlignment = GridData.FILL;
            compositeLData4.grabExcessVerticalSpace = true;
            compositeLData4.verticalAlignment = GridData.FILL;
            compositeLData4.grabExcessHorizontalSpace=true;
            groupNivel.setLayoutData(compositeLData4);
            groupNivel.setLayout(new GridLayout());

            btnFacil = new Button (groupNivel, SWT.RADIO);
            btnFacil.setText ("Fácil");
            btnFacil.setSelection (true);

            btnMedio = new Button (groupNivel, SWT.RADIO);
            btnMedio.setText ("Medio");
            btnMedio.setSelection (false);

            btnDificil = new Button (groupNivel, SWT.RADIO);
            btnDificil.setText ("Díficil");
            btnDificil.setSelection (false);

            // COMPOSITE PARA LA BOTONERA

            GridLayout compositeBtnLayout = new GridLayout();
            compositeBtnLayout.numColumns = 2;
            compositeBtnLayout.makeColumnsEqualWidth=true;

            GridData compositeBtnLImage_2 = new GridData();
            compositeBtnLImage_2.horizontalAlignment = GridData.FILL;
            compositeBtnLImage_2.grabExcessHorizontalSpace = true;

            Composite compositeImg_2 = new Composite(dialogShell, SWT.NONE);
            compositeImg_2.setLayout(compositeBtnLayout);
            compositeImg_2.setLayoutData(compositeBtnLImage_2);

            {
                btnOk = new Button(compositeImg_2, SWT.PUSH | SWT.CENTER);
                dialogShell.setDefaultButton(btnOk);
                GridData btnOkLData = new GridData();
                btnOkLData.horizontalAlignment = GridData.END;
                btnOkLData.widthHint = 100;
                btnOkLData.heightHint = 23;
                btnOkLData.grabExcessHorizontalSpace = true;
                btnOk.setLayoutData(btnOkLData);
                btnOk.setText("Aceptar");
                btnOk.setSize(100, 23);
                // evento click del botón OK
                btnOk.addSelectionListener(okSelectionAdapter);
            }

            {
                Button btnCancel = new Button(compositeImg_2, SWT.PUSH | SWT.CENTER);
                GridData btnCancelLData = new GridData();
                btnCancelLData.widthHint = 100;
                btnCancelLData.heightHint = 23;
                btnCancel.setLayoutData(btnCancelLData);
                btnCancel.setText("Cancelar");
                btnCancel.setSize(100, 23);
                // evento click del botón cancelar
                btnCancel.addSelectionListener(new SelectionAdapter() {
                    public void widgetSelected(SelectionEvent e) {
                        modalResult = SWT.CANCEL;
                        dialogShell.close ();
                    }
                });
            }

            // allow escape to close a shell
            dialogShell.addListener (SWT.Traverse, new Listener () {
                public void handleEvent (Event event) {
                    switch (event.detail) {
                        case SWT.TRAVERSE_ESCAPE:
                            modalResult = SWT.CANCEL;
                            dialogShell.close ();
                            event.detail = SWT.TRAVERSE_NONE;
                            event.doit = false;
                            break;
                    }
                }
            });

            dialogShell.open();
            Display display = dialogShell.getDisplay();
            while (!dialogShell.isDisposed()) {
                if (!display.readAndDispatch())
                    display.sleep();
            }
            return modalResult;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return modalResult;
    }

    SelectionAdapter okSelectionAdapter = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) {
            opciones = setOpciones();
            modalResult = SWT.OK;
            dialogShell.close ();
        }
    };

    public Opciones getOpciones() {
        return opciones;
    }

}
