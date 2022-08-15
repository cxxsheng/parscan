package com.cxxsheng.parscan.core.unit;

import com.cxxsheng.parscan.core.Coordinate;

import java.util.List;

public class FunctionDeclaration extends Symbol{

    private final List<Parameter> params;
    private final Coordinate coordinate;

    /**
     * @param type type of function return value
     * @param name function name
     * @param paramList function param list
     * @param coordinate the coordinate of function
     */
    public FunctionDeclaration(String type, String name, List<Parameter> paramList, Coordinate coordinate) {
        super(type, name);
        this.params = paramList;
        this.coordinate = coordinate;
    }

    public List<Parameter> getParams() {
        return params;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
