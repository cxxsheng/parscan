package com.cxxsheng.parscan.core.data;

import com.cxxsheng.parscan.core.Coordinate;
import com.cxxsheng.parscan.core.data.unit.Expression;

public class Statement implements ExpressionOrBlock {

    public final static int RETURN_STATEMENT = 0;
    public final static int THROW_STATEMENT = 1;
    public final static int BREAK_STATEMENT = 2;
    public final static int CONTINUE_STATEMENT = 3;

    private final int type;
    private final Expression exp;
    private final Coordinate x;
    public Statement(Coordinate x, int type, Expression exp) {
      this.type = type;
      this.exp = exp;
      this.x = x;
    }

}
