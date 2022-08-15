package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.FunctionDeclaration;
import com.cxxsheng.parscan.core.unit.Parameter;

import java.util.ArrayList;
import java.util.List;

public class FuncStatement {

    private FunctionDeclaration functionDec;

    private List<ExpressionOrStatement> body = new ArrayList<>();

    public FuncStatement(FunctionDeclaration func, List<ExpressionOrStatement> body){
        this();
        this.functionDec = func;
        this.body = body;
    }

    public FuncStatement(){
    }

    public FunctionDeclaration getFunDec() {
        return functionDec;
    }

    public List<ExpressionOrStatement> getBody() {
        return body;
    }

    public void setFuncDec(FunctionDeclaration function) {
        this.functionDec = function;
    }


    public void initFunctionDeclaration(String type, String name, List<Parameter> params, Coordinate c){
        functionDec = new FunctionDeclaration(type, name, params, c);
    }

    public void addExpression(Expression exp){
        body.add(exp.wrapToExpOrStatement());
    }

    public void addStatement(Statement statement){
        body.add(statement.wrapToExpOrStatement());
    }

    public Coordinate getPosition(){
        return functionDec.getCoordinate();
    }

}
