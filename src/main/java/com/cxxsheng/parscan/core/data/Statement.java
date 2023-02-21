package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.ExpressionListWithPrevs;

public class Statement extends ExpressionOrBlock {

    public final static int RETURN_STATEMENT = 0;
    public final static int THROW_STATEMENT = 1;
    public final static int BREAK_STATEMENT = 2;
    public final static int CONTINUE_STATEMENT = 3;
    public final static int ASSERT_STATEMENT = 4;

    private final int type;
    private final ExpressionListWithPrevs exp;
    private final Coordinate x;
    public Statement(Coordinate x, int type, ExpressionListWithPrevs exp) {
      this.type = type;
      this.exp = exp;
      this.x = x;
    }

    public int getType() {
      return type;
    }

    public ExpressionListWithPrevs getExp() {
      return exp;
    }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("");
    switch (type)
    {
      case RETURN_STATEMENT:
        sb.append("return ").append(exp);
        break;
      case THROW_STATEMENT:
        sb.append("throw ").append(exp);
        break;
      case BREAK_STATEMENT:
        sb.append("break");
        break;
      case CONTINUE_STATEMENT:
        sb.append("continue");
        break;
      case ASSERT_STATEMENT:
        sb.append("assert").append(exp);
        break;

    }
    return sb.toString();
  }
}
