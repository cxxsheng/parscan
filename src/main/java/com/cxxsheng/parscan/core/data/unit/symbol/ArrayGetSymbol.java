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

        if (array.isTaint()||index.isTaint())
          taint();
    }



    @Override
    public void taint() {
        isTaint = true;
    }

    @Override
    public final boolean isConstant() {
      return false;
    }
}
