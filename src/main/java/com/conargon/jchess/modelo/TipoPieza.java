package com.conargon.jchess.modelo;

import com.conargon.jchess.tipo.ColorPieza;

import static com.conargon.jchess.tipo.ColorPieza.BLANCO;

public enum TipoPieza implements ITipoPieza {
    REY {
        @Override
        public String nombre() {
            return "Rey";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/reyb.png" : "img/reyn.png";
        }
    },
    REINA {
        @Override
        public String nombre() {
            return "Reina";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/reinab.png" : "img/reinan.png";
        }
    },
    ALFIL {
        @Override
        public String nombre() {
            return "Alfil";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/alfilb.png" : "img/alfiln.png";
        }
    },
    CABALLO {
        @Override
        public String nombre() {
            return "Caballo";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/caballob.png" : "img/caballon.png";
        }
    },
    TORRE {
        @Override
        public String nombre() {
            return "Torre";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/torreb.png" : "img/torren.png";
        }
    },
    PEON {
        @Override
        public String nombre() {
            return "Peón";
        }

        @Override
        public String imagen(ColorPieza color) {
            return BLANCO.equals(color) ? "img/peonb.png" : "img/peonn.png";
        }
    };
}

interface ITipoPieza {
    String nombre();
    String imagen(ColorPieza color);
}