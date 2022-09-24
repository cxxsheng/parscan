package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;

public class NullSymbol extends Symbol {

    private static NullSymbol INSTANCE = new NullSymbol();
    // constant cannot be tainted
    @Override
    public void taint() {

    }

    @Override
    public final boolean isConstant() {
      return true;
    }


    public static  NullSymbol Init(){
        return INSTANCE;
      }

    @Override
    public String toString() {
      return "null";
    }
}
