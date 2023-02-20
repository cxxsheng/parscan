package com.cxxsheng.parscan.core.iterator;

import java.util.ArrayList;
import java.util.List;

public class RuntimeFunction extends RuntimeValue {
  private final String functionName;
  private final List<RuntimeValue> vs = new ArrayList<>();

  public RuntimeFunction(String functionName) {this.functionName = functionName;}

  public void addParam(RuntimeValue v){
    vs.add(v);
  }

}
