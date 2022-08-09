package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import java.util.List;

public class ConditionStatement implements Statement {
  private Condition condition;
  private List<ExpressionOrStatement> body;

  public Condition getCondition() {
    return condition;
  }

  public List<ExpressionOrStatement> getBody() {
    return body;
  }
}
