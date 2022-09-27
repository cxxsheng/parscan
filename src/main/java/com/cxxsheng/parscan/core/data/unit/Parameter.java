package com.cxxsheng.parscan.core.data.unit;


public class Parameter {

  private final JavaType type;
  private final String name;

  public Parameter(JavaType type, String name){
    this.type = type;
    this.name = name;
  }

  public JavaType getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return  "" +
            type + ' ' +
            name ;
  }


}
