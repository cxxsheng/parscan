package com.cxxsheng.parscan.core.data.unit;

public abstract class Symbol {

    protected boolean isTaint = false;

    public boolean isTaint(){
      return isTaint;
    }

    abstract public void taint(); //just taint
    public Expression toExp(){
        return new Expression(this);
    }

    abstract public boolean isConstant();
}
