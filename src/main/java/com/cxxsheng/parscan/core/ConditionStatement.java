package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import java.util.List;

public class ConditionStatement extends Statement {
  private final Condition condition;

  public ConditionStatement(Condition condition, List<Expression> e) {
    super(e);
    this.condition = condition;
  }

  public Condition getCondition() {
    return condition;
  }


}
