package com.cxxsheng.parscan.core;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class Coordinate {
  final private int line, column;

  public static Coordinate UN_INIT = new Coordinate(-1,-1);

  public Coordinate(int line, int column) {
    this.line = line;
    this.column = column;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }

  @Override
  public String toString() {
    return "{" +
           "" + line +
           ", " + column +
           '}';
  }

  static public Coordinate createFromCtx(ParserRuleContext ctx){
    return new Coordinate(ctx.start.getLine(), ctx.start.getCharPositionInLine());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coordinate that = (Coordinate) o;
    return line == that.line && column == that.column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(line, column);
  }

  static public Coordinate initFromToken(Token token){
      return new Coordinate(token.getLine(), token.getCharPositionInLine());
  }
}
