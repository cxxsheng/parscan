package com.cxxsheng.parscan.core;

public class Coordinate {
  final private int line, column;

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
    return "Coordinate{" +
           "" + line +
           ", " + column +
           '}';
  }
}
