package com.cxxsheng.parscan.core.data.unit;

//tmp pointer
public class TmpSymbol extends TerminalSymbol {
  private final int tmpId;
  private final Expression e;

  public TmpSymbol( int tmpId, Expression e) {
    this.tmpId = tmpId;
    this.e = e;
  }

  public Expression getExpression() {
    return e;
  }

  public String getName(){
    return "$t"+tmpId;
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();
    sb.append("@T").append(tmpId).append('{');
    sb.append(e.toString()).append('}');
    return sb.toString();
  }
}
