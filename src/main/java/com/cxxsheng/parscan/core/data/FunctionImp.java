package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.data.unit.Parameter;
import java.util.List;

public class FunctionImp {

    private FunctionDeclaration functionDec;

    private Block body = null;

    public FunctionImp(Coordinate c,String type, String name, List<Parameter> params){
      this.functionDec = new FunctionDeclaration(type, name, params, c);
    }

    public FunctionImp(FunctionDeclaration func){
      this.functionDec = func;
    }

    public FunctionDeclaration getFunDec() {
      return functionDec;
    }

    public Block getBody() {
      return body;
    }

    public void setFuncDec(FunctionDeclaration function) {
      this.functionDec = function;
    }

    public Coordinate getPosition(){
      return functionDec.getCoordinate();
    }


    public void addExpOrBlock2Body(ExpressionOrBlock eb){
      if (body == null){
        throw new RuntimeException("body has not initialiazed!");
      }
      body.addExpressionOrBlock(eb);
    }

}