package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.symbol.IdentifierSymbol;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaClass {

  private final String name;
  private final List<String> interfaceName;
  private final String superClassName;
  private final List<FunctionImp> methods = new ArrayList();
  private final List<VarDeclaration> varList = new ArrayList<>();
  private ExpressionOrBlockList staticDomain;

  public JavaClass(String name, List<String> interfaceName, String superClassName) {
      this.name = name;
      this.interfaceName = interfaceName;
      this.superClassName = superClassName;
  }

  //for anonymous class
  public JavaClass(){
      this.name = null;
      interfaceName = null;
      superClassName = null;
  }

  public void addMethod(FunctionImp imp){
      methods.add(imp);
  }

  public void addClassVariable(VarDeclaration var){
      varList.add(var);
  }


  public void setStaticDomain(ExpressionOrBlockList staticDomain) {
    this.staticDomain = staticDomain;
  }

  @Override
  public String toString() {
    return name;
  }

  public List<VarDeclaration> getVarList() {
    return varList;
  }
}
