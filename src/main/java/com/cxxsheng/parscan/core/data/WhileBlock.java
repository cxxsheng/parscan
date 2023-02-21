package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;

public class WhileBlock extends Block {
  private final ExpressionListWithPrevs boolExp;
  private final boolean isDoWhile;
  public WhileBlock(Coordinate x, ExpressionListWithPrevs boolExp, boolean isDoWhile, ExpressionOrBlockList content) {
      super(x, content);
      this.boolExp = boolExp;
      this.isDoWhile = isDoWhile;
  }


}
