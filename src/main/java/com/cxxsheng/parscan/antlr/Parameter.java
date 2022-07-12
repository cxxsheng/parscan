package com.cxxsheng.parscan.antlr;

public class Parameter {

  private final String type;
  private final String name;

  public Parameter(String type, String name){
    this.type = type;
    this.name = name;
  }

  public String getType() {
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
