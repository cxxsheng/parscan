package com.cxxsheng.parscan.core.data.unit.symbol;

import com.cxxsheng.parscan.core.data.unit.Expression;
import com.cxxsheng.parscan.core.data.unit.JavaType;
import com.cxxsheng.parscan.core.data.unit.Primitive;
import com.cxxsheng.parscan.core.data.unit.Symbol;

public class Creator extends Symbol {

    final Expression size;
    private final JavaType javaType;

    public Creator(Expression size, JavaType javaType) {
      this.size = size;
      this.javaType = javaType;


      //broadcast
      if (size.isTaint())
        taint();
    }

    @Override
    public void taint() {
      isTaint = true;
    }

    @Override
    public boolean isConstant() {
      return false;
    }
}
