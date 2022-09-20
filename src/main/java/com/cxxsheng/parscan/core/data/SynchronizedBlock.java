package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;

public class SynchronizedBlock extends Block {

  private final Expression e;
  public SynchronizedBlock(Coordinate x, Expression e, ExpressionOrBlockList content) {
    super(x, content);
    this.e = e;
  }
}
