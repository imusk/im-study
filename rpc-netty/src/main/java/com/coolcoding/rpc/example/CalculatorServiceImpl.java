package com.coolcoding.rpc.example;

public class CalculatorServiceImpl implements CalculatorService {
    @Override
    public Double add(Double n1, Double n2) {
        return n1 + n2;
    }

    @Override
    public Double sub(Double n1, Double n2) {
        return n1 - n2;
    }

    @Override
    public Double mul(Double n1, Double n2) {
        return n1 * n2;
    }

    @Override
    public Double div(Double n1, Double n2) {
        return n1 / n2;
    }
}
