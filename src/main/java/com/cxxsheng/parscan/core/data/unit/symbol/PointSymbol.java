package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class PointSymbol extends Symbol {
    private final TerminalSymbol exp;
    //ID or CallFunc
    private final Symbol v;

    public PointSymbol(TerminalSymbol exp, Symbol v) {
        this.exp = exp;
        this.v = v;
    }

    public boolean isFunc(){
        return v instanceof CallFunc;
    }

    @Override
     public String toString() {

        return ""+ exp.toString() + "." +v.toString();
    }

    public TerminalSymbol getExp() {
    return exp;
  }

    public Symbol getV() {
    return v;
  }
}
