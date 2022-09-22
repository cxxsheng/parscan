package com.cxxsheng.parscan.core.data.unit;

public class JavaType {

  private final Primitive primitive;
  private final String ObjectName;
  private final boolean isArray;

  public JavaType(Primitive primitive, boolean isArray) {
    this.primitive = primitive;
    ObjectName = null;
    this.isArray = isArray;
  }

  public JavaType(String ObjectName, boolean isArray) {
    this.primitive = null;
    this.ObjectName = ObjectName;
    this.isArray = isArray;
  }

  public boolean isArray() {
    return isArray;
  }

  private boolean isObject(){
    return this.ObjectName!=null;
  }

  public Primitive getPrimitive() {
    return primitive;
  }

  static JavaType parseJavaTypeString(String name,boolean isArray){
    Primitive primitive = Primitive.nameOf(name);
    if (primitive != null)

      return new JavaType(primitive, isArray);
    else
      // isObject instead of primitive
      return new JavaType(name, isArray);
  }
}
