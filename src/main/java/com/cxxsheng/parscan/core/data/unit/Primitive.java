package com.cxxsheng.parscan.core.data.unit;

public enum Primitive {
  
  BYTE("byte"),
  SHORT("short"),
  INT("int"),
  LONG("long"),
  FLOAT("float"),
  DOUBLE("double"),
  BOOL("boolean"),
  CHAR("char");

  private String name;

  Primitive(String name){
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static Primitive nameOf(String name){
    for (Primitive op : Primitive.values()){
      if(op.getName().equals(name))
        return op;
    }
    return null;
  }
}

