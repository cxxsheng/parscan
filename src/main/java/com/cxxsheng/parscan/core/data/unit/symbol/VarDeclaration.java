package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Symbol;


//name to value map
public class VarDeclaration extends Symbol {


  private final IdentifierSymbol name;
  private final JavaType type;
  private Expression value;

  public VarDeclaration(IdentifierSymbol name, JavaType type, Expression value) {
    this.name = name;
    this.type = type;
    this.value = value;
  }

  public VarDeclaration(IdentifierSymbol name, JavaType type) {
    this.name = name;
    this.type = type;
    this.value = null;
  }

  public boolean hasValue(){
    return this.value != null;
  }

  private void updateValue(Expression value){
    this.value = value;
  }


  @Override
  public boolean isConstant() {
    return false;
  }


  @Override
  public String toString() {
    return name.toString();
  }

  public Expression getValue() {
    return value;
  }

  @Override
  public boolean isTerminal() {
    return false;
  }
}
