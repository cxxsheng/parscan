package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Symbol;

public class PointSymbol implements Symbol {
    private final Expression exp;
    private final Symbol v;

    public PointSymbol(Expression exp, Symbol v) {
        this.exp = exp;
        this.v = v;
    }

    boolean isFunc(){
        return v instanceof CallFunc;
    }
}
