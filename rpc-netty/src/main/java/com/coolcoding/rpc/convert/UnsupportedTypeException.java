package com.coolcoding.rpc.convert;

public class UnsupportedTypeException extends RuntimeException {
    public UnsupportedTypeException() {
    }

    @Override
    public String toString() {
        return "unsupported type";
    }
}
