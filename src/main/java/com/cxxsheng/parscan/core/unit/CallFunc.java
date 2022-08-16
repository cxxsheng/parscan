package com.cxxsheng.parscan.core.unit;

import com.cxxsheng.parscan.core.Coordinate;

import java.util.List;

public class CallFunc {
    private final String funcName;
    private final List<String> params;

    private final Coordinate coordinate;

    public CallFunc(String funcName, List<String> params, Coordinate coordinate) {
        this.funcName = funcName;
        this.params = params;
        this.coordinate = coordinate;
    }



    public String getFuncName() {
        return funcName;
    }

    public List<String> getParams() {
        return params;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
