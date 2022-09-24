package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public class ConditionalExpression extends Symbol {

    private final Expression cond;
    private final Expression left;
    private final Expression right;


    public ConditionalExpression(Expression cond, Expression left, Expression right) {
        this.cond = cond;
        this.left = left;
        this.right = right;

        //broadcast taint
        if (cond.isTaint()||left.isTaint()||right.isTaint()){
          taint();
        }
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

    @Override
    public void taint() {
      isTaint = true;
    }


    @Override
    public final boolean isConstant() {
      return false;
    }

  @Override
  public String toString() {
    return ""+ cond +
           "?" + left +
           ":" + right;
  }
}
