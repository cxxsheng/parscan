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



  private ExpressionOrBlockList(){
    content = new ArrayList<>();
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
    return new ExpressionOrBlockList();
  }

  public static ExpressionOrBlockList Init(ExpressionOrBlock unit){
      if (unit==null || !unit.isTaint())
        return InitEmptyInstance();
      return new ExpressionOrBlockList(unit);
  }


  public static ExpressionOrBlockList Init(int type, List<ExpressionOrBlock> content){
      if (content==null)
        return InitEmptyInstance();

      //List<ExpressionOrBlock> newArray = new ArrayList<>();


      //filter all untainted expression
      //for (ExpressionOrBlock e : content){
      //  if (e.isTaint())
      //  {
      //    newArray.add(e);
      //  }
      //}
      //if (newArray.isEmpty())
      //  return InitEmptyInstance();
      //return new ExpressionOrBlockList(type, newArray);
      return new ExpressionOrBlockList(type, content);
  }


  public ExpressionOrBlockList addOne(ExpressionOrBlock unit){

      if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;

      content.add(unit);
      return this;
  }

  public ExpressionOrBlockList combine(ExpressionOrBlockList t){

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
      return content==null || content.size()==0;
   }


    public ExpressionOrBlock last(){
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
