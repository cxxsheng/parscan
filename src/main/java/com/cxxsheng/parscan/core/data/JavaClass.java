package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.data.unit.Parameter;
import com.cxxsheng.parscan.core.data.unit.symbol.VarDeclaration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaClass {

  private final Path filePath;
  private final String name;
  private final List<String> interfaceName;
  private final String superClassName;
  private final List<FunctionImp> methods = new ArrayList();
  private final List<VarDeclaration> varList = new ArrayList<>();
  private ExpressionOrBlockList staticDomain;


  public JavaClass(String name, List<String> interfaceName, String superClassName, Path filePath) {
      this.name = name;
      this.interfaceName = interfaceName;
      this.superClassName = superClassName;
      this.filePath = filePath;
  }

  //for anonymous class
  public JavaClass(Path filePath){
      this.name = null;
      interfaceName = null;
      superClassName = null;
      this.filePath = filePath;
  }

  public void addMethod(FunctionImp imp){
    imp.setJavaClass(this);
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
      if (imp.getFunDec().getName().equals(name))
        return imp;
    }
    return null;
  }

  public FunctionImp getFunctionImpByFullName(String name, String [] typeList){
      for (FunctionImp imp : methods){
        if (imp.getFunDec().getName().equals(name)) {
          List<Parameter> ps = imp.getFunDec().getParams();
          if (ps.size() == typeList.length)
          {
            for (int i = 0; i < typeList.length; i++){
              Parameter p = ps.get(0);
              if(!p.getType().toString().equals(typeList[i]))
                return null;
            }
            return imp;
          }
         }
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

  public List<FunctionImp> getMethods() {
    return methods;
  }

  public String getName() {
    return name;
  }
}
