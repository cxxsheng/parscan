package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import java.util.List;

public interface ExpressionHandlerCallback {

    RuntimeValue handleSymbol(Symbol s, boolean isHit);

    RuntimeValue handleExpression(Expression e, boolean isHit);

    List<RuntimeValue> handleBinExpression(List<RuntimeValue> left, Operator op, List<RuntimeValue> right, boolean isHit);

    boolean broadcastHit(Symbol terminalSymbol);

    String getTag();

}
