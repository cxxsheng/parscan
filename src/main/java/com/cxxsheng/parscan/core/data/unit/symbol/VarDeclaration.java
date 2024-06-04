package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Operator;
import com.cxxsheng.parscan.core.data.unit.Symbol;


//name to value map
public class VarDeclaration extends Symbol {


  private final IdentifierSymbol name;
  private final JavaType type;
  private ExpressionListWithPrevs expressions;

  public VarDeclaration(IdentifierSymbol name, JavaType type, ExpressionListWithPrevs expressions) {
    this.name = name;
    this.type = type;
    this.expressions = expressions;
  }

  public VarDeclaration(IdentifierSymbol name, JavaType type) {
    this.name = name;
    this.type = type;
    this.expressions = null;
  }

  public boolean hasValue(){
    return this.expressions != null;
  }

  private void updateExpressions(ExpressionListWithPrevs expressions){
    this.expressions = expressions;
  }


  @Override
  public String toString() {
    return name.toString();
  }

  public ExpressionListWithPrevs getExpressions() {
    return expressions;
  }

  public Symbol getLastExpValue(){
    if (expressions == null){
      System.out.println();
    }
    assert expressions.getLastExpression().getOp() == Operator.AS;
    return expressions.getLastExpression().getRight();
  }

  public JavaType getType() {
    return type;
  }

  public IdentifierSymbol getName() {
    return name;
  }
}
