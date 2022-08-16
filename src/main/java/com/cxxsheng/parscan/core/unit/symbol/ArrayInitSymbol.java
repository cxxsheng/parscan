package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class ArrayInitSymbol implements Symbol {
    private final String value;

    public ArrayInitSymbol(String value) {
        this.value = value;
    }
}
