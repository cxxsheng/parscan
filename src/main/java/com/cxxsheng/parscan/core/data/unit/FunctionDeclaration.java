package com.cxxsheng.parscan.core.data.unit;

import com.cxxsheng.parscan.core.Coordinate;

import java.util.List;

public class FunctionDeclaration {

    private final List<Parameter> params;
    private final Coordinate coordinate;
    private final String name;
    private final JavaType typeType;
    /**
     * @param type type of function return value
     * @param name function name
     * @param paramList function param list
     * @param x the coordinate of function
     */
    public FunctionDeclaration(JavaType type, String name, List<Parameter> paramList, Coordinate x) {
        this.params = paramList;
        this.coordinate = x;
        this.name = name;
        this.typeType = type;
    }

    public FunctionDeclaration(String name, List<Parameter> paramList, Coordinate x) {

      //Construct function does not have return value
      this(null, name, paramList, x);
    }


    public List<Parameter> getParams() {
        return params;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void addSymbolToLocalList(Symbol symbol){

    }

    public boolean isConstruct(){
      return this.typeType == null;
    }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("");
    if (typeType == null)
    //Cannot ignore ; to skip construct function because it does not have return value
      ;
    else if (typeType.isVoid())
      sb.append("void ");
    else
      sb.append(typeType).append(' ');


    sb.append(name).append('(');

    if (params != null) {
      for (Parameter e : params){
        sb.append(e.toString());
        sb.append(", ");
      }
      if (params.size()!=0)
        sb.delete(sb.length()-2, sb.length());
    }
    sb.append(')');
    return sb.toString();
  }
}
