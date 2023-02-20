package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class FloatSymbol extends TerminalSymbol {
    private final String value;

    public FloatSymbol(String value) {
        this.value = value;
    }


    @Override
      public String toString() {
        return ""+value;
      }
      public String getValue() {
        return value;
      }
}


