package com.cxxsheng.parscan.core.unit;

import java.util.Objects;

public abstract class Symbol {

    protected boolean isTaint = false;

    public boolean isTaint(){
      return isTaint;
    }

    abstract public void taint(); //just taint
    public Expression toExp(){
        return new Expression(this);
    }
}
