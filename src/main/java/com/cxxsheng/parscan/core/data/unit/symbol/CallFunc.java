package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.JavaClass;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;
import java.util.List;

public class CallFunc extends Symbol {
    private final String funcName;
    private final List<TerminalSymbol> params;


    private final Coordinate coordinate;
    private final boolean isConstructFunc;

    private JavaClass extraClass = null;
    public CallFunc(Coordinate x, String funcName, List<TerminalSymbol> params) {
        this(x, funcName,params,false);
    }

    public CallFunc(Coordinate x, String funcName, List<TerminalSymbol> params, boolean isConstructFunc) {
      this.funcName = funcName;
      this.params = params;
      this.coordinate = x;
      this.isConstructFunc = isConstructFunc;
    }




    public String getFuncName() {
        return funcName;
    }

    public List<TerminalSymbol> getParams() {
        return params;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }


  @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public String toString() {
      if (params==null){
        return funcName+"()";
      }
      StringBuilder sb = new StringBuilder("(");
      for (TerminalSymbol e : params){
          sb.append(e);
          sb.append(", ");
      }
      if (params.size() != 0)
        sb.delete(sb.length()-2, sb.length());
      sb.append(")");
      return funcName+sb;
    }

    public boolean isConstructFunc() {
      return isConstructFunc;
    }

    public boolean hasExtraClass(){
      return extraClass != null;
    }

    public void setExtraClass(JavaClass extraClass) {
      this.extraClass = extraClass;
    }

    public JavaClass extraClass() {
    return extraClass;
  }
}
