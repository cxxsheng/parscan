package com.cxxsheng.parscan.core.data.unit;

import com.cxxsheng.parscan.core.Coordinate;

//just booleanExpression and wait for z3 to handle
//has coordinate to locate code
public class BooleanExpression extends Expression {

    private final Coordinate coordinate;

    public BooleanExpression(Symbol left, Expression right, Operator op, Coordinate coordinate) {
        super(left, right, op);
        this.coordinate = coordinate;
    }

    @Override
    public boolean isAssign() {
        return false;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
