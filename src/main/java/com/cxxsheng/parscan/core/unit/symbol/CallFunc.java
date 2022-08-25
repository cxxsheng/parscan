package com.cxxsheng.parscan.core.unit.symbol;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.unit.Expression;
import com.cxxsheng.parscan.core.unit.Symbol;

import java.util.List;

public class CallFunc implements Symbol {
    private final String funcName;
    private final List<Expression> params;

    private final Coordinate coordinate;

    public CallFunc(String funcName, List<Expression> params, Coordinate coordinate) {
        this.funcName = funcName;
        this.params = params;
        this.coordinate = coordinate;
    }



    public String getFuncName() {
        return funcName;
    }

    public List<Expression> getParams() {
        return params;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
