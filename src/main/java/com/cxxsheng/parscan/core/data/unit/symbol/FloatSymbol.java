package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

public class FloatSymbol extends Symbol {
    private final double value;

    public FloatSymbol(double value) {
        this.value = value;
    }

    //constant cannot be tainted
    @Override
    public void taint() {

    }

    @Override
    public final boolean isConstant() {
      return true;
    }

    @Override
    public String toString() {
      return ""+value;
    }
}
