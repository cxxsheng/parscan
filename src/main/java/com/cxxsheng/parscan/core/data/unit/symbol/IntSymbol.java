package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

// int value, include long value
public class IntSymbol extends Symbol {
    private final long value;

    public IntSymbol(long value){
        this.value = value;
    }

    // constant cannot be tainted
    @Override
    public void taint() {

    }
}
