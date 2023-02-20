package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

// exp[exp]
public class ArrayGetSymbol extends Symbol {
    private final TerminalSymbol array;
    private final TerminalSymbol index;

    public ArrayGetSymbol(TerminalSymbol array, TerminalSymbol index) {
        this.array = array;
        this.index = index;

    }


    @Override
    public String toString() {
      return array +
             "[" + index +
             ']';
    }
}
