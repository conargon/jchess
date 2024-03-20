package com.conargon.jchess.tipo;

public enum ColorPieza implements IColorPieza {
    BLANCO {
        @Override
        public String nombre() {
            return "Blancas";
        }

        @Override
        public boolean esBlanca() {
            return true;
        }

        @Override
        public boolean esNegra() {
            return false;
        }

        @Override
        public ColorPieza contraria() {
            return NEGRO;
        }
    },
    NEGRO {
        @Override
        public String nombre() {
            return "Negras";
        }

        @Override
        public boolean esBlanca() {
            return false;
        }

        @Override
        public boolean esNegra() {
            return true;
        }

        @Override
        public ColorPieza contraria() {
            return BLANCO;
        }
    };

    public static boolean esBlanca(ColorPieza colorPieza) {
        return ColorPieza.BLANCO.equals(colorPieza);
    }

    public static boolean esNegra(ColorPieza colorPieza) {
        return ColorPieza.NEGRO.equals(colorPieza);
    }
}

interface IColorPieza {
    String nombre();
    boolean esBlanca();
    boolean esNegra();
    ColorPieza contraria();
}
