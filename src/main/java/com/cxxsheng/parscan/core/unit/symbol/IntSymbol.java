package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

// int value, include long value
public class IntSymbol implements Symbol {
    private final long value;

    public IntSymbol(long value){
        this.value = value;
    }
}
