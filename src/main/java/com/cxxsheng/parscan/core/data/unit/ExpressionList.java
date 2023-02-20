package com.cxxsheng.parscan.core.data.unit;

import java.util.ArrayList;
import java.util.List;

public class ExpressionList {
  private List<Expression> prevs = null;
  private Expression lastExpression;



  public boolean hasPreExpression(){
    return prevs != null && prevs.size() > 0;
  }

  public ExpressionList(Expression e){
    lastExpression = e;
  }

  public void addPrev(Expression pre){
    if (prevs == null)
      prevs = new ArrayList<>();
    prevs.add(pre);
  }

  public void addPrevs(List<Expression> prevs){
    if (prevs == null)
      prevs = new ArrayList<>();
    prevs.addAll(prevs);
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
}
