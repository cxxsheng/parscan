package com.cxxsheng.parscan.core;

import com.cxxsheng.parscan.core.unit.Expression;
import java.util.List;

public class Statement {
  private final List<Expression> expressions;

  public Statement(List<Expression> expressions) {this.expressions = expressions;}
}
