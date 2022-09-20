package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;

public class WhileBlock extends Block {
  private final Expression boolExp;
  private final boolean isDoWhile;
  public WhileBlock(Coordinate x, Expression boolExp, boolean isDoWhile, ExpressionOrBlockList content) {
    super(x, content);
    this.boolExp = boolExp;
    this.isDoWhile = isDoWhile;
  }


}
