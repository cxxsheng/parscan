package com.cxxsheng.parscan.core.unit;

public class ArrayGetSymbol {

    private final Expression name;
    private final Expression value;

    public ArrayGetSymbol(Expression name, Expression value) {
        this.name = name;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public Expression getName() {
        return name;
    }
}
