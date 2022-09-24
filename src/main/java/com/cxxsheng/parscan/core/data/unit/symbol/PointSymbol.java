package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public class PointSymbol extends Symbol {
    private final Expression exp;
    private final Symbol v;

    public PointSymbol(Expression exp, Symbol v) {
        this.exp = exp;
        this.v = v;
        //fixme
        if (exp.isTaint())
          taint();
    }

    boolean isFunc(){
        return v instanceof CallFunc;
    }

    @Override
    public void taint() {
      isTaint=true;
    }

    @Override
    public final boolean isConstant() {
      return false;
    }

  @Override
  public String toString() {

    return ""+ exp.toString() + "." +v.toString();
  }
}
