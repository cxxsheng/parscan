package com.cxxsheng.parscan.core.z3;

public interface LastExpressionCallback {
  //justify the latest expression like bool or assert expression must be true
  ExprWithTypeVariable justify(ExprWithTypeVariable last);
}
