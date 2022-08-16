package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class StringSymbol implements Symbol {
    private final String value;

    public StringSymbol(String value) {
        this.value = value;
    }
}
