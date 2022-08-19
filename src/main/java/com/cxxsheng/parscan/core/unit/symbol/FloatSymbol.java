package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class FloatSymbol implements Symbol {
    private final double value;

    public FloatSymbol(double value) {
        this.value = value;
    }
}
