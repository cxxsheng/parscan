package com.cxxsheng.parscan.core.data.unit;


import com.cxxsheng.parscan.core.data.ExpressionOrBlock;

public class Expression extends ExpressionOrBlock {
   private final Symbol symbol;
   private Expression left = null;
   private Expression right = null;
   private Operator op = Operator.NONE;


   private boolean isUnitary = false;

   public Expression(Expression left, Expression right, Operator op){
     this.left = left;
     this.right = right;
     this.op = op;
     this.symbol = null;
   }

   //use to wrap symbol
   public Expression(Symbol symbol){
       this.symbol = symbol;
   }

   public Expression(Symbol left, Expression right, Operator op){
       this.left = new Expression(left);
       this.right = right;
       this.op = op;
       this.symbol = null;
   }

   public Expression(Expression left, Expression right, Operator op, boolean isUnitary){
       this(left, right, op);
       this.isUnitary = isUnitary;
   }


    public Expression(Symbol left, Symbol right, Operator op){
        this.left = new Expression(left);
        this.right = new Expression(right);
        this.op = op;
        this.symbol = null;
    }

    public Expression(Expression left, Symbol right, Operator op){
        this.left = left;
        this.right = new Expression(right);
        this.op = op;
        this.symbol = null;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Expression getL(){
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

    public boolean isTerminal(){
        return  symbol != null;
    }

    public boolean isUnitary(){
        return isUnitary;
   }

    @Override
    public String toString() {

       if (isTerminal())
         return symbol.toString();

       else
         return "("+this.left + this.getOp().getName() + this.right+ ")";
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public boolean hasRight(){
        return right != null;
    }

    public boolean hasLeft(){
        return left != null;
    }

    public boolean leftCanDecompose(){
        if (hasLeft()){
            if (left.isTerminal()) // it is symbol
            {
              Symbol symbol = left.symbol;
              return !symbol.isTerminal(); // some symbol can be decomposed
            }
        }else
          return true;
        return false;
    }

    public boolean rightCanDecompose(){
      if (hasRight()){
        if (right.isTerminal())
        {
          Symbol symbol = right.symbol; // symbol also can be decomposed
          return !symbol.isTerminal();
        }else
          return true;
      }
      return false;
    }


    public boolean isTerminalSymbol(){
     return isTerminal() && symbol.isTerminal();
  }
}
