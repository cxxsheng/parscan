package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

// int value, include long value
public class IntSymbol extends TerminalSymbol {
    private final long value;

    public IntSymbol(long value){
        this.value = value;
    }


    @Override
      public String toString() {

        return ""+value;
      }

    public long getValue() {
      return value;
    }
}
