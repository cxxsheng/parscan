package com.cxxsheng.parscan.core.unit;


import com.cxxsheng.parscan.core.ExpressionOrStatement;

public class Expression {
   private Symbol left = null;
   private Expression right = null;
   private Operator op = Operator.NONE;


   public Expression(Symbol left, Expression right, Operator op){
     this.left = left;
     this.right = right;
     this.op = op;
   }


    public Expression(Symbol left, Symbol right, Operator op){
        this.left = left;
        this.right = new Expression(right, (Expression) null, null);
    }

    public Symbol getL(){
      return left;
    }

    public Expression getR(){
      return right;
    }

    public Operator getOp() {
      return op;
    }

    public boolean isAssign(){
      return op.isAssign();
    }

    public ExpressionOrStatement wrapToExpOrStatement(){
        return new ExpressionOrStatement(this);
    }



    public boolean isTerminal(){
        return left != null && right == null && op == Operator.NONE;
    }

}
