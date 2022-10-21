package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;
import java.util.ArrayList;
import java.util.List;

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

  public FunctionImp getFunctionImpByName(String name){
      for (FunctionImp imp : methods){
        if (imp.toString().equals(name))
          return imp;
      }
      return null;
  }

  public VarDeclaration getVarDeclarationByName(String name){
    for (VarDeclaration v : varList)
    {
      if (v.toString().equals(name))
        return v;
    }
    return null;
  }
}
