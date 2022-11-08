package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

// int value, include long value
public class IntSymbol extends Symbol {
    private final long value;

    public IntSymbol(long value){
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

    public long getValue() {
      return value;
    }
}
