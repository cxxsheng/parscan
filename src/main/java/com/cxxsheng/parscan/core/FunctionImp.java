package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.unit.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FunctionImp {

    private FunctionDeclaration functionDec;



    private List<Statement> body = new ArrayList<>();

    public FunctionImp(FunctionDeclaration func, List<Statement> body){
        this();
        this.functionDec = func;
        this.body = body;
    }

    public FunctionImp(){
    }

    public FunctionDeclaration getFunDec() {
        return functionDec;
    }

    public List<Statement> getBody() {
        return body;
    }

    public void setFuncDec(FunctionDeclaration function) {
        this.functionDec = function;
    }


    public void initFunctionDeclaration(String type, String name, List<Parameter> params, Coordinate c){
        functionDec = new FunctionDeclaration(type, name, params, c);
    }

    public void addExpression(Expression exp){

    }

    public void addStatement(Statement statement){
        body.add(statement);
    }

    public Coordinate getPosition(){
        return functionDec.getCoordinate();
    }

}
