package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

// exp[exp]
public class ArrayGetSymbol extends Symbol {
    private final Expression array;
    private final Expression index;

    public ArrayGetSymbol(Expression array, Expression index) {
        this.array = array;
        this.index = index;

    }

    @Override
    public final boolean isConstant() {
      return false;
    }

    @Override
    public boolean isTerminal() {
      return false;
    }

    @Override
    public String toString() {
      return array +
             "[" + index +
             ']';
    }
}
