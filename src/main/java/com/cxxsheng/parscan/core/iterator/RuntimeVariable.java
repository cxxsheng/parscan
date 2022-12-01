package com.cxxsheng.parscan.core.iterator;

public class RuntimeVariable {
  private final int[] mark;
  private final String name;


  public RuntimeVariable(int[] mark, String name) {
    this.mark = mark;
    this.name = name;
  }


  public String getName() {
    return name;
  }

  public int[] mark() {
    return mark;
  }
}
