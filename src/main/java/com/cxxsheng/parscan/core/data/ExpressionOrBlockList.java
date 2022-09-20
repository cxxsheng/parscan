package com.cxxsheng.parscan.core.data;


import com.cxxsheng.parscan.core.data.unit.Expression;
import java.util.ArrayList;
import java.util.List;

//wrapExpressions
public class ExpressionOrBlockList {

  public static final int MIXED = 3;            // 0b11
  public static final int PURE_BLOCK = 2;       // 0b10
  public static final int PURE_EXPRESSION = 1;  // 0b01
  private int type = 0;

  private final List<ExpressionOrBlock> content;

  public ExpressionOrBlockList(){
    content = new ArrayList<>();
  }

  public ExpressionOrBlockList(int type, List<ExpressionOrBlock> content) {
    this.type = type;
    this.content = content;
  }

  public ExpressionOrBlockList(ExpressionOrBlock unit){
    if (unit instanceof Expression)
      this.type |= PURE_EXPRESSION;
    else if (unit instanceof Block)
      this.type |= PURE_BLOCK;
    content = new ArrayList<>();
    content.add(unit);
  }

  public static ExpressionOrBlockList wrap(int type, List<ExpressionOrBlock> expressions) {
    return new ExpressionOrBlockList(type, expressions);
  }

  public void addOne(ExpressionOrBlock unit){
    if (unit instanceof Expression)
      this.type |= PURE_EXPRESSION;
    else if (unit instanceof Block)
      this.type |= PURE_BLOCK;
    content.add(unit);
  }

  public void addExpressionList(ExpressionOrBlockList t){
    type = t.type | type;
    content.addAll(t.content);
  }


   public boolean isOneExp(){
     return content.size()==1 && type==PURE_EXPRESSION;
   }

   public boolean isOneBlock(){
      return content.size()==1 && type==PURE_BLOCK;
   }

   public boolean isEmpty(){
      return content==null || content.size()==0;
   }
}
