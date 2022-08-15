package com.cxxsheng.parscan.core;

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
    return "Coordinate{" +
           "" + line +
           ", " + column +
           '}';
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

}
