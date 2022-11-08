package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

public class FloatSymbol extends Symbol {
    private final String value;

    public FloatSymbol(String value) {
        this.value = value;
    }


    @Override
    public final boolean isConstant() {
        return true;
      }

    @Override
    public boolean isTerminal() {
      return true;
    }

    @Override
      public String toString() {
        return ""+value;
      }
      public String getValue() {
        return value;
      }
}


