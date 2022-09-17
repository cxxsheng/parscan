package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Symbol;

// exp[exp]
public class ArrayGetSymbol extends Symbol {
    private final Expression array;
    private final Expression index;

    public ArrayGetSymbol(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }


  @Override
  public void taint() {
      isTaint = true;
  }


}
