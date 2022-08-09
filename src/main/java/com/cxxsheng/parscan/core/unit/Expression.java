package com.cxxsheng.parscan.core.unit;


public class Expression {
   private Symbol left = null;
   private Symbol right = null;
   private Operator op = Operator.NONE;


   public Expression(Symbol left, Symbol right, Operator op){
     this.left = left;
     this.right = right;
     this.op = op;
   }

    public Symbol getL(){
      return left;
    }

    public Symbol getR(){
      return right;
    }

    public Operator getOp() {
      return op;
    }

    boolean isAssign(){
      return op.isAssign();
    }
}
