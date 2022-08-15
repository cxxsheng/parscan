package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;


//make a wrap for exp and statement to make type consistence
//in AST node
public class ExpressionOrStatement {
  private Expression expression;
  private Statement statement;

  boolean isExpression = false;

  Statement getStatement(){
    return statement;
  }

  public Expression getExpression() {
    return expression;
  }

  public ExpressionOrStatement(Expression expression){
    this.expression = expression;
    isExpression = true;
  }

  public ExpressionOrStatement(Statement statement){
    this.statement = statement;
  }

  public boolean isExpression(){
    return isExpression;
  }

}
