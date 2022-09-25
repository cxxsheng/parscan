package com.cxxsheng.parscan.core.data;

import java.util.List;

public class JavaClass {

  private final String name;
  private final List<String> interfaceName;
  private final String superClassName;

  public JavaClass(String name, List<String> interfaceName, String superClassName) {
    this.name = name;
    this.interfaceName = interfaceName;
    this.superClassName = superClassName;
  }
}
