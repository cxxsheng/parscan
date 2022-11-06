package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public interface ExpressionHandlerCallback {

    void handleSymbol(Symbol s, boolean isHit);

    void handleExpression(Expression e, boolean isHit);

    boolean broadcastHit(Symbol terminalSymbol);

    String getTag();

}
