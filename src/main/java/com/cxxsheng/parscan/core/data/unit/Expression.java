package com.cxxsheng.parscan.core.data.unit;


import java.util.ArrayList;
import java.util.List;

public class Expression {
    private final Operator op;
    private final TerminalSymbol left;
    private final TerminalSymbol right;
    private final Symbol symbol;
    private final boolean isUnary;
    public Expression(Operator op, TerminalSymbol left, TerminalSymbol right, boolean isUnary) {
      this.op = op;
      this.left = left;
      this.right = right;
      this.symbol = null;
      this.isUnary = isUnary;
    }

    public Expression(Operator op, TerminalSymbol left, TerminalSymbol right) {
      this(op,left,right,false);
    }

    public Expression(Symbol symbol){
      this.op = null;
      this.left = null;
      this.right = null;
      this.symbol = symbol;
      this.isUnary = false;
    }

    public boolean isSymbol(){
      return symbol != null;
    }

    public Symbol getSymbol() {
      return symbol;
    }

    public List<Expression> wrapToRealList(){
      List<Expression> list = new ArrayList<>();
      list.add(this);
      return list;
    }

    public ExpressionList wrapToList(){
      ExpressionList list = new ExpressionList(this);
      return list;
    }
}
