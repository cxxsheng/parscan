package com.cxxsheng.parscan.core;

public interface Statement {
    default ExpressionOrStatement wrapToExpOrStatement(){
       return new ExpressionOrStatement(this);
    }
}
