package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public interface ExpressionHandlerCallback {

    RuntimeValue handleSymbol(Symbol s, boolean isHit);

    RuntimeValue handleExpression(Expression e, boolean isHit);

    RuntimeValue handleBinExpression(Expression left, Operator op, Expression right, boolean isHit);

    RuntimeValue handleAssignExpression(String left, RuntimeValue v, boolean isHit);

    boolean broadcastHit(Symbol terminalSymbol);

    String getTag();

}
