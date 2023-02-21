package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class NullSymbol extends TerminalSymbol {

    private static NullSymbol INSTANCE = new NullSymbol();

    public static NullSymbol INIT() {
      return INSTANCE;
    }



    @Override
    public String toString() {
      return "null";
    }

}
