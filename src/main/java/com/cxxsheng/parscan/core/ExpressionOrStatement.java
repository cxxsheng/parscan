package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;

public class ExpressionOrStatement {
  private Expression expression;
  private Statement statement;


  Statement getStatement(){
    return statement;
  }

  public Expression getExpression() {
    return expression;
  }
}
