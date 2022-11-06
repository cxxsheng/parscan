package com.cxxsheng.parscan.core.data.unit;

import com.cxxsheng.parscan.core.Coordinate;

import java.util.List;
import java.util.Objects;

public class FunctionDeclaration {

    private final List<Parameter> params;
    private final Coordinate coordinate;
    private final String name;
    private final JavaType retType;
    /**
     * @param retType type of function return value
     * @param name function name
     * @param paramList function param list
     * @param x the coordinate of function
     */
    public FunctionDeclaration(JavaType retType, String name, List<Parameter> paramList, Coordinate x) {
        this.params = paramList;
        this.coordinate = x;
        this.name = name;
        this.retType = retType;
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
      return this.retType == null;
    }


    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer("");
      if (retType == null)
      //Cannot ignore ; to skip construct function because it does not have return value
        ;
      else if (retType.isVoid())
        sb.append("void ");
      else
        sb.append(retType).append(' ');


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

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      FunctionDeclaration that = (FunctionDeclaration)o;
      return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(name);
    }

    public JavaType getReturnType() {
      return retType;
    }

    public boolean hasParameter(){
      return params != null && params.size() > 0;
    }

    public Parameter getParameterByIndex(int i){
      if (params!=null && params.size() > i){
        return params.get(i);
      }
      return null;
    }
}
