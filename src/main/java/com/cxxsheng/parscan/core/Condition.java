package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import com.microsoft.z3.Expr;

public class Condition {


  private Expression expression;

  public Condition(Coordinate coord, Expression expression){
    this.expression = expression;
  }


}
