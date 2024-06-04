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

  private ExpressionOrBlock owner;

  private ExpressionOrBlockList(){
    content = null;
  }


  private ExpressionOrBlockList( int type, List<ExpressionOrBlock> content){
    this.type = type;
    this.content = content;
  }

  private ExpressionOrBlockList(ExpressionOrBlock unit){

    if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;
      content = new ArrayList<>();
      unit.setDomain(this);
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


  public ExpressionOrBlockList add(List<Expression> units){
    if (units == null || units.size() <= 0 )
      return EmptyInstance;
    final ExpressionOrBlockList pointer;
    if (this == EmptyInstance)
    {
      pointer = new ExpressionOrBlockList(PURE_EXPRESSION, new ArrayList<>());
    }else
    {
      pointer = this;
    }

    pointer.type |= type;
    for (ExpressionOrBlock unit: units){
      pointer.content.add(unit);
      unit.setDomain(pointer);
    }
    return pointer;
  }

  public ExpressionOrBlockList addOne(ExpressionOrBlock unit){

      if (this == EmptyInstance)
        return new ExpressionOrBlockList(unit);

      if (unit instanceof Expression)
        this.type |= PURE_EXPRESSION;
      else if (unit instanceof Block)
        this.type |= PURE_BLOCK;

      content.add(unit);
      unit.setDomain(this);
      return this;
  }

  public ExpressionOrBlockList combine(ExpressionOrBlockList t){

    if (t.isEmpty())
      return this;

    if (this.isEmpty())
      return t;
    type |= t.type;
    content.addAll(t.content);
    for (ExpressionOrBlock e : t.getContent()){
      e.setDomain(this);
    }
    return this;
  }


  public ExpressionOrBlock getLast(){
    if (isEmpty())
      return null;
    else
      return content.get(content.size() - 1);
  }

  public ExpressionOrBlock getHead(){
    if (isEmpty())
      return null;
    else return content.get(0);
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


      StringBuilder sb = new StringBuilder();
      if (content != null)
        for (ExpressionOrBlock ep : content){
          sb.append(ep);
          sb.append('\n');
        }
      return sb.toString();
    }

    public List<ExpressionOrBlock> getContent() {
      return content;
    }

    public void setOwner(Block owner) {
      this.owner = owner;
    }

    public ExpressionOrBlock getOwner() {
      return owner;
    }

    public int size(){
      if (isEmpty())
        return 0;
      else
        return content.size();
    }

    public ExpressionOrBlock get(int i){

      if (isEmpty())
        return null;
      if (i >= size() || i < 0){
        return null;
      }
      return content.get(i);
    }


    public long getLength(){
      if (isEmpty())
          return 0;
      int result = 0;
      for (ExpressionOrBlock eb : content)
      {
          if (eb instanceof Block){
              result += ((Block) eb).getContent().getLength();
          }else{
              result++;
          }
      }
      return result;
  }
}
