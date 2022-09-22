package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.Symbol;

import java.util.List;

public class CallFunc extends Symbol {
    private final String funcName;
    private final List<Expression> params;


    private final Coordinate coordinate;

    public CallFunc(Coordinate x, String funcName, List<Expression> params) {
        this.funcName = funcName;
        this.params = params;
        this.coordinate = x;

        //broadcast taint
        for (Expression e : params){
          if (e.isTaint()){
            taint();
            break;
          }
        }

        //taint all param? maybe
        //fixme
        for (Expression e : params){
            e.taint();
        }
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

    @Override
    public void taint() {
      isTaint = true;
    }

    @Override
    public final boolean isConstant() {
      return false;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

}
