package com.cxxsheng.parscan.core.data.unit;

public abstract class Symbol {
    public Expression toExp(){
      return new Expression(this);
    }
}
