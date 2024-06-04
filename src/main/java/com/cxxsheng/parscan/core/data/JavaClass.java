package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.AntlrCore;
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

  private List<JavaClass> innerClasses = new ArrayList<>();

  public JavaClass(AntlrCore core, String name, List<String> interfaceName, String superClassName, Path filePath) {
      this.name = name;
      this.interfaceName = interfaceName;
      this.superClassName = superClassName;
      this.filePath = filePath;
      core.addGlobal(this);
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
    return getFunctionImpByFullName(name, typeList, null);
  }

  public FunctionImp getFunctionImpByFullName(String name, String [] typeList, FunctionImp curImp){
      for (FunctionImp imp : methods){
        if (imp.getFunDec().getName().equals(name)) {
          List<Parameter> ps = imp.getFunDec().getParams();
          if (ps == null){
            System.out.println();
          }
          if (ps.size() == typeList.length)
          {
            //check type is matched
            boolean typedMismatch = false;
            for (int i = 0; i < typeList.length; i++){
              Parameter p = ps.get(i);
              //ignore * type
              if (typeList[i].equals("*"))
                continue;
              if(!p.getType().toString().equals(typeList[i]))
              {
                typedMismatch=true;
                break;
              }
            }
            if (!typedMismatch && (imp != curImp))
              return imp;
            //goto next function
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

  public void addInnerClass(JavaClass innerClass){
    innerClasses.add(innerClass);
  }

  public List<JavaClass> getInnerClasses() {
    return innerClasses;
  }

  public JavaClass findInnerClassByName(String name){
    for (JavaClass innerClass : innerClasses){
      if(innerClass.getName().equals(name))
        return innerClass;
    }
    return null;
  }

  public String getSuperClassName() {
    return superClassName;
  }

  public List<String> getInterfaceName() {
    return interfaceName;
  }

  public boolean containsInterfaceName(String name){
    return interfaceName.contains(name);
  }


}
