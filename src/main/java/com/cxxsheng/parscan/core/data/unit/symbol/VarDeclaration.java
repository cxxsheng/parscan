package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Symbol;


//name to value map
public class VarDeclaration extends Symbol {


  private final IdentifierSymbol name;
  private final JavaType type;
  private ExpressionListWithPrevs value;

  public VarDeclaration(IdentifierSymbol name, JavaType type, ExpressionListWithPrevs value) {
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

  private void updateValue(ExpressionListWithPrevs value){
    this.value = value;
  }


  @Override
  public String toString() {
    return name.toString();
  }

  public ExpressionListWithPrevs getValue() {
    return value;
  }


  public JavaType getType() {
    return type;
  }

  public IdentifierSymbol getName() {
    return name;
  }
}
