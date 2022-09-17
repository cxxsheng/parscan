package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Symbol;

public class NullSymbol extends Symbol {

    private static NullSymbol INSTANCE = new NullSymbol();
    // constant cannot be tainted
    @Override
    public void taint() {

    }

   public static  NullSymbol Init(){
      return INSTANCE;
    }
}
