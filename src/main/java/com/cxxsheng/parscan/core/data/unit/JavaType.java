package com.cxxsheng.parscan.core.data.unit;

import java.util.Objects;

public class JavaType {

  private final Primitive primitive;
  private final String ObjectName;
  private final boolean isArray;

  private static final JavaType VOID = new JavaType();

  public static final JavaType INT = new JavaType(Primitive.INT, false);
  public static final JavaType FLOAT = new JavaType(Primitive.FLOAT, false);
  public static final JavaType BOOL = new JavaType(Primitive.BOOL, false);
  public static final JavaType BYTE = new JavaType(Primitive.BYTE, false);
  public static final JavaType SHORT = new JavaType(Primitive.SHORT, false);
  public static final JavaType DOUBLE = new JavaType(Primitive.DOUBLE, false);
  public static final JavaType CHAR = new JavaType(Primitive.CHAR, false);
  public static final JavaType LONG = new JavaType(Primitive.LONG, false);

  private JavaType(){
    primitive = null;
    ObjectName = null;
    isArray = false;
  }

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

  public boolean notArray() {
    return !isArray;
  }

  public boolean isObject(){
    return notArray() && this.ObjectName!=null;
  }

  public boolean isPrimitive(){
    return  notArray() && primitive!=null;
  }

  private boolean hasPrimitive(){
    return primitive != null;
  }

  private boolean hasObjectName(){
    return this.ObjectName != null;
  }

  public Primitive getPrimitive() {
    return primitive;
  }

  public static JavaType parseJavaTypeString(String name, boolean isArray){
    Primitive primitive = Primitive.nameOf(name);
    if (primitive != null)

      return new JavaType(primitive, isArray);
    else
      // is object instead of primitive
      return new JavaType(name, isArray);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JavaType javaType = (JavaType)o;

    //they are not all array or all not array
    if (this.isArray ^ javaType.isArray){
      return false;
    }

    //they are all object then compare object name
    if (this.hasObjectName() && javaType.hasObjectName()){
      return this.ObjectName.equals(javaType.ObjectName);
    }

    //they are all primitive then compare primitive type

    if (this.hasPrimitive() && javaType.hasPrimitive()){
      return this.primitive == javaType.primitive;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(ObjectName);
  }

  @Override
  public String toString() {
    String isArrayFix = isArray ? "[]":"";
    if (hasObjectName())
      return this.ObjectName+isArrayFix;
    else
      return primitive.getName()+isArrayFix;
  }

  static public JavaType getVOID() {
    return VOID;
  }

  public boolean isVoid(){
    return this == VOID;
  }

  public boolean isArray() {
    return isArray;
  }
}
