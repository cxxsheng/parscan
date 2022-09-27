package com.cxxsheng.parscan.core.data;


import com.cxxsheng.parscan.core.data.unit.Expression;
import java.util.ArrayList;
import java.util.List;

//wrapExpressions represent a domain which
//contains variables valid or invalid
public class ExpressionOrBlockList {


  public static final int MIXED = 3;            // 0b11
  public static final int PURE_BLOCK = 2;       // 0b10
  public static final int PURE_EXPRESSION = 1;  // 0b01
  private int type = 0;

  private final List<ExpressionOrBlock> content;

  private static final ExpressionOrBlockList EmptyInstance = new ExpressionOrBlockList();

  private ExpressionOrBlockList(){
    content = null;
  }

  private ExpressionOrBlockList(int type, List<ExpressionOrBlock> content) {

      this.type = type;
      this.content = content;
  }


  private ExpressionOrBlockList(ExpressionOrBlock unit){

    if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;
      content = new ArrayList<>();
      content.add(unit);
  }


  public static ExpressionOrBlockList InitEmptyInstance(){
    return EmptyInstance;
  }

  public static ExpressionOrBlockList Init(ExpressionOrBlock unit){
      if (unit==null)
        return InitEmptyInstance();
      return new ExpressionOrBlockList(unit);
  }


  public static ExpressionOrBlockList Init(int type, List<ExpressionOrBlock> content){
      if (content==null || content.size() == 0)
        return InitEmptyInstance();
      return new ExpressionOrBlockList(type, content);
  }


  public ExpressionOrBlockList addOne(ExpressionOrBlock unit){

      if (this == EmptyInstance)
        return new ExpressionOrBlockList(unit);

      if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;

      content.add(unit);
      return this;
  }

  public ExpressionOrBlockList combine(ExpressionOrBlockList t){

      if (t == EmptyInstance)
        return this;

      if (this == EmptyInstance)
        return t;

      type = t.type | type;
      content.addAll(t.content);
      return this;
  }


   public boolean isOneExp(){
     return content.size()==1 && type==PURE_EXPRESSION;
   }

   public boolean isOneBlock(){
      return content.size()==1 && type==PURE_BLOCK;
   }

   public boolean isEmpty(){
      return this== EmptyInstance || content==null || content.size()==0;
   }


    public ExpressionOrBlock last(){
      if (isEmpty())
        return null;
      return content.get(content.size() - 1);
    }

  @Override
  public String toString() {


    StringBuilder sb = new StringBuilder("");
    for (ExpressionOrBlock ep:content){
      sb.append(ep);
      sb.append('\n');
    }
    return sb.toString();
  }
}
