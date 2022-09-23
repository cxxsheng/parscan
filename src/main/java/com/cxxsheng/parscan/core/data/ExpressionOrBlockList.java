package com.cxxsheng.parscan.core.data;


import com.cxxsheng.parscan.antlr.exception.JavaMethodExtractorException;
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

  public static final ExpressionOrBlockList EMPTY_INSTANCE = new ExpressionOrBlockList();


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



  public static ExpressionOrBlockList Init(ExpressionOrBlock unit){
      if (unit==null || !unit.isTaint())
        return EMPTY_INSTANCE;
      return new ExpressionOrBlockList(unit);
  }


  public static ExpressionOrBlockList Init(int type, List<ExpressionOrBlock> content){
      if (content==null)
        return EMPTY_INSTANCE;

      List<ExpressionOrBlock> newArray = new ArrayList<>();

      for (ExpressionOrBlock e : content){
        if (e.isTaint())
          newArray.add(e);
      }
      if (newArray.isEmpty())
        return EMPTY_INSTANCE;
      return new ExpressionOrBlockList(type, newArray);
  }


  public static ExpressionOrBlockList wrap(int type, List<ExpressionOrBlock> expressions) {
      return new ExpressionOrBlockList(type, expressions);
  }

  public ExpressionOrBlockList addOne(ExpressionOrBlock unit){

      if (this==EMPTY_INSTANCE)
        return unit.wrapToList();

      if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;

      content.add(unit);
      return this;
  }

  public ExpressionOrBlockList addExpressionList(ExpressionOrBlockList t){
      if (this  == EMPTY_INSTANCE)
          return t;
      else {
        type = t.type | type;
        content.addAll(t.content);
        return this;
      }
  }


   public boolean isOneExp(){
     return content.size()==1 && type==PURE_EXPRESSION;
   }

   public boolean isOneBlock(){
      return content.size()==1 && type==PURE_BLOCK;
   }

   public boolean isEmpty(){
      return this==EMPTY_INSTANCE || content==null || content.size()==0;
   }


}
