package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

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
