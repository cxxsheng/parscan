package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class IdentifierSymbol implements Symbol {

    private final String value;

    public IdentifierSymbol(String value) {
        this.value = value;
    }
}
