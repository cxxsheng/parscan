package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Symbol;
import com.cxxsheng.parscan.core.data.unit.TerminalSymbol;

public class Creator extends Symbol {

    final TerminalSymbol size;
    private final JavaType javaType;

    public Creator(TerminalSymbol size, JavaType javaType) {
      this.size = size;
      this.javaType = javaType;
    }

  @Override
    public String toString() {

      return "new " + javaType.toString() + "["+size.toString()+"]";
    }
}
