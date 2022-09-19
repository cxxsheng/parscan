package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public class PointSymbol extends Symbol {
    private final Expression exp;
    private final Symbol v;

    public PointSymbol(Expression exp, Symbol v) {
        this.exp = exp;
        this.v = v;
    }

    boolean isFunc(){
        return v instanceof CallFunc;
    }

    @Override
    public void taint() {

    }
}
