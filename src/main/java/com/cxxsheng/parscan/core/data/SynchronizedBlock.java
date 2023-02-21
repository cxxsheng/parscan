package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;

public class SynchronizedBlock extends Block {

  private final ExpressionListWithPrevs e;
  public SynchronizedBlock(Coordinate x, ExpressionListWithPrevs e, ExpressionOrBlockList content) {
      super(x, content);
      this.e = e;
  }
}
