package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class BoolSymbol implements Symbol {
    private final boolean value;

    public BoolSymbol(boolean value) {
        this.value = value;
    }
}
