package com.cxxsheng.parscan.core.iterator;

import com.cxxsheng.parscan.core.data.unit.Operator;

public class Condition {

  public Operator op;

  public static final Condition TRUE = new Condition();
  public static final Condition FALSE = new Condition();




  private Condition(){
  }

  public Condition(Operator op){
    this.op = op;

  }


}
