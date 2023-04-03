package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import java.util.List;

public class FunctionImp {

    private FunctionDeclaration functionDec;

    private JavaClass javaClass ;
    private ExpressionOrBlockList body = null;


    public FunctionImp(Coordinate c, JavaType javaType, String name, List<Parameter> params){
      this.functionDec = new FunctionDeclaration(javaType, name, params, c);
    }

    public FunctionImp(FunctionDeclaration func){
      this.functionDec = func;
    }

    public FunctionDeclaration getFunDec() {
      return functionDec;
    }

    public ExpressionOrBlockList getBody() {
      return body;
    }

    public void setBody(ExpressionOrBlockList body) {
      this.body = body;
    }

    public void setFuncDec(FunctionDeclaration function) {
        this.functionDec = function;
      }

    public Coordinate getPosition(){
      return functionDec.getCoordinate();
    }


    @Override
    public String toString() {
      return functionDec.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this.toString().equals(o.toString());
    }


    public JavaClass getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(JavaClass javaClass) {
        this.javaClass = javaClass;
    }
}