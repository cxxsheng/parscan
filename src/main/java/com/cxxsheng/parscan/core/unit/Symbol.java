package com.cxxsheng.parscan.core.unit;


public class Symbol implements Unit {
  //represent a symbol in java
  private final String type;
  private final String name;
  public Symbol(String type, final String name){
    this.type = type;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getSymbolType() {
    return type;
  }


}
