package com.cxxsheng.parscan.core.data.unit;

import com.cxxsheng.parscan.core.data.ConditionalBlock;

import java.util.ArrayList;
import java.util.List;

public class ExpressionListWithPrevs {
  private List<Expression> prevs = null;
  private Expression lastExpression;

  private ConditionalBlock block = null;

  public boolean hasPreExpression(){
    return prevs != null && prevs.size() > 0;
  }

  public ExpressionListWithPrevs(Expression e){
    lastExpression = e;
  }

  public void addPrev(Expression pre){
    if (prevs == null)
      prevs = new ArrayList<>();
    prevs.add(pre);
  }

  public void addPrevs(List<Expression> prevs){
    if (this.prevs == null)
      this.prevs = new ArrayList<>();
    this.prevs.addAll(prevs);
  }

  public List<Expression> getPrevs() {
    return prevs;
  }

  public Expression getLastExpression() {
    return lastExpression;
  }


  public Expression getLastPrev() {
    return prevs.get(prevs.size() - 1);
  }

  public List<Expression> toExpressionList(){
    if (prevs == null)
      return lastExpression.wrapToRealList();
    else
    {
      List<Expression> list = new ArrayList<>(prevs);
      list.add(lastExpression);
      return list;
    }
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    if (hasPreExpression()){
      for (Expression prev : prevs){
        sb.append(prev).append('\n');
      }
    }
    sb.append(lastExpression).append('\n');
    return sb.toString();
  }

  public ConditionalBlock getBlock() {
    return block;
  }

  public void setBlock(ConditionalBlock block) {
    this.block = block;
  }
}
