package com.conargon.jchess.util;

public final class IntegerWrapper {

    int value;

    public IntegerWrapper(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int inc() {
        return ++value;
    }

    public int dec() {
        return --value;
    }

    public int inc(int plus) {
        value += plus;
        return value;
    }

    public int dec(int minus) {
        value -= minus;
        return value;
    }

    public int inc(IntegerWrapper plus) {
        value += plus.getValue();
        return value;
    }

    public int dec(IntegerWrapper minus) {
        value -= minus.getValue();
        return value;
    }
}
