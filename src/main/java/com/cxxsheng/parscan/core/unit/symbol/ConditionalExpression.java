package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Symbol;

public class ConditionalExpression extends Symbol {

    private final Expression cond;
    private final Expression left;
    private final Expression right;


    public ConditionalExpression(Expression cond, Expression left, Expression right) {
        this.cond = cond;
        this.left = left;
        this.right = right;
    }

    public Expression getCond() {
        return cond;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    // ConditionalExpression cannot taint because it is always at expression's right
    @Override
    public void taint() {

    }
}
