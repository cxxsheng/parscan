package com.cxxsheng.parscan.core.data.unit;

import com.cxxsheng.parscan.core.iterator.RuntimeValue;

public abstract class Symbol implements RuntimeValue {

   public Expression toExp(){
        return new Expression(this);
    }

   abstract public boolean isConstant();

   abstract public boolean isTerminal();
}
